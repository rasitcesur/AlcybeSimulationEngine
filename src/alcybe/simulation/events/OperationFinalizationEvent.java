package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.events.utils.EventType;

public class OperationFinalizationEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TaskBufferContainer taskBufferContainer;

	public OperationFinalizationEvent(TaskBufferContainer taskBufferContainer) {
		super(EventType.OperationFinalizationEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public OperationFinalizationEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate) {
		super(beginDate, EventType.OperationFinalizationEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public OperationFinalizationEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.OperationFinalizationEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public OperationFinalizationEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.OperationFinalizationEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public OperationFinalizationEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.OperationFinalizationEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public TaskBufferContainer getTaskBufferContainer() {
		return taskBufferContainer;
	}

	public void setTaskBufferContainer(TaskBufferContainer taskBufferContainer) {
		this.taskBufferContainer = taskBufferContainer;
	}
	
	

}
