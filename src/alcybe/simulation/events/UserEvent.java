package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.events.utils.EventType;

public class UserEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	EventContainer eventContainer;
	int[] events;
	
	public UserEvent(EventType eventType, EventContainer eventContainer, int userDefinedEvent) {
		super(eventType);
		setEventContainer(eventContainer);
		setEvents(new int[] {userDefinedEvent});
	}
	
	public UserEvent(EventType eventType, EventContainer eventContainer, int[] userDefinedEvents) {
		super(eventType);
		setEventContainer(eventContainer);
		setEvents(userDefinedEvents);
	}

	public UserEvent(EventType eventType, EventContainer eventContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, eventType);
		setEventContainer(eventContainer);
		setEvents(new int[] {userDefinedEvent});
	}
	
	public UserEvent(EventType eventType, EventContainer eventContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, eventType);
		setEventContainer(eventContainer);
		setEvents(userDefinedEvents);
	}
	
	public UserEvent(EventType eventType, EventContainer eventContainer, BigDate beginDate, BigDate duration, int userDefinedEvent) {
		super(beginDate, duration, eventType);
		setEventContainer(eventContainer);
		setEvents(new int[] {userDefinedEvent});
	}
	
	public UserEvent(EventType eventType, EventContainer eventContainer, BigDate beginDate, BigDate duration, int[] userDefinedEvents) {
		super(beginDate, duration, eventType);
		setEventContainer(eventContainer);
		setEvents(userDefinedEvents);
	}

	public EventContainer getEventContainer() {
		return eventContainer;
	}

	public void setEventContainer(EventContainer eventContainer) {
		this.eventContainer = eventContainer;
	}
	
	public int[] getEvents() {
		return events;
	}

	public void setEvents(int[] events) {
		this.events = events;
	}
}
