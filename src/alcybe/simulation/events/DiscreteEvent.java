package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.events.utils.EventType;

public class DiscreteEvent extends SimulationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void setDuration(BigDate duration) {
		this.duration = duration;
	}
	
	public DiscreteEvent(EventType eventType) {
		super.setEventType(eventType);
	}
	
	public DiscreteEvent(BigDate beginDate, BigDate duration, EventType eventType){
		this(beginDate, eventType);
		setDuration(duration);
	}	
	
	public DiscreteEvent(BigDate beginDate, EventType eventType){
		super.setBeginDate(beginDate);
		super.setEventType(eventType);
	}
	
	public DiscreteEvent(BigDate beginDate, int[] userDefinedEvents, EventType eventType){
		this(beginDate, eventType);
		for (int i : userDefinedEvents)
			super.eventList.add(i);
	}
	
	public DiscreteEvent(BigDate beginDate, int userDefinedEvent, EventType eventType){
		this(beginDate, new int[] {userDefinedEvent}, eventType);
	}
	/*public SimulationObject getTarget() {
		return target;
	}

	public void setTarget(SimulationObject target) {
		this.target = target;
	}*/
}
