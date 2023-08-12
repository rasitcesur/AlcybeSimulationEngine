package alcybe.simulation.data.containers;

import alcybe.simulation.model.TaskNode;
import alcybe.simulation.objects.SimulationObject;
import alcybe.simulation.types.Process;

public class EventContainer extends ElementContainer<SimulationObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TransferableElementContainer transferableElementContainer;
	public Process process; 
	public TaskNode taskNode;
	public TaskContainer taskContainer;

	public EventContainer() { }
	
	public EventContainer(TransferableElementContainer transferableElementContainer) {
		setTransferableElementContainer(transferableElementContainer);
	}
	
	public EventContainer(TransferableElementContainer transferableElementContainer, TaskContainer taskContainer) {
		setTransferableElementContainer(transferableElementContainer);
	}
	
	public EventContainer(TaskNode taskNode) {
		setTaskNode(taskNode);
	}
	
	public EventContainer(TransferableElementContainer transferableElementContainer, Process process) {
		setTransferableElementContainer(transferableElementContainer);
		setProcess(process);
	}
	
	public EventContainer(TransferableElementContainer transferableElementContainer, Process process, TaskNode taskNode) {
		setTransferableElementContainer(transferableElementContainer);
		setProcess(process);
		setTaskNode(taskNode);
	}
	
	public TransferableElementContainer getTransferableElementContainer() {
		return transferableElementContainer;
	}

	public void setTransferableElementContainer(TransferableElementContainer transferableElementContainer) {
		this.transferableElementContainer = transferableElementContainer;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}
	
	public TaskNode getTaskNode() {
		return taskNode;
	}

	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}

	@Override
	public void setElement(SimulationObject element) { }

	@Override
	public SimulationObject getElement() {
		return null;
	}

	public TaskContainer getTaskContainer() {
		return taskContainer;
	}

	public void setTaskContainer(TaskContainer taskContainer) {
		this.taskContainer = taskContainer;
	}
	
}
