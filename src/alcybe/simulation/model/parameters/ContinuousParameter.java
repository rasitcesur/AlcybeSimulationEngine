package alcybe.simulation.model.parameters;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.events.ContinuousEvent;

public abstract class ContinuousParameter<T> extends ModelParameter<T>{
	private static final long serialVersionUID = 1L;
	public ContinuousEvent continuousEvent;
	public T value;

	public void init(T value, Engine<?> simulationEngine, TransferableElementContainer elementContainer, TaskContainer taskContainer, BigDate beginDate,
			BigDate duration, BigDate endDate) {
		this.value=value;
		if(endDate==null)
			continuousEvent=new ContinuousEvent(new EventContainer(elementContainer, taskContainer), beginDate, duration) {
			
				private static final long serialVersionUID = 1L;

				@Override
				public void act(Engine<?> engine, BigDate simulationTime) {
					this.cancel = update();
				}
			};
		else 
			continuousEvent=new ContinuousEvent(new EventContainer(elementContainer, taskContainer), beginDate, duration, endDate) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void act(Engine<?> engine, BigDate simulationTime) {
				this.cancel = update();
			}
		};
		simulationEngine.addEvent(continuousEvent);
	}
	
	public abstract boolean update();
	
}
