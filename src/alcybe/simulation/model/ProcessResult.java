package alcybe.simulation.model;

import alcybe.data.BigDate;

public class ProcessResult {
	
	public BigDate duration;
	public BigDate completionDate;
	public BigDate beginDate;
	
	public ProcessResult() {}
	
	public ProcessResult(BigDate duration, BigDate completionDate) {
		setDuration(duration);
		setCompletionDate(completionDate);
	}
	
	public ProcessResult(BigDate duration, BigDate beginDate , BigDate completionDate) {
		setDuration(duration);
		setCompletionDate(completionDate);
		setBeginDate(beginDate);
	}

	public BigDate getDuration() {
		return duration;
	}

	public void setDuration(BigDate duration) {
		this.duration = duration;
	}

	public BigDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(BigDate completionDate) {
		this.completionDate = completionDate;
	}

	public BigDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(BigDate beginDate) {
		this.beginDate = beginDate;
	}

}
