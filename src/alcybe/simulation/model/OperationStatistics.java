package alcybe.simulation.model;

import java.io.Serializable;

import alcybe.data.BigDate;

public class OperationStatistics implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double totalAmount=0d, utilizationMeasure=0;
	public BigDate totalDuration=new BigDate(0), totalSetUpDuration=new BigDate(0);
	public long processingCount=0;
	
	public OperationStatistics() {}
	
	public OperationStatistics(double totalAmount) {
		this();
		setTotalAmount(totalAmount);
	}

	public OperationStatistics(double totalAmount, BigDate totalDuration, long processingCount) {
		this(totalAmount);
		setTotalDuration(totalDuration);
		setProcessingCount(processingCount);
	}
	
	public OperationStatistics(double totalAmount, BigDate totalDuration, BigDate totalSetUpDuration, long processingCount) {
		this(totalAmount, totalDuration, processingCount);
		setTotalSetUpDuration(totalSetUpDuration);
	}
	
	public OperationStatistics(double totalAmount, BigDate totalDuration, BigDate totalSetUpDuration, long processingCount,
			double utilizationMeasure) {
		this(totalAmount, totalDuration, totalSetUpDuration, processingCount);
		setUtilizationMeasure(utilizationMeasure);
	}
	
	public void updateTotalAmount(double amount){
		this.totalAmount+=amount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	
	public BigDate getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(BigDate totalDuration) {
		this.totalDuration = totalDuration;
	}

	public long getProcessingCount() {
		return processingCount;
	}

	public void setProcessingCount(long processingCount) {
		this.processingCount = processingCount;
	}

	public BigDate getTotalSetUpDuration() {
		return totalSetUpDuration;
	}

	public void setTotalSetUpDuration(BigDate totalSetUpDuration) {
		this.totalSetUpDuration = totalSetUpDuration;
	}

	public double getUtilizationMeasure() {
		return utilizationMeasure;
	}

	public void setUtilizationMeasure(double utilizationMeasure) {
		this.utilizationMeasure = utilizationMeasure;
	}
}
