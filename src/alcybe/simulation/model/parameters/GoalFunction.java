package alcybe.simulation.model.parameters;

public abstract class GoalFunction<T> extends ModelParameter<T>{
	private static final long serialVersionUID = 1L;
	public T goalValue;
	
	public abstract void update(T value);
	
}
