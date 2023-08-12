package alcybe.simulation.objects;

import java.util.ArrayList;
import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.events.OperationEvent;
import alcybe.simulation.events.OperationFinalizationEvent;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.OperationStatistics;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;

public class TransferableElement extends SimulationObject {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	public List<TransferInfo> targetList=new ArrayList<>(); //TODO transient
	public List<OperationStatistics> operationInfo=new ArrayList<>(); //TODO transient
	public double amount=1d; //TODO transient
	public int cursor=-1; //TODO transient
	public OperationFinalizationEvent operationFinalizationEvent = null; //TODO transient
	public int concurrentEventCount; //TODO transient
	public List<OperationEvent> concurrentOperationEvents=new ArrayList<>(); 
	public List<OperationFinalizationEvent> concurrentOperationFinalizationEvents=new ArrayList<>();
	public long arrivalSequence=0, uniqueSequence=0, assemblySequence=0, assemblySetCount=0;
	public double velocity;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "TransferableElement="+super.toString();
	}
	public String getUniqueIdentity() {
		// TODO Auto-generated method stub
		return super.toString()+"."+arrivalSequence;
	}
	
	public TaskNode currentNode = null;
	
	public TaskNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(TaskNode currentNode) {
		this.currentNode = currentNode;
	}
	
	public BigDate getRemainingTime() {
		BigDate remaining=new BigDate(0);
		for(int i=cursor;i<targetList.size();i++){
			try {
				OperationInfo info = 
						targetList.get(i).processContainer.get(0).workcenters.get(0).getOperationTimeInfo();
				remaining.add(info.operationTime);
			} catch (Exception e) {	}
		}
		return remaining;
	}
	
	public Task getBottleneck() {
		Task bottleneck=null;
		BigDate workload=new BigDate(0);
		for(TransferInfo tf : targetList) {
			for(TaskNode tn:tf.getProcessContainer().workcenters) {
				try {
					if(workload.compareTo(tn.getTask().totalWorkload)<0) {
						workload = tn.getTask().totalWorkload;
						bottleneck = tn.getTask();
					}
				} catch (Exception e) {}
			}
		}
		return bottleneck;
	}
	
	public BigDate getTimeToBottleneck() {
		BigDate opTime, time, bottleneck=new BigDate(0), result=new BigDate(0), cumulative=new BigDate(0);
		for(TransferInfo tf : targetList) {
			opTime = new BigDate(0);
			for(TaskNode tn:tf.getProcessContainer().workcenters) {
				try {
					OperationInfo opInfo=tn.getOperationTimeInfo();

					//TODO consider setup time
					time = opInfo.operationTime;
					if(opTime.compareTo(time)<0)
						opTime=time;
				} catch (Exception e) {}
			}
			cumulative.add(opTime);
			if(bottleneck.compareTo(opTime)<0) {
				bottleneck = opTime;
				result=cumulative;
			}
		}
		return result;
	}
	
	public BigDate getRemainingTimeToBottleneck() {
		BigDate opTime, time, bottleneck=new BigDate(0), result=new BigDate(0), cumulative=new BigDate(0);
		int size=targetList.size();
		for(int i=cursor; i<size; i++){
			TransferInfo tf = targetList.get(i);
			opTime = new BigDate(0); 
			for(TaskNode tn:tf.getProcessContainer().workcenters) {
				try {
					OperationInfo opInfo=tn.getOperationTimeInfo();
					//TODO consider setup time
					time = opInfo.operationTime;
					if(opTime.compareTo(time)<0)
						opTime=time;
				} catch (Exception e) {}
			}
			cumulative.add(opTime);
			if(bottleneck.compareTo(opTime)<0) {
				bottleneck = opTime;
				result=cumulative;
			}
		}
		return result;
	}
	
	@Override
	public boolean equals(Object element) {
		/*if(transactionSequence>-1) {
			TransferableElement e=(TransferableElement) element;
			return transactionSequence==e.transactionSequence
					&& identifier.equals(e.identifier);
		}
		return super.equals(element);*/
		TransferableElement e=(TransferableElement) element;
		return uniqueSequence==e.uniqueSequence;
		//return e.arrivalSequence==arrivalSequence && (identifier == null || identifier.equals(e.identifier));
	}
	
	public long getArrivalSequence() {
		return arrivalSequence;
	}
	
	public void setSequence(long arrivalSequence) {
		this.arrivalSequence = arrivalSequence;
	}

	public long getUniqueSequence() {
		return uniqueSequence;
	}

	public void setUniqueSequence(Long uniqueSequence) {
		this.uniqueSequence = uniqueSequence;
	}
	
	public void addConcurrentOperationEvent(OperationEvent event) {
		concurrentOperationEvents.add(event);
	}
	
	public void addConcurrentOperationEvents(List<OperationEvent> events) {
		concurrentOperationEvents.addAll(events);
	}
	
	public void removeConcurrentOperationEvent(OperationEvent event) {
		concurrentOperationEvents.remove(event);
	}
	
	public void addConcurrentOperationFinalizationEvent(OperationFinalizationEvent event) {
		concurrentOperationFinalizationEvents.add(event);
	}
	
	public void addConcurrentOperationFinalizationEvents(List<OperationFinalizationEvent> events) {
		concurrentOperationFinalizationEvents.addAll(events);
	}
	
	public void removeConcurrentOperationFinalizationEvent(OperationFinalizationEvent event) {
		concurrentOperationFinalizationEvents.remove(event);
	}
	
	public int getConcurrentOperationCount() {
		return concurrentOperationEvents.size();
	}
	
	public int getConcurrentOperationFinalizationCount() {
		return concurrentOperationFinalizationEvents.size();
	}
	
	public List<OperationEvent> getConcurrentOperationEvents() {
		return concurrentOperationEvents;
	}
	
	public List<OperationFinalizationEvent> getConcurrentOperationFinalizationEvents() {
		return concurrentOperationFinalizationEvents;
	}
	
	public void rollbackTarget() {
		if(cursor>-1)
			cursor--;
	}
	
	public TransferInfo getNextTarget() {
		if(cursor<targetList.size())
			cursor++;
		if(cursor<targetList.size()){
			TransferInfo info = targetList.get(cursor);
			return info;
		}
		return null;
	}
	
	public void addTarget(TransferInfo transferInfo) {
		targetList.add(transferInfo);
	}
	
	public TransferInfo getCurrentTarget() {
		if(cursor<targetList.size())
			return targetList.get(cursor);
		return targetList.get(targetList.size()-1);
	}
	
	public TransferInfo getPreviousTarget() {
		if(cursor>0)
			return targetList.get(cursor - 1);
		return null;
	}

	public TransferInfo getTargetAt(int index) {
		return targetList.get(index);
	}
	
	public TransferInfo getFirstTarget() {
		return targetList.get(0);
	}
	
	public TransferInfo getLastTarget() {
		return targetList.get(targetList.size()-1);
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public List<TransferInfo> getTargetList() {
		return targetList;
	}

	public List<OperationStatistics> getOperationInfo() {
		return operationInfo;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getConcurrentEventCount() {
		return concurrentEventCount;
	}

	public void setConcurrentEventCount(int concurrentEventCount) {
		this.concurrentEventCount = concurrentEventCount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setUniqueSequence(long uniqueSequence) {
		this.uniqueSequence = uniqueSequence;
	}	
}
