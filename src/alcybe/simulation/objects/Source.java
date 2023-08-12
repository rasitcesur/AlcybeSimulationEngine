package alcybe.simulation.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import alcybe.data.BigDate;
import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;

public class Source extends Task {
	
	private class Bom{
		
		public TransferableElement[] elements;
		@SuppressWarnings("unused")
		public long id; 
		
		public Bom(long id, TransferableElement[] elements) {
			this.id=id;
			this.elements=elements;
		}
		
		public Bom(long id, TransferableElement element) {
			this(id, new TransferableElement[] {element});
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<Bom> bomContainer=new ArrayList<>(100000);
	public long arrivalCount=0, arrivalTreshold=1;
	public BigDate timeBetweenArrivals;
	private long bomId=0;
	private long assemblySequence=0;
	
	public Source(){}
	
	public Source(BigDate timeBetweenArrivals){
		setTimeBetweenArrivals(timeBetweenArrivals);
	}
	
	/**public ProcessResult process(TaskNode operableElementContainer, 
			SimulationObject entity, Date simulationTime) throws Exception {
		// TODO Auto-generated method stub

		Object eventResults[]=SimulationGlobals.evalScript("var timeBetweenArrivals="+timeBetweenArrivals+";", 
				new String[] {"timeBetweenArrivals"}, this, entity);
		
		Calendar c = Calendar.getInstance();
		c.setTime(simulationTime);
		c.add(TimeUnit.valueOf(timeBetweenArrivalsUnit).getUnitValue(),
				(int)eventResults[0]);
		
		return new ProcessResult((int)eventResults[0], c.getTime());
	}*/

	public void addElement(TransferableElement element) {
		bomContainer.add(new Bom(bomId,element));
		bomId++;
	}
	
	public void addElements(TransferableElement[] elements) {
		bomContainer.add(new Bom(bomId, elements));
		bomId++;
	}
	
	public Long getArrivalCount() {
		return arrivalCount;
	}

	public void setElementCount(int elementCount) {
		this.arrivalCount = elementCount;
	}

	public Long getArrivalTreshold() {
		return arrivalTreshold;
	}

	public void setArrivalTreshold(int arrivalTreshold) {
		this.arrivalTreshold = arrivalTreshold;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private TransferableElement[] bufferArray=null;
	public TransferableElement[] getElementBuffer() {
		if(bufferArray==null) {
			int count=0;
			for (Bom o : bomContainer) 
				count+=o.elements.length;
			bufferArray = new TransferableElement[count];
			count=0;
			
			HashMap<TransferableElement, Boolean> elementIndex = new HashMap<>();
			for (Bom o : bomContainer) 
				for (int i = 0; i < o.elements.length; i++) {
					bufferArray[count]=o.elements[i];
					for (TransferInfo t : bufferArray[count].targetList) {
						for (alcybe.simulation.types.Process p : t.processContainer) {
							if(p.inputs!=null) {
								assemblySequence++;
								for (AssemblyContainer tc : p.inputs) {
									tc.getEntity().assemblySequence=assemblySequence;
									elementIndex.put(tc.getEntity(),true);
								}
							}
						}
					}
					count++;
				}
			HashMap<Long, List<TransferableElement>> assemblyIndex = 
					new HashMap<>();
			for (Entry<TransferableElement,Boolean> e : elementIndex.entrySet()) {
				TransferableElement t = e.getKey();
				if(t.assemblySequence!=0) {
					List<TransferableElement> teList=assemblyIndex.get(t.assemblySequence);
					if(teList==null) {
						teList=new ArrayList<>();
						assemblyIndex.put(t.assemblySequence, teList);
					}
					teList.add(t);
				}
			}
			
			count=0;
			Set<Entry<Long,List<TransferableElement>>> set = assemblyIndex.entrySet();
			for (Entry<Long,List<TransferableElement>> e : set) {
				count++;
				for (TransferableElement te : e.getValue()) {
					te.assemblySequence=count;
					te.assemblySetCount=set.size();
				}
			}
			
		}
		return bufferArray;
	}

	public BigDate getTimeBetweenArrivals() {
		return timeBetweenArrivals;
	}

	public void setTimeBetweenArrivals(BigDate timeBetweenArrivals) {
		this.timeBetweenArrivals = timeBetweenArrivals;
	}
}
