package alcybe.simulation.data.containers;

import alcybe.simulation.types.Task;

public class TaskContainer extends ElementContainer<Task> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TaskContainer() { }
	
	public TaskContainer(Task task) { 
		super.setElement(task);
	}
	

}
