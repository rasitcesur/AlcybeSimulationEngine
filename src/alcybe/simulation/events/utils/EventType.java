package alcybe.simulation.events.utils;

public enum EventType{
	ArrivalEvent(0),
	AtTheEndUDEvent(10),
	ContinuousEvent(9),
	ClearBufferEvent(12),
	DispatchingEvent(1),
	DispatchingFinalizationEvent(2),
	ExitEvent(8),
	OperationEvent(5),
	OperationFinalizationEvent(6),
	TransportEvent(3),
	StoreEvent(7),
	TransferEvent(4),
	UserDefinedEvent(11);
	
	private final int index; 
	private EventType(final int index) { this.index=index; }
	public int getIndex() { return this.index; }
}