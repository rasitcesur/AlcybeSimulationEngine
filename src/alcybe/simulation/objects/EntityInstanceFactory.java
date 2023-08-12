package alcybe.simulation.objects;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.DispatchingEvent;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.FixedTimeParameter;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;

public class EntityInstanceFactory extends SimulationObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TaskContainer[][][] workstationList=null;
	public long[][][] operationTime=null;
	public TransferableElementContainer[] entities=null;
	
	public void initArrival(int count, Engine<?> engine, BigDate simulationBeginTime) 
			throws Exception {
		initArrival(count, engine, simulationBeginTime, null, null, null);
	}
	
	public void initArrival(int count, Engine<?> engine, BigDate simulationBeginTime, int[] eventBlockIndex, int[] eventIndex,
			Object state) throws Exception {
		TransferableElementContainer[] entityList=new TransferableElementContainer[count];
		for (int i = 0; i < entityList.length; i++)
			entityList[i]=new TransferableElementContainer(new Entity());
		initArrival(entityList, engine, simulationBeginTime, eventBlockIndex, eventIndex, state);
	}
	
	public void initArrival(TransferableElementContainer[] entityList, Engine<?> engine, BigDate simulationBeginTime, int[] eventBlockIndex, int[] eventIndex,
			Object state) 
			throws Exception {
		entities=entityList;
		engine.setSimulationBeginTime(simulationBeginTime);
		for (int i = 0; i < entities.length; i++) {
			TransferableElement entity=entities[i].getElement();
			entity.setIdentity(i);
			if(state!=null)
				entity.setState(state);
			if(eventBlockIndex!=null && eventIndex!=null) {
				if(eventBlockIndex.length!=eventIndex.length)
					throw new Exception("Length of EventBlockIndex and eventIndex must be equal.");
				for (int j = 0; j < eventIndex.length; j++) {
					entity.insertUDEventAtTheBeginning(eventBlockIndex[j], eventIndex[j]);
				}
			}
			Process[] processList=new Process[workstationList[i].length];
			for (int j = 0; j < processList.length; j++) {
				TaskNode[] nodeList=new TaskNode[workstationList[i][j].length];
				for (int k = 0; k < nodeList.length; k++) 
					nodeList[k]=new TaskNode(workstationList[i][j][k], new FixedTimeParameter(operationTime[i][j][k], TimeUnit.second));
				processList[j]=new Process(nodeList);
			}
			entity.addTarget(new TransferInfo(processList));
			TransferInfo transferInfo=entity.getNextTarget();
			entity.concurrentEventCount = transferInfo.getProcessContainers().size();
			if(transferInfo!=null)
				engine.addEvent(new DispatchingEvent(entities[i], simulationBeginTime));
			if(i%1000==0)
				System.out.println(i);
		}
	}
}
