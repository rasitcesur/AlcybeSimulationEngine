package alcybe.simulation;

import alcybe.data.BigDate;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.objects.SimulationObject;
import alcybe.simulation.types.Task;

public class IterationResult {
	
	public SimulationObject entity;
	public Task target;
	public BigDate begin, completion;
	public EventType eventType;
	
	public IterationResult() {  }
	
	public IterationResult(SimulationObject entity,Task target, BigDate begin, 
			BigDate completion, EventType eventType) { 
		setEntity(entity);
		setTarget(target);
		setBegin(begin);
		setCompletion(completion);
		setEventType(eventType);
	}

	public SimulationObject getEntity() {
		return entity;
	}

	public void setEntity(SimulationObject entity) {
		this.entity = entity;
	}

	public Task getTarget() {
		return target;
	}

	public void setTarget(Task target) {
		this.target = target;
	}

	public BigDate getBegin() {
		return begin;
	}

	public void setBegin(BigDate begin) {
		this.begin = begin;
	}

	public BigDate getCompletion() {
		return completion;
	}

	public void setCompletion(BigDate completion) {
		this.completion = completion;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
}
