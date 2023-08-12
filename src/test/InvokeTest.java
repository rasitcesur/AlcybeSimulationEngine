package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import alcybe.simulation.Engine;
import alcybe.simulation.events.utils.EventList;

public class InvokeTest {

	public static final EventList el=new EventList() {
		
		public void writeMessage(String message) {
			System.out.println(message);
		}
		
		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object callFunction(String functionName, Object... parameters) {
			try {
				return getMethod(functionName).invoke(this, parameters);
			} catch (Exception e) {
				return null;
			}
		}
	};
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		
		Engine en=new Engine();
		en.eventList=el;
		en.callFunction("writeMessage", "ghjklkj");
		el.getMethod("writeMessage").invoke(el,"asdfgh");
		for(Method m:el.getClass().getDeclaredMethods()) {
			if(m.getName().equals("writeMessage")) {
				System.out.println("writeMessage is detected!");
				try {
					m.invoke(el, "asd");
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
