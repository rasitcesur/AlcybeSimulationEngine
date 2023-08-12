package alcybe.simulation.events;

import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.types.Task;

public abstract class UserDefinedEvent extends SimulationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Task[] targets;
	
	public UserDefinedEvent() { }
	
	
	public UserDefinedEvent(Task... targets) { 
		 setTargets(targets);
	}

	public Task[] getTargets() {
		return targets;
	}

	public void setTargets(Task... targets) {
		this.targets = targets;
	}

	public abstract void act(Engine simulationEngine, EventContainer eventContainer);
}
