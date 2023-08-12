package alcybe.simulation.objects.sensoradapters;

import alcybe.data.DataTable;
import alcybe.simulation.model.SensorMeasurement;

public class TableAdapter extends SensorAdapter {
	
	private DataTable dataTable;
	private int index=-1, measurementIndex, timeIndex;
	public TableAdapter(DataTable dataTable, int measurementIndex, int timeIndex) throws Exception {
		this.dataTable=dataTable;
		this.measurementIndex=measurementIndex;
		this.timeIndex=timeIndex;
	}

	@Override
	public SensorMeasurement measure() throws Exception {
		// TODO Auto-generated method stub
		double measurement=act();
		return new SensorMeasurement(null, (long)dataTable.getData(index, timeIndex), measurement);
	}
	
	@Override
	public double act() throws Exception {
		index++;
		index%=dataTable.getRowCount();
		return (double)dataTable.getData(index, measurementIndex);
	}	
}
