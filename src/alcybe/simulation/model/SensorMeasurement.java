package alcybe.simulation.model;

public class SensorMeasurement implements Comparable<SensorMeasurement> {
	
	public double measurement;
	public long time;
	public String id;
	
	public SensorMeasurement() { }

	public SensorMeasurement(long time, double measurement) { 
		this.measurement=measurement;
		this.time=time;
	}
	
	public SensorMeasurement(String id, long time, double measurement) { 
		this(time, measurement);
		this.id=id;
	}

	public int compareTo(long time) {
		// TODO Auto-generated method stub
		return Long.compare(this.time, time);
	}
	
	@Override
	public int compareTo(SensorMeasurement container) {
		// TODO Auto-generated method stub
		/**int result=Long.compare(this.time, container.time);
		if(result==0) result=Double.compare(this.measurement, container.measurement);
		return result;*/
		return Long.compare(this.time, container.time);
	}

}
