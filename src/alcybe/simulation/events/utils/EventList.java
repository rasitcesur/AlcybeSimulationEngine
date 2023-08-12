package alcybe.simulation.events.utils;

import java.lang.reflect.Method;
import java.util.HashMap;

import alcybe.simulation.events.UserDefinedEvent;

public abstract class EventList {
	
	public UserDefinedEvent[] events= null;
	protected final Class<?> me;
	final HashMap<String, Method> methods=new HashMap<>();
	
	abstract public void init();
	
	public EventList() {
		me=this.getClass();
		for(Method m:me.getMethods())
			methods.put(m.getName(), m);
		init();
	}
	
	public Method getMethod(String name) {
		return methods.get(name);
	}
	
	public Object callFunction(String functionName, Object... parameters) {
		try {
			return getMethod(functionName).invoke(this, parameters);
		} catch (Exception e) {
			return null;
		}
	}
}
