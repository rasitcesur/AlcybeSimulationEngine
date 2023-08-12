package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.events.utils.EventType;

public abstract class ContinuousEvent extends SimulationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	EventContainer eventContainer;
	public BigDate endDate;
	
	public abstract void act(Engine<?> engine, BigDate simulationTime);
	
	public void setDuration(BigDate duration) {
		this.duration = duration;
	}

	public ContinuousEvent() {
		super.setEventType(EventType.ContinuousEvent);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate){
		this();
		setEventContainer(eventContainer);
		super.setBeginDate(beginDate);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate, int[] userDefinedEvents){
		this(eventContainer, beginDate);
		for (int i : userDefinedEvents)
			super.eventList.add(i);
	}
	
	public ContinuousEvent(BigDate beginDate, BigDate duration) {
		this();
		setBeginDate(beginDate);
		setDuration(duration);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate, BigDate duration){
		this(eventContainer, beginDate);
		setDuration(duration);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate, BigDate duration, int[] userDefinedEvents){
		this(eventContainer, beginDate, userDefinedEvents);
		setDuration(duration);
	}
	
	public ContinuousEvent(EventContainer eventContainer,
			BigDate beginDate, BigDate duration,  int userDefinedEvent){
		this(eventContainer, beginDate, duration, new int[] {userDefinedEvent});
	}
	
	public ContinuousEvent(BigDate beginDate, BigDate duration, BigDate endDate) {
		this(beginDate, duration);
		setEndDate(endDate);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate, BigDate duration, BigDate endDate){
		this(eventContainer, beginDate, duration);
		setEndDate(endDate);
	}
	
	public ContinuousEvent(EventContainer eventContainer, BigDate beginDate, BigDate duration, BigDate endDate, int[] userDefinedEvents){
		this(eventContainer, beginDate, duration, userDefinedEvents);
		setEndDate(endDate);
	}
	
	public ContinuousEvent(EventContainer eventContainer,
			BigDate beginDate, BigDate duration,  BigDate endDate, int userDefinedEvent){
		this(eventContainer, beginDate, duration, new int[] {userDefinedEvent});
		setEndDate(endDate);
	}
	
	public ContinuousEvent(EventContainer eventContainer,
			BigDate beginDate, int userDefinedEvent){
		this(eventContainer, beginDate, new int[] {userDefinedEvent});
	}

	@Override
	public int compareTo(SimulationEvent event) {
		// TODO Auto-generated method stub
		return this.beginDate.compareTo(event.beginDate);
	}

	public EventContainer getEventContainer() {
		return eventContainer;
	}

	public void setEventContainer(EventContainer eventContainer) {
		this.eventContainer = eventContainer;
	}

	public BigDate getEndDate() {
		return endDate;
	}

	public void setEndDate(BigDate endDate) {
		this.endDate = endDate;
	}

	/*public SimulationObject getTarget() {
		return target;
	}

	public void setTarget(SimulationObject target) {
		this.target = target;
	}*/
}
