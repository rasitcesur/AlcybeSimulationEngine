package alcybe.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.OperationFinalizationEvent;
import alcybe.simulation.events.SimulationEvent;
import alcybe.simulation.events.UserDefinedEvent;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.OperationParameter;
import alcybe.simulation.objects.TransferableElement;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.io.SerializationAgent;

public abstract class EngineBlock {
	
	protected Engine<?> engine;
	protected final HashMap<Long, Long> assemblyIndex=new HashMap<>();
	
	public EngineBlock(Engine<?> engine) {
		this.engine=engine;
	}
	
	@SuppressWarnings("unchecked")
	public void addOperationFinalizationEvent(TransferableElementContainer eContainer, TaskBufferContainer tContainer, BigDate eventDate) {
		OperationFinalizationEvent finalizationEvent = engine.addOperationFinalizationEvent(eContainer.getElement(), tContainer, new BigDate(eventDate.getTime())); 
		for(@SuppressWarnings("rawtypes") OperationParameter p:tContainer.getTaskNode().operationParameters) {
			p.init(p.value, engine, eContainer, tContainer.getTaskNode().getTaskContainer(), engine.getSimulationTime(), p.duration, eventDate);
			p.operationFinalizationEvent = finalizationEvent;
			p.simulationEngine=engine;			
		}
	}
	
	protected boolean fireAtTheBeginningEvents(int blockIndex, EventContainer eventContainer) {

		boolean cancel=false;
		if(eventContainer != null) {
			TaskNode taskNode=eventContainer.getTaskNode();
			if(taskNode!=null) {
			
				TaskContainer taskContainer = taskNode.getTaskContainer();
				if(taskContainer!=null) {
					Task target = taskContainer.getElement();
					LinkedList<Integer> beginningEvents=target.atTheBeginning.get(blockIndex);
					if(beginningEvents!=null)
						for(int i:beginningEvents) {
							UserDefinedEvent userEvent=engine.eventList.events[i];
							userEvent.act(engine, eventContainer);
							cancel |= userEvent.cancel;
						}
				}
			}
	
			TransferableElementContainer elementContainer = eventContainer.getTransferableElementContainer();
			if(elementContainer!=null) {
				TransferableElement entity = elementContainer.getElement();
				LinkedList<Integer> beginningEvents=entity.atTheBeginning.get(blockIndex);
				if(beginningEvents!=null)
					for(int i:beginningEvents) {
						UserDefinedEvent userEvent=engine.eventList.events[i];
						userEvent.act(engine, eventContainer);
						cancel |= userEvent.cancel;
					}
			}
		}
		
		return cancel;
	}
	
	protected TransferableElement cloneEntityFromTemplate(TransferableElement entity, 
			long arrivalSequence, long assemblySequence, long uniqueSequence) 
			throws ClassNotFoundException, IOException {
		
		TransferableElement tEntity=(TransferableElement) entity.clone();//SerializationAgent.clone(entity);
		tEntity.targetList = new ArrayList<>();
		tEntity.cursor=-1;
		tEntity.amount=((TransferableElement)entity).amount;
		tEntity.arrivalSequence=arrivalSequence;
		tEntity.uniqueSequence=uniqueSequence;
		tEntity.assemblySequence=assemblySequence;
		
		if(uniqueSequence%2500==0)
			System.out.println(uniqueSequence);
		
		for(TransferInfo info:((TransferableElement)entity).targetList) {
			try {
				TransferInfo transferInfo=(TransferInfo) 
						SerializationAgent.clone((TransferInfo)SerializationAgent.clone(info));
				tEntity.getTargetList().add(transferInfo);
				if(info.outputs!=null) 
					transferInfo.outputs=cloneElements(info.outputs);
	
				for (int i=0;i<transferInfo.processContainer.size();i++) {
					
					Process template=info.processContainer.get(i);
					Process c = transferInfo.processContainer.get(i);
					for (int j = 0; j < template.workcenters.size(); j++) {
						c.workcenters.get(j).setTaskContainer(template.workcenters.get(j).getTaskContainer());
						c.workcenters.get(j).operationParameters=template.workcenters.get(j).operationParameters;
					}
	 				long aSeq=0;
					if(assemblyIndex.containsKey(template.id))
						aSeq=assemblyIndex.get(template.id);
					else {
						long tempVal=0;
						for(long k:assemblyIndex.values())
							if(tempVal<k)
								tempVal=k;
						tempVal++;
						assemblyIndex.put(template.id, tempVal);
						aSeq=tempVal;
					}
					c.id=aSeq;
					//c.setTarget(template.getTarget());
					//c.inputs=new BomNode();
					if(template.inputs!=null) 
						c.inputs=template.inputs;//cloneElements(template.inputs);
					if(template.outputs!=null) 
						c.outputs=template.outputs;//cloneElements(template.outputs);
					
				}
			}catch(Exception e) { e.printStackTrace(); }
		}
		return tEntity;
	}
	
	protected AssemblyContainer[] cloneElements(AssemblyContainer[] elements) throws ClassNotFoundException, IOException {
		AssemblyContainer[] result=null;
		if(elements!=null) {
			result=new AssemblyContainer[elements.length];
			for (int x = 0; x < elements.length; x++) {
				result[x]=(AssemblyContainer) SerializationAgent.clone(elements[x]);
				result[x].entityContainer = elements[x].entityContainer;
				TransferableElement rEntity=result[x].getEntity();
				TransferableElement eEntity=elements[x].getEntity();
				if(eEntity==null)
					System.out.println("null");
				rEntity.amount = eEntity.amount;
				rEntity.targetList = eEntity.targetList;
				rEntity.cursor = -1;
				result[x].setEntity(rEntity);
				
			}
		}
		return result;
	}
	
	protected long entityUniqueSequence=0, entityArrivalSequence=0;
	
	protected long getNextEntityUniqueSequence() {
		entityUniqueSequence++;
		return entityUniqueSequence;
	}
	
	protected long getNextEntityArrivalSequence() {
		entityArrivalSequence++;
		return entityArrivalSequence;
	}
	
	public abstract IterationResult[] fireEvent(SimulationEvent event) throws Exception;
}
