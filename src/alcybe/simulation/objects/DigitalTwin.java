package alcybe.simulation.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alcybe.script.AlcybeScriptEngine.ScriptType;
import alcybe.simulation.model.SensorMeasurement;
import alcybe.simulation.objects.sensoradapters.VirtualSensorAdapter;

public class DigitalTwin extends Sensor implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private Sensor pair=null;
	public double range;
	
	private VirtualSensorAdapter va=null;
	public List<Sensor> inputs=null;
	
	public DigitalTwin(String id, Sensor pair, ScriptType type, URI path, Sensor[] inputs, double range) throws Exception {
		super(id, type);
		initSensor(pair, "", inputs, range);
	}
	
	public DigitalTwin(String id, Sensor pair, ScriptType type, String script, Sensor[] inputs, double range) throws Exception {
		super(id, type);
		initSensor(pair, script, inputs, range);
	}
	
	protected void initSensor(Sensor pair, String script, Sensor[] inputs, double range) {
		this.pair=pair;
		va=(VirtualSensorAdapter) super.adapter;
		this.inputs=Arrays.asList(inputs);
		//for (Sensor sensor : inputs)
		//	sensor.virtualAgents.add((DigitalTwin)this);
		va.inputSize=inputs.length;
		va.script=script;
		this.range=range;
	}
	
	public void pulse(SensorMeasurement container) {
		VirtualSensorAdapter va=(VirtualSensorAdapter) super.adapter;
		va.sensorInputs.put(container.id, container);
	}
	
	@Override
	public SensorMeasurement measure() throws Exception {
		SensorMeasurement container = super.measure();
		if(pair!=null) {
			SensorMeasurement pairContainer=pair.measure();
			if(Math.abs(container.measurement-pairContainer.measurement)>range)
				System.out.println("Anomally Detected!\t"+pairContainer.id+
						"\t"+pairContainer.time+"\t"+container.measurement+" - "+pairContainer.measurement);
		}
		return container;
	}
	
	private int interval;
	final List<SensorMeasurement> measurementBuffer = new ArrayList<SensorMeasurement>();
	final List<Long> timeIndex=new ArrayList<>();
	public int indexRange=100;
	private int indexCursor=0;
	private int buffferCapacity=500;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int capacity=this.buffferCapacity+indexRange;
		while(true) {
			try {
				SensorMeasurement container=measure();
				System.out.println(container.id+":\t"+container.time+"\t"+container.measurement);
				synchronized (measurementBuffer) {
					measurementBuffer.add(container);
					indexCursor++;
					if(indexCursor==indexRange) {
						indexCursor=0;
						timeIndex.add(container.time);
					}
					if(measurementBuffer.size()==capacity) {
						for (int i = 0; i < indexRange; i++)
							measurementBuffer.remove(0);
						timeIndex.remove(0);
					}
				}
				for (Sensor s : inputs)
					pulse(s.measure());
				if(this.interval>0)
					Thread.sleep(this.interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
