package test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.TriangularDistribution;

import alcybe.analytics.RandomNumberAgent;
import alcybe.data.BigDate;
import alcybe.data.DataTable;
import alcybe.data.InMemoryDatabase;
import alcybe.data.delegates.DelegateTemplate;
import alcybe.data.delegates.DoubleDelegate;
import alcybe.data.delegates.IntegerDelegate;
import alcybe.data.delegates.StringDelegate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.ContinuousEvent;
import alcybe.simulation.events.DispatchingEvent;
import alcybe.simulation.events.UserDefinedEvent;
import alcybe.simulation.events.UserEvent;
import alcybe.simulation.events.utils.EventList;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.FixedTimeParameter;
import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.EntityInstanceFactory;
import alcybe.simulation.objects.Store;
import alcybe.simulation.objects.TransferableElement;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;

public class Covid19 {
	
	public static final InMemoryDatabase database=new InMemoryDatabase();
	public static class CountContainer{
		public int service=0, icu=0, intubated=0;
	}
	
	public static final Logger logger = Logger.getLogger(Covid19.class.getName());
	
	static {
		logger.setUseParentHandlers(false);
		try {
			FileHandler fh=new FileHandler("./simData.log", true);
			logger.addHandler(fh);
		} catch (SecurityException | IOException e) {
			//TODO Logger initialization error message
		}
	}
	
	public static CountContainer[] dailyCount=null;
	public static int avgNumOfPeoplePerShop=2456; 
	public static int[] numberOfInfectedHome,numberOfInfectedHospital;
	public static List<TransferableElementContainer> incubatedList=new ArrayList<>(), 
			activeList=new ArrayList<>(),
			icuList=new ArrayList<>(), intList=new ArrayList<>(), rServiceList=new ArrayList<>(), 
			healthyList=new ArrayList<>(20000000), infectedList=new ArrayList<>(), deadList=new ArrayList<>();

	public static final long dayMS=86400000l;
	
	public static TaskContainer cemetryContainer; 
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		@SuppressWarnings("rawtypes")
		Engine<?> simEngine=new Engine();
		
		simEngine.database.createNewTable("populationData", new DelegateTemplate[] {new StringDelegate(),
				new IntegerDelegate(),new DoubleDelegate(),new IntegerDelegate(),new IntegerDelegate(),
				new IntegerDelegate(),new IntegerDelegate(),new IntegerDelegate(), new IntegerDelegate()
				,new IntegerDelegate()});
		
		simEngine.database.createNewTable("hospitalData", new DelegateTemplate[] {new StringDelegate(),
						new IntegerDelegate(),new IntegerDelegate(),new DoubleDelegate(),
						new IntegerDelegate(),new IntegerDelegate(),
						new DoubleDelegate(),new DoubleDelegate()});
		
		
		DelegateTemplate[] dt=new DelegateTemplate[27];
		for (int i = 0; i < dt.length; i++)
			dt[i]=new IntegerDelegate();
		simEngine.database.createNewTable("infectionData", dt);
		
		simEngine.database.fillTable("populationData", new URI("dataset_covid19/population.txt"));
		simEngine.database.fillTable("hospitalData", new URI("dataset_covid19/hospital.txt"));
		simEngine.database.fillTable("infectionData", new URI("dataset_covid19/turkey_data.txt"));
		

		final DataTable dataTable=simEngine.database.getTable("infectionData"), 
				populationData=simEngine.database.getTable("populationData"), 
				hospitalData=simEngine.database.getTable("hospitalData");
		numberOfInfectedHome=new int[dataTable.getRowSize()];
		
		
		numberOfInfectedHospital=new int[dataTable.getRowSize()];
		
		CountContainer[] dailyCount=new CountContainer[dataTable.getRowSize()+1];
		for(int i = 0; i < dailyCount.length; i++) 
			dailyCount[i]=new CountContainer();
		simEngine.database.addVariable("dailyCount", dailyCount);
		simEngine.database.addVariable("numberOfInfectedHome", new int[dataTable.getRowSize()+1]);
		simEngine.database.addVariable("numberOfInfectedHospital", new int[dataTable.getRowSize()+1]);
		simEngine.database.addVariable("dataTableBeginDay", 3);
		simEngine.database.addVariable("infectionTimespan", 3*24);

		simEngine.database.commitVariables();
		//System.out.println("");
		simEngine.eventList=new EventList() {
			
			//public static Entity[] entities;
			//public static long day;
			
			/**public int generateTimeSpan(int dMin, int dMax, int hMin, int hMax) {
				double days= RandomNumberAgent.generateUniformRandom(dMin, dMax);
				return (int)Math.round(days*24+8+Math.round(RandomNumberAgent.generateUniformRandom(hMin, hMax)));
			}*/
			
			
			public void saveLog(final Object id, final Object homeId, final BigDate simulationTime, final BigDate infectionTime, final boolean isSvere, 
					final String infectionSource, final String previousState, final String currentState) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String logData=id +"\t"+homeId+"\t"+simulationTime.toString()+"\t"+simulationTime.getTime()+"\t"+ 
								infectionTime.toString()+"\t"+infectionTime.getTime()+"\t"+(isSvere?"True":"False")+"\t"+
									infectionSource+"\t"+previousState+"\t"+currentState+"\n";
						logger.info(logData);
					}
				}).start();
			}
			
			private void infectEntity(Engine<?> engine, TransferableElementContainer container,
					int hours, boolean isSvere, String infectionSource) {
				infectedList.add(container);
				TransferableElement entity=container.getElement();
				entity.setAttribute("IsSvere", isSvere);
				entity.setAttribute("InfectionTime", engine.getSimulationTime());
				entity.setAttribute("InfectionSource", infectionSource);
				BigDate time=new BigDate(engine.getSimulationTime().getTime());
				time.addHour(hours);
				engine.addEvent(new UserEvent(EventType.UserDefinedEvent, new EventContainer(container, entity.currentNode.getTaskContainer()),
					time , 3));
				
				saveLog(entity.identity, ((TaskContainer)entity.getAttribute("Home")).getElement().identity, engine.getSimulationTime(),engine.getSimulationTime(),
						(boolean)entity.getAttribute("IsSvere"), infectionSource,
						(String)entity.getState(),"Infected");
				entity.setState("Infected");
			}
			
			public void infect(Engine<?> engine, TransferableElementContainer container, 
						double random, double threshold, int hours, String infectionSource) {
				float reInfectionCoeff=-1f;
				String state=(String) container.getElement().getState();
				if(state.equals("Healthy")) reInfectionCoeff=1f;
				else if(state.equals("Recovered"))reInfectionCoeff=0.16f;
				if(random>threshold && reInfectionCoeff>0) {
					//TransferableElement entity=(TransferableElement) container.getElement();

					long d=engine.getSimulationTime().getTime().longValue();
					int dS=(int)((d-engine.getSimulationBeginTime().getTime().longValue())/dayMS);
					
					int tablePos=dS-(int)simEngine.database.getVariable(3)+hours/24;
					if(tablePos>-1) {
						int infectedHospital=(int)dataTable.getData(tablePos, "I.New");
						int infectedHome=(int)dataTable.getData(tablePos, "H.New");
						double ratio=(double)infectedHome/(double)(infectedHome+infectedHospital);
						boolean isSevere=false;
						if(Math.random()<=ratio) {
							if(numberOfInfectedHome[tablePos]<infectedHome) {
								/**
								infectedList.add(entity);
								entity.setState("Infected");
								entity.setAttribute("IsSvere", false);
								entity.setAttribute("InfectionTime", d);
								engine.addEvent(new DiscreteEvent(10, source, new TaskNode(entity),
										ComputationAgent.addTime(engine.getSimulationTime(), 
												TimeUnit.HOUR, hours), 7, EventType.UserDefinedEvent));
								*/
								if(Math.random()<=reInfectionCoeff) {
									infectEntity(engine, container, hours, false, infectionSource);
									numberOfInfectedHome[tablePos]++;
								}
							}else isSevere = numberOfInfectedHospital[tablePos]<infectedHospital;
						}else {
							isSevere = numberOfInfectedHospital[tablePos]<infectedHospital;
							if(!isSevere && numberOfInfectedHome[tablePos]<infectedHome) {
								if(Math.random()<=reInfectionCoeff) {
									infectEntity(engine, container, hours, false, infectionSource);
									numberOfInfectedHome[tablePos]++;
								}
							}
						}
						if(isSevere) {
							if(Math.random()<=reInfectionCoeff) {
								infectEntity(engine, container, hours, true, infectionSource);
								numberOfInfectedHospital[tablePos]++;				
							}
						}
					}
					
				}
			}
			
			public void checkInfected(Engine<?> engine, TaskContainer taskContainer, TransferableElementContainer container, double random, double threshold,
					String infectionSource) {
				Task task = taskContainer.getElement();
				TriangularDistribution td=new TriangularDistribution(4, 5.1, 7);
				TransferableElement entity = container.getElement();
				if(entity.getState().equals("Infected") || entity.getState().equals("Asymptomatic")) {
					for (Entry<Object, List<TransferableElementContainer>> e : task.elementBuffer.entrySet()) 
						for(TransferableElementContainer t : e.getValue()) 
							infect(engine, t, random, threshold, (int)Math.round(td.sample()), infectionSource);
				} else { 
					boolean hasInfected=false;
					for (Entry<Object, List<TransferableElementContainer>> e : task.elementBuffer.entrySet()) {
						for(TransferableElementContainer t : e.getValue()) {
							TransferableElement tElement = t.getElement();
							hasInfected=tElement.getState().equals("Infected")|| tElement.getState().equals("Asymptomatic");
							if(hasInfected) break;
						}
						if(hasInfected) break;
					}
					if(hasInfected) 
						infect(engine, container, random, threshold, (int)Math.round(td.sample()), infectionSource);
				}
			}
			
			double[] markovTable = {0.000962293794187,
					0.011783189316575,
					0.033955223880597,
					0.067635506677141,
					0.110516496465043,
					0.159721131186174,
					0.221651610369207,
					0.302602120974077,
					0.387863315003928,
					0.492183817753339,
					0.607276119402985,
					0.721661429693637,
					0.824214454045562,
					0.914002356637863,
					0.981942262372349,
					1 };
			
			public void initTransferToShop(@SuppressWarnings("rawtypes") Engine simulationEngine, TaskContainer taskContainer, 
					TransferableElementContainer container) {
				System.out.println("****************************************");
				System.out.println("Transfer to Shop");
				System.out.println("****************************************");
				//int hours=generateTimeSpan(7, 15, 0, 11);
				int hours=7*24;
				double rand=Math.random();
				int i=0;
				if(rand>=markovTable[7]) i = 7;
				
				for (; i < markovTable.length && rand >= markovTable[i]; i++) ;
				
				hours+=i+7;
				
				BigDate d=new BigDate(simulationEngine.getSimulationTime().getTime());
				hours-=d.getHour();
				d.addHour(hours);
				simulationEngine.addEvent(new UserEvent(EventType.UserDefinedEvent, new EventContainer(container, taskContainer), d, 2));
			}
			
			@Override
			public void init() {
				super.events = new UserDefinedEvent[] {

						//Home 0	
						new UserDefinedEvent() {
							
							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {
								TaskNode currentNode=eventContainer.getTaskNode();
								TaskContainer taskContainer=currentNode.getTaskContainer();
								double r=RandomNumberAgent.generateUniformRandom(0, 100);
								checkInfected(simulationEngine, taskContainer, eventContainer.getTransferableElementContainer(), r, 75, "Home");
								TransferableElement tEntity = eventContainer.getTransferableElementContainer().getElement();
								if((boolean)tEntity.getAttribute("Shopping"))
									initTransferToShop(simulationEngine, (TaskContainer)tEntity.getAttribute("Shop"), 
											eventContainer.getTransferableElementContainer());
							}
						},
						
						//Shop 1
						new UserDefinedEvent() {
							
							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {
								//System.out.println(tEntity.getState()+" in shop");
								//Task task=(Task)simulationEngine.objectList.get(0);
								TaskContainer taskContainer=eventContainer.getTaskContainer();
								double r=RandomNumberAgent.generateUniformRandom(0, 100);
								checkInfected(simulationEngine, taskContainer, eventContainer.transferableElementContainer,r, 87.5, "Store");
								
								BigDate time=new BigDate(simulationEngine.getSimulationTime().getTime());
								time.addHour(1);
								simulationEngine.addEvent(new UserEvent(EventType.UserDefinedEvent, eventContainer, time, 2));
							}
						},
						
						//Transfer to Home & Shop 2
						new UserDefinedEvent() {
									
							/**
							 * 
						 	*/
							private static final long serialVersionUID = 1L;
							
							@Override
							public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {
								
								
								/**
								 Object state=currentNode.entity.getState();
				if(state.equals("Healthy")||state.equals("Infected")||state.equals("Asymptomatic")||state.equals("Recovered")) {
					Task task=(Task)simulationEngine.objectList.get(0);
					if(currentNode.target.equals(task)) {
						simulationEngine.transfer(currentNode.entity, currentNode, 
								new TaskNode((Task)currentNode.entity.getAttribute("Home")), 
								simulationEngine.getSimulationTime());
					} else if((boolean)currentNode.entity.getAttribute("Shopping")) {
						if((boolean)currentNode.entity.getAttribute("UnderQuarantine")) {
							initTransferToShop(simulationEngine, currentNode);
						}else {
							simulationEngine.transfer(currentNode.entity, currentNode, new TaskNode(task), 
									simulationEngine.getSimulationTime());
						}
					}
				}
								 * */
								
								
								TransferableElementContainer container = eventContainer.getTransferableElementContainer();
								TransferableElement entity = container.getElement();
								//if((boolean)entity.getAttribute("UnderQuarantine")) {
								//	initTransferToShop(simulationEngine, eventContainer.getTaskContainer(), container);
								//}else {
								if(!(boolean)entity.getAttribute("UnderQuarantine")) {
									if(entity.currentNode.getTask().equals(((TaskContainer)entity.getAttribute("Home")).getElement())) {

										Object state=entity.getState();
										if(state.equals("Healthy")||state.equals("Infected")||state.equals("Asymptomatic")||
												state.equals("Recovered")) 

											initTransferToShop(simulationEngine, (TaskContainer)entity.getAttribute("Shop"), container);
									}else
										simulationEngine.transfer(container, entity.currentNode, 
												(TaskContainer)entity.getAttribute("Home"),
												simulationEngine.getSimulationTime());
								
								}
							}
						},
						
						//Transfer To Hospital 3
						new UserDefinedEvent() {
															
							/**
							 * 
						 	 */
							private static final long serialVersionUID = 1L;
							
							@Override
							public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {

								//System.out.println("hospital event");
								//Mild, Severe, Intensive Care, Intubated, Death, Recovered
								TransferableElementContainer container = eventContainer.getTransferableElementContainer();
								TransferableElement entity = container.getElement();
								entity.setAttribute("RecoveryBegin", simulationEngine.getSimulationTime().getTime());
								activeList.add(container);
								if((boolean)entity.getAttribute("IsSvere")) {
									simulationEngine.transfer(container, entity.currentNode, 
											new TaskNode((TaskContainer)entity.getAttribute("Hospital")), 
											simulationEngine.getSimulationTime());					
								}
								else {
									TaskContainer home=(TaskContainer)entity.getAttribute("Home");
									simulationEngine.transfer(container, entity.currentNode, 
											new TaskNode(home), 
											simulationEngine.getSimulationTime());
									for(TaskBufferContainer c:home.getElement().processBuffer) 
										c.getTransferableElementContainer().getElement().setAttribute("UnderQuarantine",true);
								}
								//EventList.testList.add(currentNode.entity);
								/*int hours=generateTimeSpan(1, 3, 0, 1);
								simulationEngine.addEvent(new DiscreteEvent(10, source, new TaskNode(currentNode.entity),
										ComputationAgent.addTime(simulationEngine.getSimulationTime(), 
												TimeUnit.HOUR, hours), 8, EventType.UserDefinedEvent));*/
							}
						},		
						
						//Before Transfer to Market 8
						new UserDefinedEvent() {
																	
							/**
							 * 
						 	 */
							private static final long serialVersionUID = 1L;
									
							@Override
							public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {
									//Mild, Severe, Intensive Care, Intubated, Death, Recovered
								TransferableElementContainer container = eventContainer.getTransferableElementContainer();
								TransferableElement entity = container.getElement();
								String state=(String) entity.getState();
								
								this.cancel = state.equals("Dead") || 
										(eventContainer.getTaskNode().getTaskContainer().getElement().getAttribute("Type").equals(""));
								
								/**if(this.cancelSimulationEvent) {
									System.out.println("............");
									System.out.println("Transferring to shop is cancelled!");
									System.out.println("............");
								}*/
							
							}
						}				
					};
			}
		};
		EntityInstanceFactory eif=new EntityInstanceFactory();
		eif.identity="eif";
		final BigDate beginDate=new BigDate(0);
		final BigDate endDate=new BigDate(0);
		endDate.addDay(328);
		
		int population=1000000;//83614362;//83614362;
		Object[] cityPopulation=populationData.getColumn(1);
		Object[] shopCapacityDifference=populationData.getColumn(8);
		Object[] hospitalCountList=hospitalData.getColumn(1);
		//EventList.entities=new Entity[population];
		TaskContainer shopContainer=null, homeContainer=null, hospitalContainer=null;
		int shopCount=0, houseCount=0, minPopulation=0, houseHold=0, houseHoldPopulation=0,
				numOfHospitals=0,hospitalCount=0, peoplePerHospital=0;

		int cityIndex=-1, assignedPeople=0, populationBound=0, peoplePerShop=0;
		LinkedList<Integer> homeEvent=new LinkedList<Integer>(),
				shopEvent=new LinkedList<Integer>();
		homeEvent.add(0);
		shopEvent.add(1);
		for (int i = 0; i < population; i++) {
			Entity entity=new Entity();
			entity.identity=Integer.toString(i);
			if(i==populationBound) {
				cityIndex++;
				shopCount=0;
				peoplePerShop=avgNumOfPeoplePerShop+(int)shopCapacityDifference[cityIndex];
				populationBound+=(int)cityPopulation[cityIndex];
				assignedPeople=0;
				houseHoldPopulation=0;
				minPopulation=(int)populationData.getData(cityIndex, "Min Household Population");
				numOfHospitals=(int) hospitalCountList[cityIndex];
				peoplePerHospital=(int)Math.ceil(populationBound)/numOfHospitals;
			}
			if(assignedPeople%peoplePerShop==0) {
				Store shop=new Store();
				shop.setAttribute("Type", "Shop");
				shop.atTheBeginning.put(EventType.StoreEvent.getIndex(),shopEvent);
				shop.bufferCapacity=1_000_000;
				shop.identity=100_000_000+shopCount;
				shopContainer = new TaskContainer(shop);
				shopCount++;
			}
			
			if(assignedPeople%peoplePerHospital==0) {
				Store hospital=new Store();
				hospital.setAttribute("Type", "Hospital");
				hospital.bufferCapacity=1_000_000;
				hospital.identity=110_000_000+hospitalCount;
				hospitalContainer = new TaskContainer(hospital);
				hospitalCount++;
			}
			entity.setState("Healthy");
			entity.setAttribute("Shop", shopContainer);
			entity.setAttribute("Hospital", hospitalContainer);
			assignedPeople++;
			if(assignedPeople>minPopulation) 
				houseHold=(int)populationData.getData(cityIndex, "Max Household");
			else
				houseHold=(int)populationData.getData(cityIndex, "Min Household");
			if(houseHoldPopulation%houseHold==0) {
				Store home=new Store();
				home.atTheBeginning.put(EventType.StoreEvent.getIndex(),homeEvent);
				home.setAttribute("Type", "Home");
				home.bufferCapacity=1_000_000;
				home.identity=houseCount;
				homeContainer = new TaskContainer(home);
				houseCount++;
				entity.setAttribute("Shopping", true);
			}else
				entity.setAttribute("Shopping", false);
			entity.setAttribute("Home", homeContainer);
			houseHoldPopulation++;
			if(houseHoldPopulation==minPopulation)
				houseHoldPopulation=0;
			
			entity.setAttribute("UnderQuarantine", false);
			
			//Adding dispatching event to transfer entities to their homes at the beginning.
			entity.addTarget(new TransferInfo(new Process(new TaskNode(homeContainer, new FixedTimeParameter(28800,TimeUnit.second)))));
			
			
			TransferableElementContainer container = new TransferableElementContainer(entity);

			simEngine.addEvent(new DispatchingEvent(container, beginDate));
			
			healthyList.add(container);
			
			if(i%1000==0)
				System.out.println(i);
		}
		System.out.println(population+" entities are created.");
		System.out.println(hospitalCount+" "+shopCount+" "+houseCount);

		
		//Cemetry
		Store cemetry=new Store();
		cemetry.setIdentity(9999999);
		cemetry.bufferCapacity=90000000;
		cemetryContainer = new TaskContainer(cemetry);
		
		/**Entity[] population=new Entity[4000000];
		for (int i = 0; i < population.length; i++) {
			population[i]=new Entity();
			population[i].setIdentity(i);
			population[i].addTarget(new TransferInfo(new Process(new TaskNode(houses[i/4], 8l))));
			src.addElement(population[i]);
			if(i%2500==0)
				System.out.println(i);
		}*/
		
		System.gc();
		//Date beginDate=getDate();
		simEngine.showTrace=true;
		simEngine.initDefaultBlocks();
		
		
		simEngine.addEvent(new ContinuousEvent(beginDate, new BigDate(86400000),endDate) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
					
			private void infect(Engine<?> engine,int hours, String infectionSource) {
				int index=(int)Math.round((double)healthyList.size()*Math.random());
				if(index==healthyList.size()) index--;
				if(index>-1) {
					TransferableElementContainer e=healthyList.get(index);
					healthyList.remove(index);

					simEngine.callFunction("infect", engine, e, 1, 0, hours, infectionSource);
					//infect(engine, e, 1, 0, hours, infectionSource);
				}
			}
			
			private List<TransferableElementContainer> getStateChangingList(List<TransferableElementContainer> sourceList,
					int changeSize, long d, String attribute, TriangularDistribution durationDist){

				List<TransferableElementContainer> changeList=new ArrayList<>();
				double minScore=Double.MAX_VALUE;
				TransferableElementContainer minElement=null;
				for(TransferableElementContainer c:sourceList) {
					TransferableElement e = c.getElement();
					double rand=Math.random();
					long infectionTime=((BigDate) e.getAttribute(attribute)).getTime().longValue();
					int duration=(int) ((d-infectionTime)/dayMS);
					double score=durationDist.cumulativeProbability(duration)-rand;
					if(changeList.size()<changeSize) {
						e.setAttribute("Score", score);
						changeList.add(c);
						if(minScore>score) {
							minScore=score;
							minElement=c;
						}
					}else if(score>minScore) {
						changeList.remove(minElement);
						e.setAttribute("Score", score);
						changeList.add(c);
						minScore=Double.MAX_VALUE;
						for(TransferableElementContainer el:changeList) {
							if(minScore>score) {
								minScore=(double)el.getElement().getAttribute("Score");
								minElement=el;
							}
						}
					}
				}
				return changeList;
			}
			
			void initTransfer(Engine<?> engine, TransferableElementContainer container, String state) {
				TransferableElement entity = container.getElement();
				switch(state) {
				case "Dead":{
					engine.transfer(container, entity.currentNode, 
							new TaskNode(cemetryContainer), 
							engine.getSimulationTime());
					break;
				}
				case "Recovered":{
					TaskContainer homeContainer=(TaskContainer)entity.getAttribute("Home");
					engine.transfer(container, entity.currentNode, 
							new TaskNode(homeContainer), engine.getSimulationTime());
					entity.setAttribute("IsSvere", false);
					Task home = homeContainer.getElement();
					for(TaskBufferContainer t:home.processBuffer) {
						TransferableElement tEntity = t.getTransferableElementContainer().getElement();
						String es=(String) tEntity.getState();
						if(es.equals("Healthy")||es.equals("Infected")||es.equals("Asymptomatic")||
								es.equals("Recovered"))
							tEntity.setAttribute("UnderQuarantine",false);
					}
					break;
				}
				case "Incubated":
				case "Intubated":{
					TaskContainer homeContainer=(TaskContainer)entity.getAttribute("Home");
					Task home = homeContainer.getElement();
					if(home.equals(entity.currentNode.getTask())) {
						entity.setAttribute("IsSvere", true);
						engine.transfer(container, entity.currentNode,
								new TaskNode((TaskContainer)entity.getAttribute("Hospital")), 
								engine.getSimulationTime());
					}
					break;
				}
				}
				TaskContainer homeContainer=(TaskContainer)entity.getAttribute("Home");
				simEngine.callFunction("saveLog", entity.identity, homeContainer.getElement().identity, 
						engine.getSimulationTime(),(BigDate)entity.getAttribute("InfectionTime"),
						(boolean)entity.getAttribute("IsSvere"), (String)entity.getAttribute("InfectionSource"),
						(String)entity.getState(),state);
				
				/**
				   saveLog(entity.identity, ((Task)entity.getAttribute("Home")).identity, 
						engine.getSimulationTime(),(Date)entity.getAttribute("InfectionTime"),
						(boolean)entity.getAttribute("IsSvere"), (String)entity.getAttribute("InfectionSource"),
						(String)entity.getState(),state);
				 * */
				entity.setState(state);
			}
			
			void changeState(Engine<?> engine, List<TransferableElementContainer> changeList, 
					List<TransferableElementContainer> sourceList,
					List<TransferableElementContainer> firstTransfer, int transferCount1,
					List<TransferableElementContainer> secondTransfer, int transferCount2,
					List<TransferableElementContainer> thirdTransfer, int transferCount3,
					List<TransferableElementContainer> fourthTransfer, String... stateList ){
				
				for(TransferableElementContainer e:changeList)
					sourceList.remove(e);
				
				for (int i = 0; changeList.size()>0 && i < transferCount1; i++) {
					int pos=(int)Math.ceil(Math.random()*changeList.size());
					if(pos==changeList.size())pos--;
					TransferableElementContainer element=changeList.get(pos);
					firstTransfer.add(element);
					changeList.remove(pos);
					initTransfer(engine, element, stateList[0]);
				}
				
				for (int i = 0; changeList.size()>0 && i < transferCount2; i++) {
					int pos=(int)Math.ceil(Math.random()*changeList.size());
					if(pos==changeList.size())pos--;
					TransferableElementContainer element=changeList.get(pos);
					secondTransfer.add(element);
					changeList.remove(pos);
					initTransfer(engine, element, stateList[1]);
				}
				
				if(thirdTransfer!=null) {
					for (int i = 0; changeList.size()>0 && i < transferCount3; i++) {
						int pos=(int)Math.ceil(Math.random()*changeList.size());
						if(pos==changeList.size())pos--;
						TransferableElementContainer element=changeList.get(pos);
						thirdTransfer.add(element);
						changeList.remove(pos);
						initTransfer(engine, element, stateList[2]);
					}
				}
				
				while (changeList.size()>0) {
					TransferableElementContainer element=changeList.get(0);
					fourthTransfer.add(element);
					changeList.remove(0);
					initTransfer(engine, element, stateList[3]); //TODO error index out of bounds
				}
			}
			
			@Override
			public void act(Engine<?> engine, BigDate simulationTime) {
				
				long d=simulationTime.getTime().longValue();
				int dS=(int)((d-engine.getSimulationBeginTime().getTime().longValue())/dayMS);

				int tablePos=dS-(int)simEngine.database.getVariable(3);
				int iCount=(int)dataTable.getData(dS, "I.New")+
						(int)dataTable.getData(dS, "H.New")-
						numberOfInfectedHome[dS]-numberOfInfectedHospital[dS];
				for (int i = 0; i < iCount; i++) {
					infect(engine, (int)simEngine.database.getVariable(4), "Other");
				}
	
				if(tablePos>-1) {
					
					int actIcu=0, actInt=0,icuInt=0, intIcu=0, intAct=0, icuAct=0, actDead=0, 
							icuDead=0, intDead=0, recovered=0;
					try {
						//totalHosp = (int)EventList.dataTable.getData(tablePos, "I.New");
						//totalMild = (int)EventList.dataTable.getData(tablePos, "H.New");
						
						intAct = (int)dataTable.getData(tablePos, "Int.Act"); 
						icuAct = (int)dataTable.getData(tablePos, "Icu.Act"); 
						
						actIcu = (int)dataTable.getData(tablePos, "Act.Icu"); 
						intIcu = (int)dataTable.getData(tablePos, "Int.Icu"); 
						
						actInt = (int)dataTable.getData(tablePos, "Act.Int"); 
						icuInt = (int)dataTable.getData(tablePos, "Icu.Int"); 
						
						actDead = (int)dataTable.getData(tablePos, "Act.Dead"); 
						icuDead = (int)dataTable.getData(tablePos, "Icu.Dead");
						intDead = (int)dataTable.getData(tablePos, "Int.Dead"); 
						
						recovered = (int)dataTable.getData(tablePos, "R.New"); 
						
						List<TransferableElementContainer> changeList=
								getStateChangingList(activeList, actDead+actIcu+actInt+recovered, 
										d, "InfectionTime", new TriangularDistribution(6, 14, 41));
						
						List<TransferableElementContainer> icuChangeList =
								getStateChangingList(icuList, icuDead+icuAct+icuInt, 
										d, "InfectionTime", new TriangularDistribution(6, 14, 41));
						
						List<TransferableElementContainer> intChangeList =
								getStateChangingList(intList, intDead+intAct+intIcu, 
										d, "InfectionTime", new TriangularDistribution(6, 14, 41));
						
						changeState(engine, changeList, activeList, deadList, actDead, 
								intList, actInt, icuList, actIcu,
								healthyList, "Dead", "Intubated", "Incubated", "Recovered");
						
						changeState(engine, icuChangeList, icuList, deadList, icuDead, 
								intList, icuInt, null, icuAct, activeList, 
								"Dead", "Intubated", "","Svere");
						
						changeState(engine, intChangeList, intList, deadList, intDead, 
								icuList, intIcu, null, intAct, activeList, 
								"Dead", "", "Incubated", "Svere");
						
						/**
						 * Active to others
						 * 
						{
							for(TransferableElement e:changeList)
								EventList.activeList.remove(e);
							for (int i = 0; changeList.size()>0 && i < actDead; i++) {
								int pos=(int)Math.ceil(Math.random()*changeList.size());
								if(pos==changeList.size())pos--;
								EventList.deadList.add(changeList.get(pos));
								changeList.remove(pos);
							}
							for (int i = 0; changeList.size()>0 && i < actInt; i++) {
								int pos=(int)Math.ceil(Math.random()*changeList.size());
								if(pos==changeList.size())pos--;
								EventList.intList.add(changeList.get(pos));
								changeList.remove(pos);
							}
							EventList.icuList.addAll(changeList);
							changeList.clear();
						}*/
						
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				/*Distribution uniDist=new UniformDistribution(0, 1);
				int countHosp=0, countMild=0;
				
				for (TransferableElement entity : EventList.testList) {
					double num=uniDist.generateRandomNumber();
					if(num<0.17479 && countHosp<totalHosp) {
						countHosp++;
						EventList.hospitalizedList.add(entity);
						EventList.healthyList.remove(entity);
					} else if(countMild<totalMild) {
						countMild++;
						EventList.mildList.add(entity);
						EventList.healthyList.remove(entity);
					}
				}
				
				for (int i = countHosp; i < totalHosp; i++) {
					int ix=(int)RandomNumberAgent.generateUniformRandom(0, EventList.healthyList.size());
					TransferableElement entity=EventList.healthyList.get(ix);
					EventList.healthyList.remove(ix);
					EventList.hospitalizedList.add(entity);
					entity.setAttribute("InfectionTime", dS);
					entity.setState("Infected");
				}
				
				for (int i = countMild; i < totalMild; i++) {
					int ix=(int)RandomNumberAgent.generateUniformRandom(0, EventList.healthyList.size());
					TransferableElement entity=EventList.healthyList.get(ix);
					EventList.healthyList.remove(ix);
					EventList.mildList.add(entity);
					entity.setAttribute("InfectionTime", dS);
					entity.setState("Infected");
				}
				
				List<TransferableElement> activeList=new LinkedList<>();
				activeList.addAll(EventList.hospitalizedList);
				activeList.addAll(EventList.mildList);
				
				List<TransferableElement> bestList=new LinkedList<>();
				int listSize=actIcu;
				double minScore=Double.MAX_VALUE;
				Distribution actDist=new TriangularDistribution(6, 14, 41);
				TreeSet<EventList.EntityIndex> actIcuIndex=new TreeSet<>();
				
				for (TransferableElement entity : activeList) {
					double rand=Math.random();
					if(actIcuIndex.size()<listSize) {
						int days=dS-(int)(((int)entity.getAttribute("InfectionTime") -
								engine.getSimulationBeginTime().getTime())/EventList.dayMS);
						actIcuIndex.add(new EventList.EntityIndex(rand-actDist.p(days)));
						
					}
				}
				
				int size=0;
				for (int i = 0; i < Math.min(size,entityIndex.size()); i++) {
					int r=(int) RandomNumberAgent.generateUniformRandom(0, entityIndex.size());
					int ix=entityIndex.get(r);
					entityIndex.remove(r);
					eif.entities[ix].setState("Infected");
					int hours=EventList.generateTimeSpan(3, 5, 0, 24);
					simEngine.addEvent(new DiscreteEvent(10, eif.entities[ix], new TaskNode((TransferableElement) eif.entities[ix]),
							ComputationAgent.addTime(simulationTime, 
									TimeUnit.HOUR, hours), 3, EventType.UserDefinedEvent));
				}*/
			}
		});
		
		BigDate endTime=new BigDate(0);
		endTime.addYear(1);
		simEngine.setSimulationEndTime(endTime);
		
		System.out.println(beginDate+" "+simEngine.getSimulationEndTime());
		Thread.sleep(1000);
		
		System.out.println("beginnig....");
		/**simEngine.initEvent(new DiscreteEvent[]{new DiscreteEvent(0,null,new TaskNode(src), 
				new Date(2018, 5, 28, 8, 0),//beginDate, 
				EventType.ArrivalEvent)}, new Date(2018, 5, 29, 8, 0));//getDate(beginDate,1));*/
		long begin=System.currentTimeMillis();
		simEngine.run();
		long end=System.currentTimeMillis();
		System.out.println(begin+" "+end+" "+(((double)(end-begin))/1E3));

		/**for (Entity e : eif.entities) {
			System.out.println(e.getState());
		}*/
		
	}
}
