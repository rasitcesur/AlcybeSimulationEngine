package test;

import java.math.BigDecimal;
import java.util.TreeSet;

import alcybe.simulation.objects.SimulationObject;

public class Way extends SimulationObject{
	
	private static final long serialVersionUID = 1L;
	
	static final BigDecimal mileConstant=new BigDecimal(0.000621371192);
	BigDecimal length=null;
	
	public TreeSet<NodeContainer> nodes = new TreeSet<>();
	private transient NodeContainer[] containers;
	
	public NodeContainer findNodeContainer(Node node) {
		Integer result = findNodeContainerIndex(node);
		if(result==null)
			return null;
		else
			return containers[result];
	}
	
	public Integer findNodeContainerIndex(Node node) {
		if(containers==null)
			containers=nodes.descendingSet().toArray(new NodeContainer[0]);
		Integer result = binarySearch(containers, 0, containers.length - 1, new NodeContainer(node));
		return result;
	}
	
	private static Integer binarySearch(NodeContainer arr[], int first, int last, NodeContainer key){  
		if(arr.length>0) {
			int mid = (first + last)/2;  
			while( first <= last ){  
				if ( arr[mid].compareTo(key) == -1 ){  
					first = mid + 1;     
				}else if ( arr[mid].compareTo(key) == 0 ){  
			        return mid;
			    } else
			    	last = mid - 1;  
				mid = (first + last)/2;  
			}
		}
		return null;
	}  
	
	public void addNode(long x) {
		addNode(new Node(x));		
	}
	
	public void addNode(long x, long y) {
		addNode(new Node(x, y));		
	}
	
	public void addNode(Node node) {
		NodeContainer container=findNodeContainer(node);
		if(container==null) {
			container=new NodeContainer(node);
			nodes.add(container);
			containers=nodes.descendingSet().toArray(new NodeContainer[0]);
		} else
			container.nodes.add(node);	
	}
	
	public Way() { }
	
	public Way(double length) {
		setLength(length);
	}
	
	public Way(long length) {
		setLength(length);
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}
	
	public void setLength(long length) {
		this.length = new BigDecimal(length);
	}
	
	public void setLength(double length) {
		this.length = new BigDecimal(length);
	}
	
	public BigDecimal toKilometer() {
		return length.movePointLeft(3);
	}
	
	public BigDecimal toCentimeter() {
		return length.movePointRight(2);
	}
	
	public BigDecimal toMillimeter() {
		return length.movePointRight(3);
	}
	
	
	public BigDecimal toMicrometer() {
		return length.movePointRight(6);
	}
	
	public BigDecimal toMile() {
		return length.multiply(mileConstant);
	}

	public NodeContainer[] getNodeContainers() {
		return containers;
	}
}
