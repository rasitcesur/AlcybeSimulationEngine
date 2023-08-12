package alcybe.simulation;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import alcybe.data.BigDate;
import alcybe.data.InMemoryDatabase;
import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.ArrivalEvent;
import alcybe.simulation.events.ClearBufferEvent;
import alcybe.simulation.events.UserEvent;
import alcybe.simulation.events.ContinuousEvent;
import alcybe.simulation.events.DiscreteEvent;
import alcybe.simulation.events.DispatchingEvent;
import alcybe.simulation.events.DispatchingFinalizationEvent;
import alcybe.simulation.events.ExitEvent;
import alcybe.simulation.events.OperationEvent;
import alcybe.simulation.events.OperationFinalizationEvent;
import alcybe.simulation.events.TransferEvent;
import alcybe.simulation.events.TransportEvent;
import alcybe.simulation.events.UserDefinedEvent;
import alcybe.simulation.events.utils.EventList;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.events.utils.SimulationEventCollection;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.ProcessResult;
import alcybe.simulation.model.RealtimeTaskContainer;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.GoalFunction;
import alcybe.simulation.events.SimulationEvent;
import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.SimulationObject;
import alcybe.simulation.objects.Source;
import alcybe.simulation.objects.TransferableElement;
import licensing.LicenseAgent;
import licensing.LicenseInfo;
import alcybe.simulation.types.DispatchingStrategy;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.io.FileAgent;
import alcybe.utils.io.FileAgent.FileCreateMode;

public class Engine<T> implements Runnable{

	// TODO Exception handling is required in run
	
	static LicenseInfo licenseInfo;
	
	static {
		 try {
			licenseInfo=LicenseAgent.checkLicense("license.lic");
		} catch (Exception e) {
			licenseInfo=null;
		}
	}

	public EventList eventList=null; 
	public final HashMap<String, RealtimeTaskContainer> realTimeTaskInfo = 
			new HashMap<>();
	public final List<SimulationObject> objectList = new LinkedList<SimulationObject>();
	public boolean showTrace = true;
	public boolean saveGanttFile=false;
	protected BigDate simulationTime, lastExit;
	public BigDate simulationCycleTime = new BigDate(0);
	protected BigDate simulationEndTime,simulationBeginTime;
	protected final SortedSet<SimulationEventCollection> eventIndex = new TreeSet<>();
	public final List<EngineBlock> engineBlocks = new ArrayList<EngineBlock>();
	public EngineBlock[] blockArray = null;
	//public int multithreadThreshold=100000;
	protected final int procCount=Runtime.getRuntime().availableProcessors();
	public InMemoryDatabase database=new InMemoryDatabase();
	public GoalFunction<T> goalFunction;
	
	public OperationFinalizationEvent addOperationFinalizationEvent(TransferableElement entity, TaskBufferContainer container, BigDate eventDate) {
		entity.operationFinalizationEvent = new OperationFinalizationEvent(container, eventDate);
		entity.addConcurrentOperationFinalizationEvent(entity.operationFinalizationEvent);
		addEvent(entity.operationFinalizationEvent);
		return entity.operationFinalizationEvent;
	}
	
	public Object callFunction(String functionName, Object... parameters) {
		return eventList.callFunction(functionName, parameters);
	}
	
	public void transfer(TransferableElementContainer transferableElementContainer, TaskNode current, TaskNode target) {
		transfer(transferableElementContainer, current, new TaskNode[] { target }, DispatchingStrategy.RealTime);
	}
	
	public void transfer(TransferableElementContainer transferableElementContainer, TaskNode current, TaskNode target, BigDate transferBegin) {
		transfer(transferableElementContainer, current, new TaskNode[] { target }, DispatchingStrategy.RealTime, transferBegin);
	}
	
	public void transfer(TransferableElementContainer transferableElementContainer, TaskNode current, TaskContainer target, BigDate transferBegin) {
		transfer(transferableElementContainer, current, new TaskNode[] { new TaskNode(target) }, DispatchingStrategy.RealTime, transferBegin);
	}
	
	public void transfer(TransferableElementContainer transferableElementContainer, TaskNode current, TaskNode[] targets, DispatchingStrategy dispatchingStrategy) {
		transfer(transferableElementContainer, current, targets, dispatchingStrategy, getSimulationTime());
	}
	
	public void transfer(TransferableElementContainer transferableElementContainer, TaskNode current, TaskNode[] targets, DispatchingStrategy dispatchingStrategy, 
			BigDate transferBegin) {
		
		Process process = new Process(dispatchingStrategy, targets);
		TaskNode taskNode=process.getWorkcenter();
		
		TaskBufferContainer container=new TaskBufferContainer(transferableElementContainer, taskNode, process);
		
		addEvent(new TransferEvent(container, transferBegin));
		addEvent(new ClearBufferEvent(container, transferBegin));
	}
	
	public void initDefaultBlocks() {
		//0 Arrival
		insertBlock(new EngineBlock(this) {
			
			protected void initArrival(Task source, TransferableElement entity, Long aSeq,
					BigDate time, List<IterationResult> result) 
							throws ClassNotFoundException, IOException {
				
				/**
				 * "cloneEntityFromTemplate" function clones entity from the template 
				 * to provide a new entity for the simulation model.
				 * */
				TransferableElement te=(TransferableElement) entity;
				TransferableElement tEntity=cloneEntityFromTemplate(entity,aSeq, te.assemblySetCount*aSeq+te.assemblySequence,
						getNextEntityUniqueSequence());
				TransferableElementContainer transferableElementContainer=new TransferableElementContainer(tEntity);
				synchronized (engine) {
					//new EventContainer(transferableElementContainer, new TaskContainer(source), w)
					engine.addEvent(new DispatchingEvent(transferableElementContainer, time));
				}
				
				
				result.add(new IterationResult(tEntity, source, 
						time, time, EventType.ArrivalEvent));
			}
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {

				ArrivalEvent aEvent=(ArrivalEvent)event;
				
				Source source = (Source) aEvent.getSource();
				
				event.cancel |= fireAtTheBeginningEvents(0, new EventContainer(new TaskNode(new TaskContainer(source))));
				
				if(event.cancel)
					return null;
				
				List<IterationResult> result=new ArrayList<>();
				final BigDate simulationTime2=event.getBeginDate();				
				
				
				long arrivalSeq=getNextEntityArrivalSequence();
				source.arrivalCount++;
				
				final TransferableElement[] elementBuffer = source.getElementBuffer();
				
				/**if(elementBuffer.length>multithreadThreshold) {
					ExecutorService es=Executors.newWorkStealingPool(procCount);
					final int len=elementBuffer.length/procCount;
					final CyclicBarrier barrier=new CyclicBarrier(procCount+1);
					for (int i1 = 0; i1 < procCount; i1++) {
						final int i=i1;
						es.execute(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								int begin=i*len;
								int end=Math.min(begin+len, elementBuffer.length);
								for (int j = begin; j < end; j++) {
									TransferableElement entity=elementBuffer[j];
									try {
										initArrival(source, entity, arrivalSeq, simulationTime2, result);
									} catch (ClassNotFoundException | IOException e) {
										e.printStackTrace();
									}
									try {
										barrier.await();
									}catch(Exception ex) {}
								}
							}
						});
					}

					try {
						barrier.await();
					}catch(Exception ex) {}

				}else*/ 
				for (TransferableElement entity : elementBuffer) 	
					initArrival(source, entity, arrivalSeq, simulationTime2, result);	
				
				
				ProcessResult evtRes = source.process(null, null, simulationTime2);
				if(source.arrivalCount<source.arrivalTreshold)
					engine.addEvent(new ArrivalEvent(source, evtRes.completionDate));
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//1 Dispatching
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				DispatchingEvent dEvent=(DispatchingEvent)event;
				TransferableElementContainer elementContainer = dEvent.getElementContainer();
				TransferableElement tEntity = elementContainer.getElement();
				
				
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), new EventContainer(elementContainer));
				
				if(event.cancel)
					return null;
				
				
				TransferInfo transferInfo=tEntity.getNextTarget();
				if(transferInfo!=null) {
					tEntity.concurrentEventCount = transferInfo.getProcessContainers().size();
					for(Process w:transferInfo.processContainer) {
						engine.addEvent(new DispatchingFinalizationEvent(elementContainer, w, event.getBeginDate()));
					}
				} else {
					//TODO Fire exit event;
					/**TaskNode c=new TaskNode(b.entityContainer, null, null);
					container.setTaskNode(c);
					engine.addEvent(new DiscreteEvent(7, container, completionDate, 
							EventType.ExitEvent));*/
					engine.addEvent(new ExitEvent(elementContainer, tEntity.getCurrentNode(), event.getBeginDate()));
				}
					
				BigDate date=event.getBeginDate();
				
				List<IterationResult> result=new ArrayList<>();
				result.add( new IterationResult(tEntity, null, 
						date, date, event.eventType));
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//2 DispatchingFinalization
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				if(event.cancel)
					return null;
				
				DispatchingFinalizationEvent dEvent=(DispatchingFinalizationEvent)event;
				TransferableElementContainer elementContainer = dEvent.getElementContainer();
				TransferableElement tEntity = elementContainer.getElement();
				Process process = dEvent.getProcess();
				
				/**
				 * Workload calculation for detecting the bottleneck.
				 * 
				 * */
				for(TransferInfo tf : tEntity.targetList) {
					List<TaskNode> wsList=tf.getProcessContainer().workcenters;
					int wsCount=wsList.size();
					for( TaskNode tn:wsList) {
						try {
							OperationInfo opInfo = tn.getOperationTimeInfo();
							BigDate opTime=new BigDate(opInfo.getOperationTime().getTime());
							opTime.divide(wsCount); 
							tn.getTask().totalWorkload.add(opTime);
						} catch (Exception e) {}
					}
				}		

				/**
				 * Calculating virtual queue time for real-time dispatching
				 * */
				OperationInfo info;
				for (TaskNode t : process.workcenters) {
					info=t.getOperationTimeInfo();
					Task target = t.taskContainer.getElement();
					target.virtualQueueTime.add(info.operationTime);
				}
				
				
				/**
				 * Dispatching
				 * */
				TaskNode taskNode = process.getWorkcenter();
				
				EventContainer container=new EventContainer(elementContainer, process, taskNode);
				container.setProcess(process);
				container.setTaskNode(taskNode);
				
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), container);
				
				if(event.cancel)
					return null;
				
				Task task=taskNode.getTaskContainer().getElement();
				if(taskNode.operationTime!=null)
					task.totalWorkload.add(taskNode.operationTime.get());
				if(taskNode.setupTime!=null)
					task.totalWorkload.add(taskNode.setupTime.get());
				List<IterationResult> result=new ArrayList<>();
				
				BigDate date = event.getBeginDate();
				
				result.add( new IterationResult(tEntity, task, 
						date, date, event.eventType));
				
				//engine.addEvent(new TransferEvent(elementContainer, process, taskNode, simulationTime));
				
				TransportEvent tEvent=new TransportEvent(elementContainer, process, taskNode, event.getBeginDate(), new BigDate(1000));
				tEvent.transferEvent=new TransferEvent(elementContainer, process, taskNode, event.getBeginDate());
				engine.addEvent(tEvent);
				
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//3 Transport
		insertBlock(new EngineBlock(this) {

			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				// TODO Auto-generated method stub
				TransportEvent tEvent=(TransportEvent) event;
				
				engine.addEvent(tEvent.transferEvent);
				return null;
			}
			
		});
		
		//4 Transfer
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				/**
				 * TODO Container.transferTime kullanarak transfer s�resi hesapla
				 * TODO operationCompletionTimeList kullan�lm�yorsa kald�r! kald�r.
				 * */
				
				TransferEvent tEvent = (TransferEvent)event;
				TaskBufferContainer bufferContainer = tEvent.getTaskBufferContainer();
				TaskNode taskNode = bufferContainer.getTaskNode();
				TransferableElementContainer elementContainer = bufferContainer.getTransferableElementContainer();
				Process process = bufferContainer.getProcess();
				
				List<IterationResult> result=new ArrayList<>();
				
				
				TransferableElement entity = elementContainer.getElement();
				entity.setCurrentNode(taskNode);
				TaskContainer taskContainer = taskNode.getTaskContainer();
				Task task = taskContainer.getElement();
				
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), new EventContainer(elementContainer, process, taskNode));
				
				if(event.cancel)
					return null;
				
				task.virtualQueueTime.set(0);
				
				/**
				 * Cancel operation
				 * */
				
				/**if(task.processBuffer.size()>0) {
					TaskNode ce=task.processBuffer.get(0);
					long currentTime=event.beginDate.getTime();
					if(ce.entity.operationFinalizationEvent!=null && 
							(ce.entity.getRemainingTime() - currentgetTaskNodeTime + task.processBeginTime) < 
							container.entity.getRemainingTime()) {
						//ce.entity.rollbackTarget();
						task.elementQueue.add(ce);
						task.decreaseEventCount();
						ce.entity.getConcurrentEvents().clear();
						ce.entity.operationFinalizationEvent.cancel=true;
						ce.entity.concurrentEventCount=1;
						task.clearBuffers(ce);
						System.out.println("finalization cancel "+ce.entity.identifier+"."+ce.entity.arrivalSequence);
					}
				}*/
				
				
				/**
				 * Send element to directly element queue or assembly buffer of workstation.
				 * */
				
				/**byte processingStatus=-1;
				if(process.inputs!=null) {
					processingStatus=0;
					task.addToAssemblyBuffer(bufferContainer);
					if(task.readyForAssemble(bufferContainer))
						processingStatus=1;		
				}else if(!task.isFullCapacity())
					processingStatus=1;
				
				if(processingStatus!=0) {
					OperationEvent oEvent=new OperationEvent(task.getBlockIndex(), elementContainer, process, taskNode,
							simulationTime);
					if(!entity.getConcurrentOperationEvents().contains(oEvent))
						entity.addConcurrentOperationEvent(oEvent);
					task.elementQueue.add(bufferContainer);
					entity.setCurrentNode(taskNode);
				}
				else
					System.out.println("buffer is full");
				//TODO buffer full alert
				
				if(processingStatus==1 && !task.isEventBufferFull()) {
					task.increseEventCount();
					OperationEvent oEvent=new OperationEvent(task.getBlockIndex(), elementContainer, process, taskNode,
							simulationTime);
					if(!entity.getConcurrentOperationEvents().contains(oEvent))
						entity.addConcurrentOperationEvent(oEvent);
					engine.addEvent(oEvent);
				}*/
				
				byte processingStatus=0;
				if(process.inputs!=null) {
					processingStatus=-1;
					task.addToAssemblyBuffer(bufferContainer);
					if(task.readyForAssemble(bufferContainer))
						processingStatus=1;		
				}
				if(processingStatus>-1) {
					OperationEvent oEvent=new OperationEvent(task.getEventType(), bufferContainer, taskContainer,
							event.getBeginDate());
					if(!entity.getConcurrentOperationEvents().contains(oEvent))
						entity.addConcurrentOperationEvent(oEvent);
					task.elementQueue.add(bufferContainer);
					entity.setCurrentNode(taskNode);
					//System.out.println(task.getEventType());
					engine.addEvent(oEvent);
				}
				
				BigDate date=event.getBeginDate();
				result.add( new IterationResult(entity, task, 
						date, date, event.eventType));
								
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//5 Operation
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				
				List<IterationResult> result=new ArrayList<>();			
				BigDate completionDate=getSimulationTime();
				
				OperationEvent oEvent=((OperationEvent)event);
				
				Task task=(Task) oEvent.getTaskContainer().getElement();
				
				if(task.isAvailableForProcessing()){ //hasWaitingEntity
					task.increseEventCount();
					/**
					 * "entity" and "currentTarget" variables are same for all concurrent operations.
					 * There is no need to re-assign them from concurrent event list.
					 * */
					//iterateContainer
					TaskBufferContainer container = task.iterateElement();
					TaskNode taskNode = container.getTaskNode();
					task.processBeginTime = event.getBeginDate();
					TransferableElement entity = container.getTransferableElementContainer().getElement();
					
					event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), 
							new EventContainer(container.getTransferableElementContainer(), container.getProcess(), taskNode));
					
					if(event.cancel)
						return null;
					
					TransferInfo currentTarget=entity.getCurrentTarget();

					taskNode.setProcessSequence(task.increaseProcessSequence());
					task.processBuffer.add(container);
					
					/**
					 * hasNoInput variable is used to understand whether any input exists or 
					 * not. isAllComponentsAvailable is used for checking assembly conditions.
					 * */
					boolean hasNoInput=container.getProcess().inputs==null;
					boolean isAllComponentsAvailable=hasNoInput;
					if(!hasNoInput)
						isAllComponentsAvailable=task.readyForAssemble(container);
					
					entity.concurrentEventCount--;
					
					if(isAllComponentsAvailable) {
						//dEvent.setTaskNode(taskNode);
						
						/**
						 * Determine the concurrent processing strategy. All means that all concurrent operations
						 * begins at the same time. Available means that any operation begin without waiting others.
						 * Constraint means that any operation can begin when the user defined constraint satisfied.
						 * The next transfer event will be consisted with the completion of the last operation event.
						 * */
						
						//boolean transfer=false;
						switch(currentTarget.processingStrategy) {
						case AllAvailable:
							if(entity.concurrentEventCount == 0) {
								
								/**
								 * Finding latest completion within concurrent events.
								 * */
								BigDate transferTime=event.getBeginDate();
								for (OperationEvent e : entity.getConcurrentOperationEvents()) {
									TaskBufferContainer tContainer=e.getTaskBufferContainer();
									taskNode=tContainer.getTaskNode();
									ProcessResult evtRes=task.process(taskNode, entity, getSimulationTime());
									completionDate=evtRes.completionDate;
									task.setTaskCompletionDate(completionDate);
									
									if(taskNode.operationTime!=null)
										task.totalWorkload.subtract(taskNode.operationTime.get());
									if(taskNode.setupTime!=null)
										task.totalWorkload.subtract(taskNode.setupTime.get());
									if(completionDate.compareTo(transferTime)>0)
										transferTime=completionDate;
									task.setWaitingConcurrentOperation(false);
									
									result.add(new IterationResult(tContainer.getTransferableElementContainer().getElement(), 
											taskNode.getTask(), evtRes.beginDate, completionDate, event.eventType));

									//transfer=true;
									
									
									/**
									 * Finalize the operation
									 * */
									addOperationFinalizationEvent(container.getTransferableElementContainer(), tContainer, completionDate);
								}
								entity.getConcurrentOperationEvents().clear();

							}
							break;
						case AnyAvailable:
							ProcessResult evtRes=task.process(taskNode, entity, event.getBeginDate());
							completionDate=evtRes.getCompletionDate();
							
							if(taskNode.operationTime!=null)
								task.totalWorkload.subtract(taskNode.operationTime.get());
							if(taskNode.setupTime!=null)
								task.totalWorkload.subtract(taskNode.setupTime.get());
							
							task.setTaskCompletionDate(completionDate);
							task.setWaitingConcurrentOperation(false);
							
							result.add( new IterationResult(entity, task, 
									evtRes.beginDate, completionDate, event.eventType));
							//transfer=true;

							/**
							 * Finalize operation
							 * */

							entity.removeConcurrentOperationEvent(oEvent);
							addOperationFinalizationEvent(container.getTransferableElementContainer(), container, completionDate);
							
							break;
						case Constraint:
							break;
						}
						
					}
				}
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//6 Operation Finalization
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {			
				
				/**
				 * Finalize operation
				 * */
				
				if(event.cancel)
					return null;
				
				OperationFinalizationEvent fEvent=((OperationFinalizationEvent)event);
				TaskBufferContainer container=fEvent.getTaskBufferContainer();
				TaskNode taskNode = container.getTaskNode();
				TaskContainer taskContainer = taskNode.getTaskContainer();
				Task task=(Task) taskContainer.getElement();
				TransferableElementContainer elementContainer=container.getTransferableElementContainer();
				
				TransferableElement entity=elementContainer.getElement();
				
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), 
						new EventContainer(elementContainer, container.getProcess(), taskNode));
				
				if(event.cancel)
					return null;
				
				List<IterationResult> result=new ArrayList<>();
				BigDate completionDate=getSimulationTime();
								
				//task.totalWorkload.subtract(task.taskDuration);
				//boolean hasDispatchingEvent=false;
				//boolean transfer=true;
					
				/**
				 * "outputs" must be assigned before getting next target.
				 * */
				
				
				entity.removeConcurrentOperationFinalizationEvent(fEvent);
				if(entity.getConcurrentOperationFinalizationCount()==0) {
					AssemblyContainer[] outputs = entity.getCurrentTarget().getOutputs();
					
					
					/**
					 * Removes entity and all related sub parts from input buffer and assembly buffer.
					 * */
						
					List<AssemblyContainer> outputList=new ArrayList<>();
					
					/**
					 * If outputs is null, it means no new entity will be created.
					 * Assembly buffer will be investigated, if there is an entity
					 * or entities marked as output. 
					 * Otherwise, the last entity arriving to the workstation, will be
					 * considered as output.
					 * */
					
					if(outputs.length==0) {
						boolean noOutput=true;
						if(task.assemblyBuffer.size()>0) {
							List<TaskBufferContainer> taskList=task.assemblyBuffer.get(entity.assemblySequence);
							if(taskList != null)
								for(TaskBufferContainer tc:taskList) {
									if(tc.getTaskNode().hasOutput) {
										noOutput=false;
										outputList.add(new AssemblyContainer(entity, entity.amount, 1));
									}
								}
						}
						if(noOutput)
							outputList.add(new AssemblyContainer((Entity) entity, entity.amount, 1));
						
					} else {
						for (AssemblyContainer e : outputs) {
							/**
						 	 * New entity is being created...
							 * */
							TransferableElement eEntity=e.entityContainer.getElement();
							TransferableElement tEntity=cloneEntityFromTemplate(eEntity, 
									entity.arrivalSequence, entity.arrivalSequence*eEntity.assemblySetCount+
										eEntity.assemblySequence, getNextEntityUniqueSequence());
							outputList.add(new AssemblyContainer((Entity) tEntity, e.amount, e.batchAmount));
						}
					}
					
					/**
					 * The entity is forwarded to next target. If there is no new target,
					 * the entity leaves the system.
					 * */
					
					for (AssemblyContainer b : outputList) {
						//TransferableElement bEntity=b.entityContainer.getElement();
						for (int i = 0; i < b.batchAmount; i++) {
							engine.addEvent(new DispatchingEvent(b.entityContainer, completionDate));
							/**
							 * TODO Exit event
							 * if(transferInfo==null){
								
							} else {
								
							}
							
							bEntity.concurrentEventCount=transferInfo.getProcessContainers().size();
							for(Process targetContainer:transferInfo.getProcessContainers()) {
								engine.addEvent(new DispatchingEvent(elementContainer, completionDate));
								//hasDispatchingEvent=true;
							}*/
						}
					}
				}
				
				task.decreaseEventCount();
				task.clearBuffers(container);
				
				
				/**
				 * A new operation event is created, if there is entity waiting in the buffer.
				 * The entity parameter of event is set null, because the entity will be
				 * chosen from buffer according to dispatching rules.
				 * */
				if(task.isAvailableForProcessing()) {
					engine.addEvent(new OperationEvent(task.getEventType(), taskContainer,
							event.getBeginDate()));
				}
				result.add( new IterationResult(entity, task, 
						event.getBeginDate(), completionDate, event.eventType));
				
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//7 Operation for Store
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				OperationEvent oEvent=((OperationEvent)event);
				
				TaskBufferContainer container = oEvent.getTaskBufferContainer();

				TaskNode taskNode = container.getTaskNode();
				Task task=(Task) taskNode.getTaskContainer().getElement();
				TransferableElementContainer elementContainer=container.getTransferableElementContainer();
				TransferableElement entity=elementContainer.getElement();

				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), 
						new EventContainer(elementContainer, container.getProcess(), taskNode));
				
				if(event.cancel)
					return null;
				task.addElement(elementContainer);
				List<IterationResult> result=new ArrayList<>();
				/**TaskNode c=new TaskNode(entity, null, null);
				engine.addEvent(new DiscreteEvent(7, task, c, simulationTime, 
						EventType.ExitEvent));*/
				
				BigDate date = event.getBeginDate();
				result.add( new IterationResult(entity, task, 
						date, date, event.eventType));
				
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//8 Exit
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				ExitEvent eEvent=(ExitEvent)event;
				
				TransferableElementContainer container = eEvent.getElementContainer();
				TransferableElement entity=container.getElement();
				
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), 
						new EventContainer(eEvent.getElementContainer(), null, null));
				
				if(event.cancel)
					return null;
				
				List<IterationResult> result=new ArrayList<>();
				
				engine.simulationCycleTime = getSimulationTime();
				engine.simulationCycleTime.subtract(engine.lastExit);
				engine.lastExit=getSimulationTime();
				Object eSource=entity.currentNode;
				BigDate date = event.getBeginDate();
				
				if(eSource instanceof Task){
					Task sourceWorkCenter=(Task) eSource;
					sourceWorkCenter.removeElementIfExists(container);
					result.add(
							new IterationResult(entity, sourceWorkCenter, date, 
							date, event.eventType));
				} else
					result.add(new IterationResult(entity, null, date, 
							date, event.eventType));
				
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
		//9 Continuous Event Block
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				ContinuousEvent cEvent = (ContinuousEvent) event;
				EventContainer container = cEvent.getEventContainer();
				event.cancel |= fireAtTheBeginningEvents(event.getEventType().getIndex(), container);
				if(event.cancel)
					return null;
				BigDate date = getSimulationTime();
				if(cEvent.endDate == null || cEvent.endDate.compareTo(date)>0) {
					cEvent.act(engine, getSimulationTime());
					if(!event.cancel && event.duration!=null && (event.dueDate == null || event.dueDate.compareTo(date)>0)) {
						event.beginDate.add(event.duration);
						addEvent(event);
					}
				}
				
				TransferableElement entity=null;
				Task task = null;
				if(container!=null) {
					
					if(container.getTransferableElementContainer()!=null)
						entity=container.getTransferableElementContainer().getElement();
					if(container.getTaskContainer()!=null)
						task=container.getTaskContainer().getElement();
					
				} 
				return new IterationResult[] {new IterationResult(entity, task, date, date, event.eventType)};
			}
		});
		
		//10 AtTheEndUDEvent Block
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				UserEvent udEvent=(UserEvent) event;
				Task task=(Task) udEvent.getEventContainer().getTaskNode().getTask();
				LinkedList<Integer> endingEvents=task.atTheEnd.get(event.getEventType().getIndex());
				//List<IterationResult> result=new ArrayList<>();
				List<IterationResult> list=new ArrayList<>();
				
				if(endingEvents!=null)
					for(int i:endingEvents) {
						UserDefinedEvent userEvent=eventList.events[i];
						EventContainer container=udEvent.getEventContainer();
						TransferableElement entity=container.getTransferableElementContainer().getElement();
						userEvent.act(engine,  container);
						if(userEvent.cancel) break;

						BigDate date = getSimulationTime();
						list.add(new IterationResult(entity, task, date, 
								date, event.eventType));
						//result.add(new IterationResult(entity, task, simulationTime, 
						//	simulationTime, event.eventType));
					}
				return new IterationResult[] {}; 
				//return result.toArray(new IterationResult[0]);
			}
		});
		
		//11 UDEventBlock
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				
				UserEvent uEvent=(UserEvent)event;

				EventContainer container=uEvent.getEventContainer();
				
				for (Integer i : event.eventList) {
					eventList.events[i].act(engine, container);
				}
				TransferableElement entity=null;
				Task task = null;
				if(container!=null) {
					entity=container.getTransferableElementContainer().getElement();
					if(container.getTaskContainer()!=null)
						task=container.getTaskContainer().getElement();
				}

				BigDate date = getSimulationTime();
				
				return new IterationResult[] {new IterationResult(entity, task, date, 
						date, event.eventType)}; 
				
			}
		});
		
		//12 ClearBuffer Block
		insertBlock(new EngineBlock(this) {
			
			@Override
			public IterationResult[] fireEvent(SimulationEvent event) throws Exception {
				
				ClearBufferEvent bEvent=(ClearBufferEvent)event;	
				TaskBufferContainer container = bEvent.getTaskBufferContainer();
				TransferableElement entity=container.getTransferableElementContainer().getElement();
				TaskNode taskNode=container.getTaskNode();
				
				if(taskNode!=null) {
					entity.getConcurrentOperationEvents().clear();
					entity.getConcurrentOperationFinalizationEvents().clear();
					Task curr=taskNode.getTask();
					curr.decreaseEventCount();
					curr.clearBuffers(container);
				}
				
				List<IterationResult> result=new ArrayList<>();
				
				return result.toArray(new IterationResult[0]);
			}
		});
		
	}
	
	public void insertBlock(EngineBlock e) {
		engineBlocks.add(e);
		blockArray=engineBlocks.toArray(new EngineBlock[0]);
	}
	
	public void addEvent(SimulationEvent event){
		BigDate time = new BigDate(event.beginDate.getTime());
		BigDate time1 = new BigDate(event.beginDate.getTime().add(new BigDecimal(1)));
		SimulationEventCollection begin=new SimulationEventCollection(time),
				end=new SimulationEventCollection(time1);
		Set<SimulationEventCollection> indexElement=eventIndex.subSet(begin, end);
		SimulationEventCollection eventCollection=null;
		if(indexElement.size()==1)
			eventCollection=indexElement.iterator().next();
		else{
			eventCollection=new SimulationEventCollection(time);
			eventIndex.add(eventCollection);
		}
		
		
		/*if(event.getContainer().getEntity()!=null && event.getContainer().getTarget()!=null)
			System.out.println("..."+event.getContainer().getEntity().identifier+"."+
					event.getContainer().getEntity().sequence+"\t"+event.getContainer().getTarget().identifier+
					"\t"+event.eventType);*/
		
		eventCollection.addEvent(event);		
	}
	
	protected SimulationEventCollection getFirstEventCollection(){
		
		SimulationEventCollection eventCollection=null;
		if(eventIndex.size()>0) {
			eventCollection=eventIndex.first();
			while(eventIndex.size()>1 && eventCollection.getEventCount()==0) {
				eventIndex.remove(eventCollection);
				eventCollection=eventIndex.first();
			}
		}
		if(eventCollection==null)
			return null;
		return eventCollection;
	}
	
	@Override
	public void run() {
		
		try {
			if(licenseInfo!=null)
				System.out.println("No license found");
			else {
				//System.out.println("Licensed to "+linfo.customerName+"\t"+linfo.expirationDate);

				HashMap<String, Integer> taskID=new HashMap<>();
				HashMap<String, Integer> resourceID=new HashMap<>();
				String ganttSeperator="";
				if(saveGanttFile) 
					FileAgent.writeFile(new URI("results.gantt"), "{\"data\":[", FileCreateMode.IfNotExists, false);
				
				boolean allFinished;
				IterationResult[] result=null;
				ArrayList<SimulationEvent> eventList=null;
				SimulationEventCollection events=getFirstEventCollection();
				if(events!=null) {
					lastExit=new BigDate(events.date.getTime());
					simulationBeginTime=lastExit;
				}
				while(events!=null){
					allFinished=true;
					
					while ((eventList=events.iterateEvents())!=null) {
						
						for(SimulationEvent event:eventList) {
							simulationTime=event.getBeginDate();
							int res=simulationTime.compareTo(simulationEndTime);
							if(res<1){
								allFinished=false;
								try {
									int blockId=event.getEventType().getIndex();
									try {
										result = blockArray[blockId].fireEvent(event); //blockArray[0].
									}catch(Exception ex){
										ex.printStackTrace();
										System.out.println(ex.getMessage());
										//FileAgent.writeFile(new URI("errors.log"), ex.getMessage().getBytes(), 
											//	FileCreateMode.IfNotExists, true);
									}
									if(result != null)
										for (IterationResult iterationResult : result) {
											String ident=iterationResult.getTarget()==null? "ENGINE" :
															""+iterationResult.getTarget().getIdentity();
											//if(iterationResult.eventType.equals(EventType.OperationEvent))
											if(showTrace)
												System.out.println(ident+"\t"+ 
														(iterationResult.getEntity() == null ? "null" : 
														iterationResult.getEntity().getIdentity() + "." +
														((Entity)iterationResult.getEntity()).arrivalSequence)+"\t"+
														iterationResult.eventType+"\t"+
														iterationResult.begin.toString()+"\t"+
														iterationResult.completion.toString()+"\t"+
														iterationResult.completion.getTime().
														subtract(simulationTime.getTime()).
														divide(new BigDecimal(1000)));
											if(saveGanttFile && iterationResult.eventType.equals(EventType.OperationEvent)) {
												if(!resourceID.containsKey(ident))
													resourceID.put(ident, 0);
												String taskIdentity=iterationResult.getEntity().getIdentity()+"_"+ident;
												if(!taskID.containsKey(taskIdentity))
													taskID.put(taskIdentity, taskID.size()+1);
												BigDate comp=new BigDate(iterationResult.completion.getTime());
												comp.subtract(iterationResult.begin);
												FileAgent.writeFile(new URI("results.gantt"), 
														ganttSeperator+"{\"TaskID\":"+taskID.get(taskIdentity)
														+",\"TaskName\":\""+taskIdentity+"\",\"StartDate\":\""+iterationResult.begin.toString().replace(" ", "T")
														+"Z\",\"EndDate\":\""+iterationResult.completion.toString().replace(" ", "T")+
														"Z\",\"Duration\":"+(comp.getDay()-1)+",\"Progress\":0,\"Predecessor\":\"\","
																+ "\"resources\":[{\"resourceId\":\""+ident+"\",\"resourceName\":\""+ident+"\",\"unit\":100}],"
																		+ "\"info\":\"<p><br></p>\",\"DurationUnit\":\"day\"}", 
																		FileCreateMode.IfNotExists, true);
												ganttSeperator=",";
											}
											
										}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else break;
						}
						eventList.clear();
						//System.gc();
					}
					eventIndex.remove(events);
					
					if(allFinished)
						break;
					events=getFirstEventCollection();
						
				}
				if(saveGanttFile) {
					FileAgent.writeFile(new URI("results.gantt"), "],\"resources\":[", FileCreateMode.IfNotExists, true);
					String resources="", sep="";
					for(Entry<String, Integer> en:resourceID.entrySet()) {
						resources+=sep+"{\"resourceId\":\""+en.getKey()+"\",\"resourceName\":\""+en.getKey()+"\"}";
						sep=",";
					}
					FileAgent.writeFile(new URI("results.gantt"), resources+"],\"projectStartDate\":null,\"projectEndDate\":null,\"advanced\":{\"columns\":[{\"name\":\"Task ID\",\"width\":\"70\",\"show\":true},{\"name\":\"Task Name\",\"width\":\"350\",\"show\":true},{\"name\":\"Start Date\",\"width\":\"130\",\"show\":false},{\"name\":\"End Date\",\"width\":\"130\",\"show\":false},{\"name\":\"Duration\",\"width\":\"130\",\"show\":false},{\"name\":\"Progress %\",\"width\":\"150\",\"show\":false},{\"name\":\"Dependency\",\"width\":\"150\",\"show\":false},{\"name\":\"Resources\",\"width\":\"200\",\"show\":false},{\"name\":\"Color\",\"width\":\"100\",\"show\":false}],\"firstDayOfWeek\":0,\"workWeek\":[\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"],\"holidays\":[]}}", FileCreateMode.IfNotExists, true);
					
				}
				
				/**if(event!=null)
					System.out.print("\t"+((event.getBeginDate().getTime()-begin)/1000));*/
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initEvent(DiscreteEvent[] events, BigDate endTime){
		setSimulationEndTime(endTime);
		for(DiscreteEvent event:events){
			addEvent(event);
		}
	}
	
	public BigDate getSimulationTime(){
		return new BigDate(simulationTime.getTime());
	}

	protected void setSimulationTime(BigDate simulationTime) {
		this.simulationTime = new BigDate(simulationTime.getTime());
	}

	public BigDate getSimulationEndTime() {
		return new BigDate(simulationEndTime.getTime());
	}

	public void setSimulationEndTime(BigDate simulationEndTime) {
		this.simulationEndTime = new BigDate(simulationEndTime.getTime());;
	}

	public BigDate getSimulationBeginTime() {
		return new BigDate(simulationBeginTime.getTime());
	}

	public void setSimulationBeginTime(BigDate simulationBeginTime) {
		this.simulationBeginTime = simulationBeginTime;
	}
	
	public BigDate getSimulationDuration() {
		BigDate result = new BigDate(this.simulationTime.getTime());
		result.subtract(this.simulationBeginTime);
		return result;
	}

}
