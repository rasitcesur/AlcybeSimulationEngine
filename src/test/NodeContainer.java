package test;

import java.util.ArrayList;
import java.util.List;

public class NodeContainer implements Comparable<NodeContainer>{
	
	public final List<Node> nodes=new ArrayList<>();
	
	public NodeContainer() { }
	
	public NodeContainer(Node node) {
		nodes.add(node);
	}
	
	public NodeContainer(Node... nodes) {
		for(Node n:nodes)
			this.nodes.add(n);
	}

	@Override
	public int compareTo(NodeContainer o) {
		return nodes.get(0).compareTo(o.nodes.get(0)) ;
	}
	
	
}
