package alcybe.simulation.objects.sensoradapters;

import alcybe.simulation.model.SensorMeasurement;

public abstract class SensorAdapter {

	abstract public double act() throws Exception;
	
	public SensorMeasurement measure() throws Exception {
		long begin = System.currentTimeMillis();
		double measurement=act();
		long end=System.currentTimeMillis();
		long time=end/2+begin/2;
		boolean isOdd = ((end<<63)>>63) == -1 || ((begin<<63)>>63) == -1; 
		if(isOdd) time++;
		return new SensorMeasurement(null, time, measurement);
	}
}
