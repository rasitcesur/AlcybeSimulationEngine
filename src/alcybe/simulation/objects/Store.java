package alcybe.simulation.objects;


import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Task;

public class Store extends Task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Store(){ }
	
	public OperationInfo getOperationTimeInfo(TaskNode operableElementContainer) throws Exception {
		return operableElementContainer.getOperationTimeInfo();
	}
	
	@Override
	public EventType getEventType() {
		return EventType.StoreEvent;
	}
}
