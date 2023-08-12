package alcybe.simulation.objects.sensoradapters;

import java.net.URI;

import alcybe.utils.io.FileAgent;

public class FileAdapter extends SensorAdapter {
	
	private double[] data;
	private int index=-1;
	
	public FileAdapter(URI path) throws Exception {
		String text = FileAgent.readTextFile(path);
		String[] rows = text.split("\n");
		for (int i = 0; i < rows.length; i++)
			data[i]=Double.parseDouble(rows[i]);
	}

	@Override
	public double act() throws Exception {
		index++;
		index%=data.length;
		return data[index];
	}
	
}
