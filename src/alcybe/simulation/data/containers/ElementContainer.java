package alcybe.simulation.data.containers;

import java.io.Serializable;

import alcybe.simulation.objects.SimulationObject;

public abstract class ElementContainer<T extends SimulationObject> implements Serializable{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	protected transient T element;
	
	public ElementContainer() {}
	
	public ElementContainer(T element) {
		setElement(element);
	}
	
	public void setElement(T  element) {
		this.element=element;
	}
	
	public T getElement() {
		return this.element;
	}

}
