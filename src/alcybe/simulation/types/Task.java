package alcybe.simulation.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.TaskBufferContainer;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.OperationInfo;
import alcybe.simulation.model.OperationStatistics;
import alcybe.simulation.model.Shift;
import alcybe.simulation.objects.InventoryHolderElement;

public abstract class Task extends InventoryHolderElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public static enum ElementSelectionRule{
		FirstInFirstOut,
		HighestPriority,
		LastInFirstOut,
		LowestPriority,
		RealTime,
		RealTimeBottleneck,
		SlackTime
	};
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Task = "+super.toString();
	}
	
	public final List<TaskBufferContainer> elementQueue = new LinkedList<>(), processBuffer=new LinkedList<>(),
			resourceBuffer=new LinkedList<>();
	//public final HashMap<Object,SimulationObject> previousElementIndex= new HashMap<>();
	public boolean waitingConcurrentOperation = false;
	public long processSequence=0;
	public double priority=1;
	public BigDate processBeginTime=new BigDate(0);
	public OperationStatistics taskStatistics=new OperationStatistics();
	public long activeEventCount;
	public BigDate virtualQueueTime= new BigDate(0);
	public ElementSelectionRule elementSelectionRule = ElementSelectionRule.RealTime;
	public final HashMap<Long,List<TaskBufferContainer>> assemblyBuffer = new HashMap<>();
	public BigDate taskDuration=new BigDate(0);
	public BigDate totalWorkload=new BigDate(0);
	public BigDate taskCompletionDate=new BigDate(0);
	public final List<Shift> shifts=new ArrayList<>();
	
	public Task(){
		for(int i=0;i<1000;i++) {
			if(i%7!=6) {
			BigDate sBegin1=new BigDate(2023,8,7), sBegin2=new BigDate(2023,8,7),
					sEnd1=new BigDate(2023,8,7), sEnd2=new BigDate(2023,8,7);
			sBegin1.addDay(i);
			sBegin1.addHour(7);
			sEnd1.addDay(i);
			sEnd1.addHour(12);
			sBegin2.addDay(i);
			sBegin2.addHour(12);
			sBegin2.addMinute(30);
			sEnd2.addDay(i);
			sEnd2.addHour(17);
			shifts.add(new Shift(sBegin1, sEnd1));
			shifts.add(new Shift(sBegin2, sEnd2));
			}
		}
	}
	
	public Shift getAvailableShift(BigDate date) {
		Shift result=null;
		for(Shift s:this.shifts) {
			if(s.begin.compareTo(date) < 1 && s.end.compareTo(date) > -1 ) {
				result=s;
				break;
			}
		}
		return result;
	}
	
	public EventType getEventType() {
		return null;
	}
	
	public BigDate getAssignedJobDuration() {
		BigDate res=new BigDate(0);
		for(TaskBufferContainer tc:elementQueue) {
			try {
				OperationInfo opInfo = tc.getTaskNode().getOperationTimeInfo();
				res.add(opInfo.operationTime);
			} catch (Exception e) {}
		}
		return res;
	}
	
	public void increseEventCount() {
		activeEventCount++;
	}
	
	public void decreaseEventCount() {
		activeEventCount--;
	}
	
	public long getActiveEventCount() {
		return activeEventCount;
	}
	
	public boolean isEventBufferFull() {
		return bufferCapacity<=activeEventCount;
	}
	
	public void clearBuffers(TaskBufferContainer taskContainer) {
		assemblyBuffer.remove(taskContainer.getTransferableElementContainer().getElement().assemblySequence);
		processBuffer.remove(taskContainer);
		try {
			removeElement(taskContainer.getTransferableElementContainer());
		} catch (Exception e) { }
	}
	
	public void addElementToBuffers(TaskBufferContainer taskContainer) {
		processBuffer.add(taskContainer);
		addToAssemblyBuffer(taskContainer);
	}
	
	public void addToAssemblyBuffer(TaskBufferContainer taskContainer) {
		List<TaskBufferContainer> list=assemblyBuffer.get(taskContainer.getTransferableElementContainer().getElement().assemblySequence);
		if(list==null) {
			list = new ArrayList<TaskBufferContainer>();
			assemblyBuffer.put(taskContainer.getTransferableElementContainer().getElement().assemblySequence, list);
		}
		list.add(taskContainer);
	}
	
	public boolean readyForAssemble(TaskBufferContainer taskContainer) {
		
		List<TaskBufferContainer> list=assemblyBuffer.get(taskContainer.getTransferableElementContainer().getElement().assemblySequence);
		if(list==null)
			return false;
		else if(taskContainer.getProcess().inputs==null)
			return false;
		else
			return list.size()==taskContainer.getProcess().inputs.length;
	}
	
	/**
	 * TODO: Setup Strategy
	 * */
	public SetupStrategy setupStrategy=SetupStrategy.SetupAfterCycleEnds;
	
	protected final HashMap<Object, OperationStatistics> totalOperatedAmount
			= new HashMap<>();
	
	
	public TaskBufferContainer iterateElement(){
		switch(elementSelectionRule) {
		case FirstInFirstOut:
			if(elementQueue.size()>0){
				TaskBufferContainer element=elementQueue.get(0);
				elementQueue.remove(0);
				return element;
			}
			break;
		case HighestPriority:{
			if(elementQueue.size()>0){
				double maxPriority=Double.MIN_VALUE;
				int index=0, pIndex=0;
				for (TaskBufferContainer tc:elementQueue) {
					priority=tc.getTaskNode().priority;
					if(maxPriority<priority) {
						index=pIndex;
						maxPriority=priority;
					}
					pIndex++;
				}
				TaskBufferContainer taskContainer=elementQueue.get(index);
				elementQueue.remove(index);
				return taskContainer;
			}
			break;
		}
		case LastInFirstOut:
			if(elementQueue.size()>0){
				int index=elementQueue.size()-1;
				TaskBufferContainer taskContainer=elementQueue.get(index);
				elementQueue.remove(index);
				return taskContainer;
			}
			break;
		case LowestPriority:
			if(elementQueue.size()>0){
				double minPriority=Double.MAX_VALUE;
				int index=0, pIndex=0;
				for (TaskBufferContainer tc:elementQueue) {
					priority=tc.getTaskNode().priority;
					if(minPriority>priority) {
						index=pIndex;
						minPriority=priority;
					}
					pIndex++;
				}
				TaskBufferContainer taskContainer=elementQueue.get(index);
				elementQueue.remove(index);
				return taskContainer;
			}
			break;
		case RealTime:
		case RealTimeBottleneck:{
			if(elementQueue.size()>0){
				BigDate maxOpTime=new BigDate(Double.MIN_VALUE);
				int index=0, pIndex=0;
				BigDate bottleneckTime=new BigDate(0), bt=new BigDate(0);
				for (TaskBufferContainer tc:elementQueue) {
					try {
						bt = tc.getTransferableElementContainer().getElement().getBottleneck().totalWorkload;
						if(bottleneckTime.compareTo(bt) <= 0) {
							bottleneckTime = bt;
							//OperationInfo opInfo=tn.getOperationTimeInfo(this);
							priority= elementSelectionRule.equals(ElementSelectionRule.RealTime) ? 
									tc.getTransferableElementContainer().getElement().getRemainingTime().getTime().doubleValue() : 
										tc.getTransferableElementContainer().getElement().getRemainingTimeToBottleneck().getTime().doubleValue();
									//SimulationGlobals.addTime(tn.entity.getRemainingTime(), (int)opInfo.operationTime,
									//TimeUnit.valueOf(opInfo.operationTimeUnit)); 
							// - tn.entity.getRemainingTimeToBottleneck();
							if(maxOpTime.getTime().doubleValue()<priority) {
								index=pIndex;
								maxOpTime=new BigDate(priority);
							}
						}
					} catch (Exception e) {}
					pIndex++;
				}
				TaskBufferContainer taskContainer=elementQueue.get(index);
				elementQueue.remove(index);
				return taskContainer;
			}
			break;
		}
		case SlackTime:{
			if(elementQueue.size()>0){
				double maxOpTime=Double.MIN_VALUE;
				int index=0, pIndex=0;
				for (TaskBufferContainer tc:elementQueue) {
					try {
						OperationInfo opInfo=tc.getTaskNode().getOperationTimeInfo();
						priority=tc.getTransferableElementContainer().getElement().getRemainingTime().getTime().doubleValue()+
								opInfo.operationTime.getTime().doubleValue();
						if(maxOpTime<priority) {
							index=pIndex;
							maxOpTime=priority;
						}
					} catch (Exception e) {}
					pIndex++;
				}
				TaskBufferContainer taskContainer=elementQueue.get(index);
				elementQueue.remove(index);
				return taskContainer;
			}
			break;
		}
		}
		return null;
	}
	
	public boolean hasWaitingEntity(){
		return elementQueue.size()>0;
	}
	
	public double getAssignedCapacity() {
		double totalBuffer=0;
		for (TaskBufferContainer e : processBuffer)
			totalBuffer+=e.getTransferableElementContainer().getElement().amount;
		return totalBuffer;
	}
	
	public double getRemainingCapacity() {
		return bufferCapacity - getAssignedCapacity();
	}
	
	public boolean isFullCapacity(){
		return bufferCapacity==getAssignedCapacity();
	}
	
	public boolean isAvailableForProcessing() {
		return hasWaitingEntity() && !isFullCapacity();
	}
	
	public TaskBufferContainer getCurrentElement(){
		if(elementQueue.size()>0){
			TaskBufferContainer element=elementQueue.get(0);
			return element;
		}
		return null;
	}
	
	
	protected void addOperatedAmount(Object id, double amount){
		OperationStatistics info = this.totalOperatedAmount.get(id);
		if(info==null){
			info=new OperationStatistics();
			this.totalOperatedAmount.put(id,info);
		}
		info.updateTotalAmount(amount);
	}
	
	protected double getOperatedAmount(Object id){
		OperationStatistics info = this.totalOperatedAmount.get(id);
		if(info==null)
			return 0;
		else
			return info.totalAmount;
	}
	
	/**protected void addPreviousElement(SimulationObject element){
		if(!previousElementIndex.containsKey(element.identifier))
			previousElementIndex.put(element.identifier, element);
	}
	
	protected void removePreviousElement(SimulationObject element){
		previousElementIndex.remove(element.identifier);
	}
	
	protected void clearPreviousElements(){
		previousElementIndex.clear();
	}
	
	public boolean hasSameElementProcessed(){
		boolean result=false;
		for(TaskNode o:processBuffer){
			if(o.getEntity()!=null)
				result|=this.previousElementIndex.containsKey(o.getEntity().identifier);
			if(result) break;
		}
		return result;
	}*/
	
	public boolean isWaitingConcurrentOperation() {
		return waitingConcurrentOperation;
	}

	public void setWaitingConcurrentOperation(boolean waitingConcurrentOperation) {
		this.waitingConcurrentOperation = waitingConcurrentOperation;
	}

	public long getProcessSequence() {
		return processSequence;
	}
	
	public long increaseProcessSequence() {
		processSequence++;
		return processSequence;
	}

	public void setProcessSequence(long processSequence) {
		this.processSequence = processSequence;
	}

	public BigDate getTaskCompletionDate() {
		return new BigDate(taskCompletionDate.getTime());
	}

	public void setTaskCompletionDate(BigDate taskCompletionDate) {
		this.taskCompletionDate = taskCompletionDate;
	}

	public OperationStatistics getTaskStatistics() {
		return taskStatistics;
	}

	public BigDate getVirtualQueueTime() {
		return virtualQueueTime;
	}

	public void setVirtualQueueTime(BigDate virtualQueueTime) {
		this.virtualQueueTime = virtualQueueTime;
	}
}
