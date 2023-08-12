package alcybe.simulation.model.parameters;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.OperationFinalizationEvent;
import alcybe.simulation.objects.TransferableElement;

public abstract class OperationParameter<T> extends ContinuousParameter<T>{
	private static final long serialVersionUID = 1L;
	
	public OperationFinalizationEvent operationFinalizationEvent;
	public Engine<?> simulationEngine;
	public BigDate duration;
	
	public void init(T value) {
		this.value=value;
	}
	
	public void cancel() {
		TransferableElementContainer elementContainer=operationFinalizationEvent.getTaskBufferContainer().getTransferableElementContainer();
		TransferableElement entity=elementContainer.getElement();
		entity.removeConcurrentOperationFinalizationEvent(operationFinalizationEvent);
		operationFinalizationEvent.cancel=true;
		OperationFinalizationEvent ofEvent = new OperationFinalizationEvent(operationFinalizationEvent.getTaskBufferContainer(), 
				simulationEngine.getSimulationTime());
		entity.addConcurrentOperationFinalizationEvent(ofEvent);
		this.simulationEngine.addEvent(ofEvent);
		super.continuousEvent.cancel=true;
	}
}
