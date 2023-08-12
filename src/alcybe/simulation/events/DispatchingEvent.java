package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;

public class DispatchingEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	TransferableElementContainer elementContainer;

	public DispatchingEvent(TransferableElementContainer elementContainer) {
		super(EventType.DispatchingEvent);
		setElementContainer(elementContainer);
	}

	public DispatchingEvent(TransferableElementContainer elementContainer, BigDate beginDate) {
		super(beginDate, EventType.DispatchingEvent);
		setElementContainer(elementContainer);
	}

	public DispatchingEvent(TransferableElementContainer elementContainer, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.DispatchingEvent);
		setElementContainer(elementContainer);
	}
	
	public DispatchingEvent(TransferableElementContainer elementContainer, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.DispatchingEvent);
		setElementContainer(elementContainer);
	}
	
	public DispatchingEvent(TransferableElementContainer elementContainer, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.DispatchingEvent);
		setElementContainer(elementContainer);
	}

	public TransferableElementContainer getElementContainer() {
		return elementContainer;
	}

	public void setElementContainer(TransferableElementContainer elementContainer) {
		this.elementContainer = elementContainer;
	}
	
	

}
