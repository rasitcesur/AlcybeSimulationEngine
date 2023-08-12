package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Process;

public class TransferEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	TaskBufferContainer taskBufferContainer;

	public TransferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode) {
		super(EventType.TransferEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
	}

	public TransferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate) {
		super(beginDate, EventType.TransferEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
	}

	public TransferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.TransferEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
	}
	
	public TransferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.TransferEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
	}
	
	public TransferEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.TransferEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
	}

	public TransferEvent(TaskBufferContainer taskBufferContainer) {
		super(EventType.TransferEvent);
		setTaskBufferContainer(taskBufferContainer);
		setEventType(EventType.TransferEvent);
	}

	public TransferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate) {
		super(beginDate, EventType.TransferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public TransferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.TransferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public TransferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.TransferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}
	
	public TransferEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.TransferEvent);
		setTaskBufferContainer(taskBufferContainer);
	}

	public TaskBufferContainer getTaskBufferContainer() {
		return taskBufferContainer;
	}

	public void setTaskBufferContainer(TaskBufferContainer taskBufferContainer) {
		this.taskBufferContainer = taskBufferContainer;
	}

}