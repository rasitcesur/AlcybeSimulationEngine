package alcybe.simulation.objects;

import java.util.HashMap;
import java.util.LinkedList;

import alcybe.data.BigDate;
import alcybe.simulation.model.ProcessResult;
import alcybe.simulation.model.TaskNode;

public class SimulationObject extends SerializableObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	protected static HashMap<Object, Integer> stateIndex=new HashMap<>();
	protected static LinkedList<Object> stateData=new LinkedList<>();
	protected static HashMap<String, Integer> attributeIndex=new HashMap<>();
	protected HashMap<Integer, Object> attributes=new HashMap<>();
	public HashMap<Integer, LinkedList<Integer>> atTheBeginning=new HashMap<>(),
			atTheEnd=new HashMap<>();
	public Object identity;
	public String definition;
	public int state;
	
	public Object getAttribute(String name) {
		return attributes.get(attributeIndex.get(name));
	}
	
	public void setAttribute(String name, Object value){
		Integer attIndex=attributeIndex.get(name);
		if(attIndex==null) {
			attIndex=attributeIndex.keySet().size();
			attributeIndex.put(name, attIndex);
		}
		attributes.put(attIndex, value);
	}
	
	public Object getState() {
		return stateData.get(state);
	}

	public void setState(Object state) {
		Integer sIndex=stateIndex.get(state);
		if(sIndex==null) {
			sIndex=stateData.size();
			stateIndex.put(state, sIndex);
			stateData.add(state);
		}
		this.state=sIndex;
	}
	
	public SimulationObject(){}
	
	private static void insertUDEvent(HashMap<Integer, LinkedList<Integer>> list, int blockIndex, int eventIndex) {
		LinkedList<Integer> eventList=list.get(blockIndex);
		if(eventList==null) {
			eventList=new LinkedList<Integer>();
			list.put(blockIndex, eventList);
		}
		eventList.add(eventIndex);
	}
	
	public void insertUDEventAtTheBeginning(int blockIndex, int eventIndex) {
		insertUDEvent(atTheBeginning, blockIndex, eventIndex);
	}
	
	public void insertUDEventAtTheEnd(int blockIndex, int eventIndex) {
		insertUDEvent(atTheEnd, blockIndex, eventIndex);
	}
	
	public ProcessResult process(TaskNode operableElementContainer, 
			SimulationObject entity, BigDate simulationTime) throws Exception{ 
		return new ProcessResult(new BigDate(0), simulationTime);
	}

	public Object getIdentity() {
		return identity;
	}

	public void setIdentity(Object identity) {
		this.identity = identity;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(identity!=null)
			return identity.toString();
		return "null";
	}

}
