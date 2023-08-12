package alcybe.simulation.model.parameters;

import alcybe.data.BigDate;
import alcybe.utils.data.TimeUnit;

public class FixedTimeParameter extends ModelParameter<BigDate>{
	private static final long serialVersionUID = 1L;
	TimeUnit timeUnit;
	long time;
	
	public FixedTimeParameter(long time, TimeUnit timeUnit) {
		this.time=time;
		this.timeUnit=timeUnit;
	}

	@Override
	public BigDate get() {
		BigDate date=new BigDate(0);
		date.add(time, timeUnit);
		return date;
	}
	
	
}
