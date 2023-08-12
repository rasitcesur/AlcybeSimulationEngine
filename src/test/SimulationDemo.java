package test;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.AssemblyContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.events.ArrivalEvent;
import alcybe.simulation.events.DiscreteEvent;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.FixedTimeParameter;
import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.Source;
import alcybe.simulation.objects.Workstation;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;

public class SimulationDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("rawtypes")
		Engine<?> simEngine=new Engine();
		Workstation m1=new Workstation(),m2=new Workstation(),m3=new Workstation(),
				m4=new Workstation();
		m1.setIdentity("M1");
		m2.setIdentity("M2");
		m3.setIdentity("M3");
		m4.setIdentity("M4");
		m3.bufferCapacity=1;
		//List<SimulationEvent> events=new ArrayList<>();

		TaskContainer t1=new TaskContainer(m1), t2=new TaskContainer(m2),t3=new TaskContainer(m3),
				t4=new TaskContainer(m4);
		
		Entity p1=new Entity(),p2=new Entity(),p3=new Entity(),p4=new Entity();
		
		//BomElement[] inputs=new BomElement[] {new BomElement(p1,1), new BomElement(p2,1)}; 
		AssemblyContainer[] outputs= {new AssemblyContainer(p3,1,1),new AssemblyContainer(p4,1,1)};
		AssemblyContainer[] inputs=new AssemblyContainer[] {new AssemblyContainer(p3,1), new AssemblyContainer(p4,1), new AssemblyContainer(p2,1)}; 
		
		p1.setIdentity("P1");
		p2.setIdentity("P2");
		p3.setIdentity("P3");
		p4.setIdentity("P4");
		
		
		/**p1.addTarget(new TransferInfo(outputs, new Process(new TaskNode(m1,"40")))); //output, new TaskNode(m3,"30", inputs)
		//p1.addTarget(new TransferInfo(new Bom(inputs, new TaskNode(m3,"30"))));
		p2.addTarget(new TransferInfo(new Process(new TaskNode(m2,"25"))));
		p2.addTarget(new TransferInfo(new Process(inputs, new TaskNode(m3,"30"))));
		p3.addTarget(new TransferInfo(new Process(inputs, new TaskNode(m3,"30"))));
		//p4.addTarget(new TransferInfo(new Bom(new TaskNode(m2,"15"))));
		//p4.addTarget(new TransferInfo(new Bom(inputs, new TaskNode(m3,"30"))));
		p4.addTarget(new TransferInfo(new Process(new TaskNode(m2,"15")), new Process(inputs, new TaskNode(m3,"30"))));
		*/
		
		p1.addTarget(new TransferInfo(new Process(new TaskNode(t1,new FixedTimeParameter(40, TimeUnit.second)), new TaskNode(t3,new FixedTimeParameter(30, TimeUnit.second)))));//, new Process(new TaskNode(m3,"30"))));
		//p1.addTarget(new TransferInfo(new Process(new TaskNode(t3,new FixedTimeParameter(30, TimeUnit.second)))));
		p2.addTarget(new TransferInfo(new Process(new TaskNode(t2,new FixedTimeParameter(25, TimeUnit.second)), new TaskNode(t3,new FixedTimeParameter(20, TimeUnit.second)))));// , new Process(new TaskNode(m3,"20"))));
		p2.addTarget(new TransferInfo(new Process(new TaskNode(t1,new FixedTimeParameter(20, TimeUnit.second)))));
		
		
		Source src=new Source();
		src.setIdentity("S1");
		src.addElement(p1);
		src.addElement(p2);
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		//Date beginDate=getDate();
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, 
				new BigDate(0))}, new BigDate(3600000));//getDate(beginDate,1));
		simEngine.initDefaultBlocks();
		simEngine.showTrace=true;
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
	}
}
