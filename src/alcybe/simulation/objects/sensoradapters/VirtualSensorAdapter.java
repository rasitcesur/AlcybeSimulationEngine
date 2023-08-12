package alcybe.simulation.objects.sensoradapters;


import java.util.HashMap;
import java.util.Map.Entry;

import alcybe.script.ScriptAgent;
import alcybe.script.AlcybeScriptEngine;
import alcybe.script.AlcybeScriptEngine.ScriptType;
import alcybe.simulation.model.SensorMeasurement;

public class VirtualSensorAdapter extends SensorAdapter {

	public final HashMap<String, SensorMeasurement> sensorInputs=new HashMap<>();
	ScriptAgent scriptManager;
	AlcybeScriptEngine engine;
	public String script="";
	public int inputSize;
	
	public VirtualSensorAdapter(ScriptType type) {
		scriptManager=new ScriptAgent(type);
		engine = scriptManager.getEngine();
	}
	

	@Override
	public double act() throws Exception {
		// TODO Auto-generated method stub
		double result;
		if(inputSize == sensorInputs.size()) {
			for (Entry<String, SensorMeasurement> e : sensorInputs.entrySet())
				engine.putVar(e.getKey(), e.getValue().measurement);	
			engine.runScript(script);
			result=(double)engine.getVar("measurement");		
		} else
			result=Double.MIN_VALUE;
		return result;
	}
}
