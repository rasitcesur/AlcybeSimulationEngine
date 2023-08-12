package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.events.utils.EventType;

public class OperationEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	TaskBufferContainer taskBufferContainer;
	TaskContainer taskContainer;
	
	public OperationEvent(EventType eventType, TaskBufferContainer taskBufferContainer, TaskContainer taskContainer) {
		super(eventType);
		setTaskBufferContainer(taskBufferContainer);
		setTaskContainer(taskContainer);
	}

	public OperationEvent(EventType eventType, TaskBufferContainer taskBufferContainer, TaskContainer taskContainer, BigDate beginDate) {
		super(beginDate, eventType);
		setTaskBufferContainer(taskBufferContainer);
		setTaskContainer(taskContainer);
	}

	public OperationEvent(EventType eventType, TaskBufferContainer taskBufferContainer, TaskContainer taskContainer,
			BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, eventType);
		setTaskBufferContainer(taskBufferContainer);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskBufferContainer taskBufferContainer, TaskContainer taskContainer, 
			BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, eventType);
		setTaskBufferContainer(taskBufferContainer);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskBufferContainer taskBufferContainer, TaskContainer taskContainer,
			BigDate beginDate, BigDate duration) {
		super(beginDate, duration, eventType);
		setTaskBufferContainer(taskBufferContainer);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskContainer taskContainer) {
		super(eventType);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskContainer taskContainer, BigDate beginDate) {
		super(beginDate, eventType);
		setTaskContainer(taskContainer);
	}

	public OperationEvent(EventType eventType, TaskContainer taskContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, eventType);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskContainer taskContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, eventType);
		setTaskContainer(taskContainer);
	}
	
	public OperationEvent(EventType eventType, TaskContainer taskContainer, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, eventType);
		setTaskContainer(taskContainer);
	}

	public TaskBufferContainer getTaskBufferContainer() {
		return taskBufferContainer;
	}

	public void setTaskBufferContainer(TaskBufferContainer taskBufferContainer) {
		this.taskBufferContainer = taskBufferContainer;
	}

	public TaskContainer getTaskContainer() {
		return taskContainer;
	}

	public void setTaskContainer(TaskContainer taskContainer) {
		this.taskContainer = taskContainer;
	}
}
