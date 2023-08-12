package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.Task;

public class ExitEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	TransferableElementContainer elementContainer;
	TaskNode taskNode;
	Task task;
	
	public ExitEvent(TransferableElementContainer elementContainer, TaskNode taskNode) {
		super(EventType.ExitEvent);
		setElementContainer(elementContainer);
		setTaskNode(taskNode);
	}

	public ExitEvent(TransferableElementContainer elementContainer, TaskNode taskNode, 
			BigDate beginDate) {
		super(beginDate, EventType.ExitEvent);
		setElementContainer(elementContainer);
		setTaskNode(taskNode);
	}

	public ExitEvent(TransferableElementContainer elementContainer, TaskNode taskNode, 
			BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.ExitEvent);
		setElementContainer(elementContainer);
		setTaskNode(taskNode);
	}
	
	public ExitEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, 
			BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.ExitEvent);
		setElementContainer(elementContainer);
		setTaskNode(taskNode);
	}
	
	public ExitEvent(TransferableElementContainer elementContainer, Process process, TaskNode taskNode, 
			BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.ExitEvent);
		setElementContainer(elementContainer);
		setTaskNode(taskNode);
	}

	public TransferableElementContainer getElementContainer() {
		return elementContainer;
	}

	public void setElementContainer(TransferableElementContainer elementContainer) {
		this.elementContainer = elementContainer;
	}

	public TaskNode getTaskNode() {
		return taskNode;
	}

	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
