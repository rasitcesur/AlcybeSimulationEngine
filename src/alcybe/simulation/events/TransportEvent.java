package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Process;

public class TransportEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	TaskBufferContainer taskBufferContainer;
	BigDate interval=new BigDate(1000);
	public TransferEvent transferEvent=null;

	public TransportEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate interval) {
		super(EventType.TransportEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
		setInterval(interval);
	}

	public TransportEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, BigDate interval) {
		super(beginDate, EventType.TransportEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
		setInterval(interval);
	}

	public TransportEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, int userDefinedEvent, 
			BigDate interval) {
		super(beginDate, userDefinedEvent, EventType.TransportEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
		setInterval(interval);
	}
	
	public TransportEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, int[] userDefinedEvents, 
			BigDate interval) {
		super(beginDate, userDefinedEvents, EventType.TransportEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
		setInterval(interval);
	}
	
	public TransportEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, BigDate beginDate, BigDate duration, 
			BigDate interval) {
		super(beginDate, duration, EventType.TransportEvent);
		setTaskBufferContainer(new TaskBufferContainer(elementContainer, taskNode, process));
		setInterval(interval);
	}

	public TransportEvent(TaskBufferContainer taskBufferContainer, BigDate interval) {
		super(EventType.TransportEvent);
		setTaskBufferContainer(taskBufferContainer);
		setEventType(EventType.TransportEvent);
		setInterval(interval);
	}

	public TransportEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, BigDate interval) {
		super(beginDate, EventType.TransportEvent);
		setTaskBufferContainer(taskBufferContainer);
		setInterval(interval);
	}

	public TransportEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int userDefinedEvent, BigDate interval) {
		super(beginDate, userDefinedEvent, EventType.TransportEvent);
		setTaskBufferContainer(taskBufferContainer);
		setInterval(interval);
	}
	
	public TransportEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, int[] userDefinedEvents, BigDate interval) {
		super(beginDate, userDefinedEvents, EventType.TransportEvent);
		setTaskBufferContainer(taskBufferContainer);
		setInterval(interval);
	}
	
	public TransportEvent(TaskBufferContainer taskBufferContainer, BigDate beginDate, BigDate duration, BigDate interval) {
		super(beginDate, duration, EventType.TransportEvent);
		setTaskBufferContainer(taskBufferContainer);
		setInterval(interval);
	}

	public TaskBufferContainer getTaskBufferContainer() {
		return taskBufferContainer;
	}

	public void setTaskBufferContainer(TaskBufferContainer taskBufferContainer) {
		this.taskBufferContainer = taskBufferContainer;
	}

	public BigDate getInterval() {
		return interval;
	}

	public void setInterval(BigDate interval) {
		this.interval = interval;
	}

}