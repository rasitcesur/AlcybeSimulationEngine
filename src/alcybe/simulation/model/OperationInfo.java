package alcybe.simulation.model;

import alcybe.data.BigDate;

public class OperationInfo {
	
	public BigDate operationTime, setupTime;
	
	public OperationInfo() {}
	
	public OperationInfo(BigDate setupTime, BigDate operationTime) {
		setSetupTime(setupTime);
		setOperationTime(operationTime);
	}

	public BigDate getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(BigDate operationTime) {
		this.operationTime = operationTime;
	}

	public BigDate getSetupTime() {
		return setupTime;
	}

	public void setSetupTime(BigDate setupTime) {
		this.setupTime = setupTime;
	}
}
