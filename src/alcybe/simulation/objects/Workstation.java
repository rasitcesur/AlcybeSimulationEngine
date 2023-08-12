package alcybe.simulation.objects;

import alcybe.data.BigDate;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.ProcessResult;
import alcybe.simulation.model.Shift;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Task;

public class Workstation extends Task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Workstation(){ 
		super();
	}
	
	public Workstation(Object identity){ 
		setIdentity(identity);
	}
	
	public OperationInfo getOperationTimeInfo(TaskNode operableElementContainer) throws Exception {
		return operableElementContainer.getOperationTimeInfo();
	}
	
	@Override
	public EventType getEventType() {
		return EventType.OperationEvent;
	}
	
	private Object lastIdentity;
	@Override
	public ProcessResult process(TaskNode operableElementContainer, SimulationObject entity, 
			BigDate simulationTime) throws Exception {
		// TODO Auto-generated method stub
		
		OperationInfo info=getOperationTimeInfo(operableElementContainer);
		
		BigDate d=new BigDate(simulationTime.getTime());
		taskDuration=new BigDate(0);
		taskDuration.add(info.operationTime);
		taskDuration.add(info.setupTime);
		/**if(lastIdentity==null || entity.getIdentity()==null || lastIdentity.equals(entity.getIdentity())){
			taskDuration.add(info.setupTime);
		}*/
		d.add(taskDuration);
		
		
		BigDate[] operationDurationInfo = Shift.getIdleDuration(simulationTime, d, shifts);
		
		lastIdentity=entity.getIdentity();
		//TODO create a class for generating statistics 
		
		taskStatistics.totalAmount+=((TransferableElement)entity).amount;
		taskStatistics.processingCount++;
		taskStatistics.totalDuration.add(taskDuration);
		
		//taskStatistics.utilizationMeasure+=taskDuration*(getAssignedCapacity()/bufferCapacity);
		
		//TransferableElement entityProxy=(TransferableElement) entity;
		/*boolean makeSetup=false;
		switch(SetupStrategy.valueOf(this.setupStrategy)){
		case NoSetup:
			break;
		case SetupAfterCycleEnds:
			if(!previousElementIndex.containsKey(event.getObject().identifier))
				makeSetup=true;
			break;
		case SetupAfterEachOperation:
			makeSetup=true;
			break;
		case SetupAfterSpecifiedAmount:
			te=(TransferableElement) event.getObject();
			if(evaluateTreshold(te.identifier))
				makeSetup=true;
			break;
		case SetupRegardingBufferCapacity:
			if(getRemainingBufferCapacity()<previousElementIndex.size()){
				makeSetup=true;
				clearPreviousElements();
			}
			break;
		case SetupRegardingCycleAndAmount:
			te=(TransferableElement) event.getObject();
			if(evaluateTreshold(te.identifier))
				makeSetup=true;
			break;
		default:
			break;
		}
		
		if(makeSetup)
			c.add(TimeUnit.valueOf(setupTimeUnit).getUnitValue(),
					(int)(evalScript(setupTime, new String[] {"result"})[0]));
		
		//operation = new Timestamp(c.getTime().getTime());
		
		runScript(endOperationEvent);
		*/
		return new ProcessResult(taskDuration, operationDurationInfo[0], operationDurationInfo[1]);//simulationTime, d);
	}
}
