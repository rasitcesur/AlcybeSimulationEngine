package alcybe.simulation.data.containers;

import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Process;

public class TaskBufferContainer {
	
	TaskNode taskNode;
	Process process;
	TransferableElementContainer transferableElementContainer;
	
	public TaskBufferContainer() { }
	
	public TaskBufferContainer(TransferableElementContainer container) { 
		setTransferableElementContainer(container);
	}
	
	public TaskBufferContainer(TaskNode taskNode) { 
		setTaskNode(taskNode);
	}
	
	public TaskBufferContainer(TransferableElementContainer container, TaskNode taskNode) { 
		this(taskNode);
		setTransferableElementContainer(container);
	}
	
	public TaskBufferContainer(TransferableElementContainer container, TaskNode taskNode, Process process) { 
		this(container, taskNode);
		setProcess(process);
	}

	public TaskNode getTaskNode() {
		return taskNode;
	}

	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public TransferableElementContainer getTransferableElementContainer() {
		return transferableElementContainer;
	}

	public void setTransferableElementContainer(TransferableElementContainer transferableElementContainer) {
		this.transferableElementContainer = transferableElementContainer;
	}
}



