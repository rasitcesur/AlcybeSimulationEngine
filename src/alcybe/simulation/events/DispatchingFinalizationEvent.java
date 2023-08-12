package alcybe.simulation.events;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.types.Process;

public class DispatchingFinalizationEvent extends DiscreteEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	TransferableElementContainer elementContainer;
	Process process;

	public DispatchingFinalizationEvent(TransferableElementContainer elementContainer, alcybe.simulation.types.Process process) {
		super(EventType.DispatchingFinalizationEvent);
		setElementContainer(elementContainer);
		setProcess(process);
	}

	public DispatchingFinalizationEvent(TransferableElementContainer elementContainer, alcybe.simulation.types.Process process, BigDate beginDate) {
		super(beginDate, EventType.DispatchingFinalizationEvent);
		setElementContainer(elementContainer);
		setProcess(process);
	}

	public DispatchingFinalizationEvent(TransferableElementContainer elementContainer, alcybe.simulation.types.Process process, BigDate beginDate, 
			int userDefinedEvent) {
		super(beginDate, userDefinedEvent, EventType.DispatchingFinalizationEvent);
		setElementContainer(elementContainer);
		setProcess(process);
	}
	
	public DispatchingFinalizationEvent(TransferableElementContainer elementContainer, alcybe.simulation.types.Process process, BigDate beginDate, 
			int[] userDefinedEvents) {
		super(beginDate, userDefinedEvents, EventType.DispatchingFinalizationEvent);
		setElementContainer(elementContainer);
		setProcess(process);
	}
	
	public DispatchingFinalizationEvent(TransferableElementContainer elementContainer, alcybe.simulation.types.Process process, BigDate beginDate, 
			BigDate duration) {
		super(beginDate, duration, EventType.DispatchingFinalizationEvent);
		setElementContainer(elementContainer);
		setProcess(process);
	}

	public TransferableElementContainer getElementContainer() {
		return elementContainer;
	}

	public void setElementContainer(TransferableElementContainer elementContainer) {
		this.elementContainer = elementContainer;
	}

	public alcybe.simulation.types.Process getProcess() {
		return process;
	}

	public void setProcess(alcybe.simulation.types.Process process) {
		this.process = process;
	}
}
