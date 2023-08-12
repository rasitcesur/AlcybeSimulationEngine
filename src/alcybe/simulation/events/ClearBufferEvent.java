package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Process;

public class ClearBufferEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	TaskBufferContainer taskBufferContainer;
	
	public ClearBufferEvent(TaskBufferContainer taskBufferContainer) {
		super(EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	
	public ClearBufferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate) {
		super(beginDate, EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public ClearBufferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, 
			BigDate beginDate) {
		super(beginDate, EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public ClearBufferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public ClearBufferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public ClearBufferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, 
			BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.ClearBufferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public TaskBufferContainer getTaskBufferContainer() {
		return taskBufferContainer;
	}

	public void setTaskBufferContainer(TaskBufferContainer taskBufferContainer) {
		this.taskBufferContainer = taskBufferContainer;
	}


}
