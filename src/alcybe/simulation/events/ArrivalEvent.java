package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.objects.Source;

public class ArrivalEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Source source;

	public ArrivalEvent(Source source) {
		super(EventType.ArrivalEvent);
		setSource(source);
	}

	public ArrivalEvent(Source source, BigDate beginDate) {
		super(beginDate, EventType.ArrivalEvent);
		setSource(source);
	}

	public ArrivalEvent(Source source, BigDate beginDate, int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.ArrivalEvent);
		setSource(source);
	}
	
	public ArrivalEvent(Source source, BigDate beginDate, int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.ArrivalEvent);
		setSource(source);
	}
	
	public ArrivalEvent(Source source, BigDate beginDate, BigDate duration) {
		super(beginDate, duration, EventType.ArrivalEvent);
		setSource(source);
	}
	
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

}
