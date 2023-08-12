package alcybe.simulation.model;

import java.util.ArrayList;
import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.data.containers.TransferableElementContainer;
import alcybe.simulation.model.parameters.ModelParameter;
import alcybe.simulation.model.parameters.OperationParameter;
import alcybe.simulation.objects.SerializableObject;
import alcybe.simulation.types.Task;

public class TaskNode extends SerializableObject{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public transient TaskContainer taskContainer; //TODO transient
	public boolean hasOutput=false;
	public long processSequence=0;
	public double priority;
	public ModelParameter<BigDate> operationTime, setupTime, transferTime;
	public transient List<OperationParameter<?>> operationParameters=new ArrayList<>();
	
	public TaskNode() {}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String id="";
		Task target = taskContainer.getElement();
		if(target!=null)
			id+=target.toString();
		return "TaskNode = ["+id+"]";
	}
	
	public OperationInfo getOperationTimeInfo() throws Exception {
		BigDate st = setupTime==null ? new BigDate(0): setupTime.get();
		BigDate ot = operationTime==null ? new BigDate(0): operationTime.get();
		return new OperationInfo(st, ot);	
		
	}
	
	
	public TaskNode(TaskContainer taskContainer) {
		this();
		setTaskContainer(taskContainer);
	}
	
	public TaskNode(TaskContainer taskContainer, TransferableElementContainer entityContainer) {
		this();
		setTaskContainer(taskContainer);
	}
		
	public TaskNode(TaskContainer targetContainer, ModelParameter<BigDate> operationTime) {
		this(targetContainer);
		setOperationTime(operationTime);
	}
	
	public TaskNode(TaskContainer targetContainer, ModelParameter<BigDate> operationTime, ModelParameter<BigDate> setupTime) {
		this(targetContainer, operationTime);
		setSetupTime(setupTime);
	}
	
	public TaskNode(TaskContainer targetContainer, ModelParameter<BigDate> operationTime, ModelParameter<BigDate> setupTime, 
			ModelParameter<BigDate> transferTime) {
		this(targetContainer, operationTime, setupTime);
		setTransferTime(transferTime);
	}

	public Task getTask() {
		return taskContainer.getElement();
	}
	
	public TaskContainer getTaskContainer() {
		return taskContainer;
	}

	public void setTaskContainer(TaskContainer taskContainer) {
		this.taskContainer = taskContainer;
	}

	public long getProcessSequence() {
		return processSequence;
	}

	public void setProcessSequence(long processSequence) {
		this.processSequence = processSequence;
	}

	public boolean isHasOutput() {
		return hasOutput;
	}

	public void setHasOutput(boolean hasOutput) {
		this.hasOutput = hasOutput;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ModelParameter<BigDate> getOperationTime() {
		return operationTime;
	}

	public ModelParameter<BigDate> getSetupTime() {
		return setupTime;
	}

	public ModelParameter<BigDate> getTransferTime() {
		return transferTime;
	}

	public void setOperationTime(ModelParameter<BigDate> operationTime) {
		this.operationTime = operationTime;
	}

	public void setSetupTime(ModelParameter<BigDate> setupTime) {
		this.setupTime = setupTime;
	}

	public void setTransferTime(ModelParameter<BigDate> transferTime) {
		this.transferTime = transferTime;
	}
}
