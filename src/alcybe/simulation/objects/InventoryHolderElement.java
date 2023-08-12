package alcybe.simulation.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import alcybe.simulation.data.containers.TransferableElementContainer;

public abstract class InventoryHolderElement extends SimulationObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int size=0;
	//public final List<TransferableElement> elementBuffer = new LinkedList<TransferableElement>();
	public HashMap<Object, List<TransferableElementContainer>> elementBuffer = new HashMap<>();
	public double bufferCapacity=1;
	
	public boolean isFull(){
		return bufferCapacity==size; //==
	}
	
	public double getRemainingBufferCapacity(){
		double totalBuffer=0;
		for(Entry<Object, List<TransferableElementContainer>> es:elementBuffer.entrySet())
			for (TransferableElementContainer e : es.getValue())
				totalBuffer+=e.getElement().amount;
		return bufferCapacity - totalBuffer;
	}
	
	public void removeElement(TransferableElementContainer container) throws Exception{
		TransferableElement element = container.getElement();
		List<TransferableElementContainer> elements=elementBuffer.get(element.getUniqueIdentity());
		if(elements==null)
			throw new Exception("Element can not be found in the buffer.");
		else{
			int s=elements.size();
			elements.remove(container);
			int s1=elements.size();
			size-=s-s1;
			if(s1==0)
				elementBuffer.remove(element.getUniqueIdentity());
			
			
		}
	}
	
	public void removeElementIfExists(TransferableElementContainer container) throws Exception{
		TransferableElement element = container.getElement();
		List<TransferableElementContainer> elements=elementBuffer.get(element.getUniqueIdentity());
		if(elements!=null){
			int s=elements.size();
			elements.remove(container);
			int s1=elements.size();
			size-=s-s1;
			if(s1==0)
				elementBuffer.remove(element.getUniqueIdentity());
			
			
		}
	}
	
	public void removeElementAt(int index) throws Exception{
		if(index>=size)
			throw new Exception("Given index is bigger than element count.");
		int cnt=0;
		TransferableElementContainer container=null;
		for(Entry<Object, List<TransferableElementContainer>> es:elementBuffer.entrySet()) {
			for (TransferableElementContainer e : es.getValue()) {
				cnt++;
				if(cnt==index) {
					container=e;
					break;
				}
					
			}
			if(cnt==index) break;
		}
		removeElement(container);
	}
	
	public void addElement(TransferableElementContainer container) throws Exception{
		TransferableElement element = container.getElement();
		if(getRemainingBufferCapacity()<element.amount)
			throw new Exception("Buffer overflow is occured.");
		List<TransferableElementContainer> elements=this.elementBuffer.get(element.getUniqueIdentity());
		if(elements==null){
			elements=new LinkedList<>();
			this.elementBuffer.put(element.getUniqueIdentity(), elements);
		}
		elements.add(container);
		size++;
	}
	
	public void removeElementWithoutIndex(TransferableElement element) throws Exception{
		elementBuffer.remove(element);
	}
}
