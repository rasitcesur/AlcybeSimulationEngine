package alcybe.simulation.types;

import java.util.ArrayList;
import java.util.List;

import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.objects.SerializableObject;

public class TransferInfo extends SerializableObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final List<Process> processContainer=new ArrayList<>();
	public AssemblyContainer[] outputs=null;
	public ConcurrentProcessingStrategy processingStrategy=ConcurrentProcessingStrategy.AllAvailable;
	
	public TransferInfo clone() {
		TransferInfo t=new TransferInfo();
		return t;
	}
	
	public TransferInfo() {}
	
	public TransferInfo(Process container) {
		this();
		addProcessContainer(container);
	}
	
	public TransferInfo(Process... containerList) {
		this();
		for (Process container : containerList)
			addProcessContainer(container);
	}
	
	public TransferInfo(AssemblyContainer[] outputs, Process container) {
		this();
		addProcessContainer(container);
		setOutputs(outputs);
	}
	
	public TransferInfo(AssemblyContainer[] outputs, Process... containerList) {
		this();
		for (Process container : containerList)
			addProcessContainer(container);
		setOutputs(outputs);
	}
	
	public void setOutputs(AssemblyContainer[] outputs) {
		this.outputs = outputs;
	}
	
	public boolean hasOutput() {
		boolean result=outputs==null;
		for (int i = 0; result && i < processContainer.size(); i++) {
			Process p = processContainer.get(i);
			result=p.outputs==null;
		}
		return !result;
	}
	
	public AssemblyContainer[] getOutputs(){
		AssemblyContainer[] result=null;
		int count = outputs==null ? 0 : outputs.length;
		for (int i = 0; i < processContainer.size(); i++) {
			Process p = processContainer.get(i);
			count += p.outputs==null ? 0 : p.outputs.length;
		}
		
		result = new AssemblyContainer[count];
		int cursor=0;
		if(outputs!=null)
			for (AssemblyContainer b : outputs) {
				result[cursor]=b;
				cursor++;
			}
		for (int i = 0; i < processContainer.size(); i++) {
			Process p = processContainer.get(i);
			if(p.outputs!=null)
				for (AssemblyContainer b : p.outputs) {
					result[cursor]=b;
					cursor++;
				}
		}
		return result;
	}

	public List<Process> getProcessContainers() {
		return processContainer;
	}
	
	public Process getProcessContainer() {
		return processContainer.get(0);
	}

	public void addProcessContainer(Process container) {
		this.processContainer.add(container);
	}
	
	public void addProcessContainers(Process[] containerList) {
		for (Process t : containerList)
			addProcessContainer(t);
	}
}
