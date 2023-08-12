package alcybe.simulation.events.utils;

import java.util.ArrayList;
import java.util.HashMap;

import alcybe.data.BigDate;
import alcybe.simulation.events.SimulationEvent;

public class SimulationEventCollection implements Comparable<SimulationEventCollection> {
	public HashMap<EventType, ArrayList<SimulationEvent>> eventList = new HashMap<>();
	
	public BigDate date;
	
	public int getEventCount() {
		return eventList.size();
	}
	
	public SimulationEventCollection(BigDate date) {
		this.date=date;
	}
	
	public void addEvent(SimulationEvent event){
		ArrayList<SimulationEvent> index=null;
		if(eventList.containsKey(event.eventType))
			index=eventList.get(event.eventType);
		else {
			index=new ArrayList<>(100_000);
			eventList.put(event.eventType, index);
		}
		index.add(event);
	}
	
	private static EventType[] eventTypeOrder= {EventType.UserDefinedEvent, EventType.AtTheEndUDEvent, EventType.ContinuousEvent, 
			EventType.ArrivalEvent, EventType.OperationFinalizationEvent,
			EventType.ClearBufferEvent,
			EventType.DispatchingEvent, EventType.DispatchingFinalizationEvent, 
			EventType.TransportEvent, EventType.TransferEvent, EventType.OperationEvent, EventType.StoreEvent,
			EventType.ExitEvent};
	
	public SimulationEvent iterateEvent() {
		
		ArrayList<SimulationEvent> q;
		SimulationEvent e=null;
		for (int i = 0; i < eventTypeOrder.length; i++) {
			if(eventList.containsKey(eventTypeOrder[i])) {
				q = eventList.get(eventTypeOrder[i]);
				e = q.get(0);
				q.remove(0);
				if(q.size()==0)
					eventList.remove(eventTypeOrder[i]);
				break;
			}
		}
		return e;
	}

	public ArrayList<SimulationEvent> iterateEvents() {
		
		ArrayList<SimulationEvent> q=null;
		for (int i = 0; i < eventTypeOrder.length; i++) {
			if(eventList.containsKey(eventTypeOrder[i])) {
				q = eventList.get(eventTypeOrder[i]);
				eventList.remove(eventTypeOrder[i]);
				break;
			}
		}
		return q;
	}
	
	@Override
	public int compareTo(SimulationEventCollection o) {
		// TODO Auto-generated method stub
		return this.date.compareTo(o.date);
	}
}
