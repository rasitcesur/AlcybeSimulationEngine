package alcybe.simulation.events;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.events.utils.EventType;


public class SimulationEvent implements Comparable<SimulationEvent>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BigDate duration;
	public BigDate beginDate;
	public BigDate dueDate;
	public boolean cancel=false;
	public EventType eventType=EventType.ExitEvent;
	public final List<Integer> eventList = new LinkedList<>(); 
	
	public BigDate getDuration() {
		return duration;
	}
	
	public void setDuration(BigDate duration) {
		this.duration = duration;
		if(this.beginDate!=null) 
			this.dueDate=new BigDate(this.beginDate.getTime().add(this.duration.getTime()));
		
	}
	
	public BigDate getBeginDate() {
		return new BigDate(beginDate.getTime());
	}
	
	public void setBeginDate(BigDate beginDate) {
		this.beginDate = beginDate;
		if(this.duration!=null)
			this.dueDate=new BigDate(this.beginDate.getTime().add(this.duration.getTime()));
	}

	public BigDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(BigDate dueDate) {
		this.dueDate = dueDate;
		if(this.duration!=null)
			this.beginDate=new BigDate(this.dueDate.getTime().subtract(this.duration.getTime()));
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	@Override
	public int compareTo(SimulationEvent event) {
		// TODO Auto-generated method stub
		return this.beginDate.compareTo(event.beginDate);
	}
}
