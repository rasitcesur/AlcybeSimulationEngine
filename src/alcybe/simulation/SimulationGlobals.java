package alcybe.simulation;

import java.util.Calendar;
import java.util.Date;


public class SimulationGlobals {
	
	public static String includeDefinitions="";
	
	public SimulationGlobals(){}
	
	public static long getTime(int amount, int timeUnit) {
		Calendar calendar=Calendar.getInstance();
		Date date=new Date(0);
		calendar.setTime(date);
		calendar.add(timeUnit, amount);
		return calendar.getTime().getTime();
	}
}
