package alcybe.simulation.model.parameters;

import java.io.Serializable;

public abstract class ModelParameter<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	Object[] parameters;
	
	public ModelParameter(Object... params){
		this.parameters=params;
	}
	
	public abstract T get();
}
