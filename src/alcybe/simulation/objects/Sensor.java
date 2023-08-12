package alcybe.simulation.objects;

import java.net.URI;

import alcybe.data.DataTable;
import alcybe.script.AlcybeScriptEngine.ScriptType;
import alcybe.simulation.model.SensorMeasurement;
import alcybe.simulation.objects.sensoradapters.FileAdapter;
import alcybe.simulation.objects.sensoradapters.SensorAdapter;
import alcybe.simulation.objects.sensoradapters.TableAdapter;
import alcybe.simulation.objects.sensoradapters.VirtualSensorAdapter;

public class Sensor extends SimulationObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected SensorAdapter adapter = null;
	private String id;
	public String definition;
	
	public Sensor() { }
	
	public Sensor(String id, String address) throws Exception { }
	
	public Sensor(String id, int param) throws Exception { }
	
	public Sensor(String id, String address, int param) throws Exception { }
	
	public Sensor(String id, DataTable dataTable, int measurementColumnIndex, int timeColumnIndex) throws Exception { 
		adapter=new TableAdapter(dataTable, measurementColumnIndex, timeColumnIndex);
		setId(id);
	}
	
	public Sensor(String id, URI path) throws Exception {
		adapter=new FileAdapter(path);
		setId(id);
	}
	
	public Sensor(String id, ScriptType type) throws Exception {
		adapter = new VirtualSensorAdapter(type);
		setId(id);
	}
	
	public SensorMeasurement measure() throws Exception{
		SensorMeasurement container = adapter.measure();
		container.id=id;
		return container;		
	}
	
	public void setId(String id) {
		this.id=id;
	}
	
	public String getId() {
		return this.id;
	}
	
}
