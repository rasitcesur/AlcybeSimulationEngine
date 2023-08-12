package alcybe.simulation.types;

import java.util.LinkedList;
import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.objects.SerializableObject;

public class Process extends SerializableObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public DispatchingStrategy dispatchingStrategy=DispatchingStrategy.FirstAvailable;
	public List<TaskNode> workcenters = new LinkedList<>();
	public transient AssemblyContainer[] inputs=null, outputs=null;
	public long id=0;
	
	public Process() {}
	
	private void addTaskNode(TaskNode t) {
		this.workcenters.add(t);
	}
	
	public Process(TaskNode... workcenters) {
		for (TaskNode t : workcenters) 
			addTaskNode(t);
	}
	
	public Process(DispatchingStrategy dispatchingStrategy, TaskNode... workcenters) {
		this(workcenters);
		setDispatchingStrategy(dispatchingStrategy);
	}
	
	public Process(List<TaskNode> workcenters) {
		for (TaskNode t : workcenters)
			addTaskNode(t);
	}
	
	public Process(AssemblyContainer[] inputs, TaskNode... workcenters) {
		this(workcenters);
		setInputs(inputs);
	}
	
	public Process(AssemblyContainer[] inputs, List<TaskNode> workcenters) {
		this(workcenters);
		setInputs(inputs);
	}
	
	public Process(long id, AssemblyContainer[] inputs, TaskNode... workcenters) {
		this(inputs, workcenters);
		setId(id);
	}
	
	public Process(long id, AssemblyContainer[] inputs, List<TaskNode> workcenters) {
		this(inputs, workcenters);
		setId(id);
	}
	
	public Process(AssemblyContainer[] inputs, AssemblyContainer[] outputs, TaskNode... workcenters) {
		this(workcenters);
		setInputs(inputs);
		setOutputs(outputs);
	}
	
	public Process(AssemblyContainer[] inputs, AssemblyContainer[] outputs, List<TaskNode> workcenters) {
		this(workcenters);
		setInputs(inputs);
		setOutputs(outputs);
	}
	
	public Process(long id, AssemblyContainer[] inputs, AssemblyContainer[] outputs, TaskNode... workcenters) {
		this(inputs, outputs, workcenters);
		setId(id);
	}
	
	public Process(long id, AssemblyContainer[] inputs, AssemblyContainer[] outputs, List<TaskNode> workcenters) {
		this(inputs, outputs, workcenters);
		setId(id);
	}
	
	
	
	private void setInputs(AssemblyContainer[] inputs) {
		this.inputs=inputs;
	}
	
	private void setOutputs(AssemblyContainer[] outputs) {
		this.outputs=outputs;
	}
	
	public TaskNode getWorkcenter() {
		TaskNode result=null;
		switch (dispatchingStrategy) {
		case CapacityUsage:
			break;
		case FirstAvailable:{
			BigDate temp=new BigDate(Long.MAX_VALUE);
			result=workcenters.get(0);
			for (TaskNode t : workcenters) {

				Task target=t.getTaskContainer().getElement();
				BigDate taskCompletionDate=target.getTaskCompletionDate();
				taskCompletionDate.add(target.totalWorkload);
				
				if(taskCompletionDate == null || 
						temp.compareTo(taskCompletionDate)>0) {
					result=t;
					temp=taskCompletionDate;
				}
			}
			break;
		} case HighestPriority:{
			double temp=Double.MIN_VALUE;
			for (TaskNode t : workcenters) {
				Task target=t.getTaskContainer().getElement();
				if(target.priority>temp) {
					result=t;
					temp=target.priority;
				}
			}
			break;
		} case LowestPriority:{
			double temp=Double.MAX_VALUE;
			for (TaskNode t : workcenters) {
				Task target=t.getTaskContainer().getElement();
				if(target.priority<temp) {
					result=t;
					temp=target.priority;
				}
			}
			break;
		}
		case PerformedTaskDuration:{
			BigDate temp=new BigDate(Double.MAX_VALUE);
			for (TaskNode t : workcenters) {
				Task target=t.getTaskContainer().getElement();
				if(target.taskStatistics.totalDuration.compareTo(temp)<0) {
					result=t;
					temp=target.taskStatistics.totalDuration;
				}
			}
			break;
		}
		case RealTime:{
			result=workcenters.get(0);
			BigDate //tempDate = result.target.getTaskCompletionDate().getTime()+result.target.virtualQueueTime,
					tempDate = new BigDate(Double.MAX_VALUE), taskDate;
			//Calendar c=Calendar.getInstance();
			for (TaskNode t : workcenters) {

				Task target=t.getTaskContainer().getElement();
				taskDate = target.getAssignedJobDuration();
						//+ simulationTime - t.target.processBeginTime; // + t.target.virtualQueueTime;
				if(tempDate.compareTo(taskDate)>0) { //tempDate>taskDate
					result=t;
					tempDate=taskDate;
				}
			}
			break;
		}
		case TotalTaskDuration:{
			BigDate temp=new BigDate(Double.MAX_VALUE);
			for (TaskNode t : workcenters) {
				Task target=t.getTaskContainer().getElement();
				if(target.taskStatistics.totalDuration.compareTo(temp)<0) {
					result=t;
					temp=target.taskStatistics.totalDuration;
				}
			}
			break;
		}
		}
		for (TaskNode t : workcenters) {
			Task target=t.getTaskContainer().getElement();
			try {
				if(!t.equals(result)) {
					OperationInfo info = t.getOperationTimeInfo();
					//TODO consider setup time
					target.virtualQueueTime.add(info.operationTime);
				}
			} catch (Exception e) {}
		}
		return result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DispatchingStrategy getDispatchingStrategy() {
		return dispatchingStrategy;
	}

	public void setDispatchingStrategy(DispatchingStrategy dispatchingStrategy) {
		this.dispatchingStrategy = dispatchingStrategy;
	}
}
