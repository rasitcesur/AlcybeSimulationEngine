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
import alcybe.simulation.types.DispatchingStrategy;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;


public class SimulationTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Source s =new Source();
		s.run();
		Entity e = new Entity();
		e.run();*/
		/*SortedSet<Integer> index=new TreeSet<>();
		index.add(3);
		index.add(4);
		index.add(5);
		for (Integer i : index.subSet(3, 5)) {
			System.out.println(i);
		}
		Date d1=new Date(1000),d2=new Date(2000);
		System.out.println(d1.compareTo(d2));*/
		
		
		/**
		Engine simEngine=new Engine();
		Workstation eb=new Workstation(),kb=new Workstation(),de=new Workstation(),
				st=new Workstation(),ay=new Workstation(),m=new Workstation();
		eb.setIdentity("Ebatlama");
		kb.setIdentity("Bantlama");
		de.setIdentity("Delme");
		st.setIdentity("�erit Testere");
		ay.setIdentity("Ayak Montaj�");
		m.setIdentity("Masa Montaj");
		//m3.bufferCapacity=3;
		List<SimulationEvent> events=new ArrayList<>();

		Entity ustTabla=new Entity(), ayak=new Entity(), perde=new Entity();
		ustTabla.setIdentity("P1");
		ayak.setIdentity("P2");
		p1.targetList.add(new TransferInfo(m1, "40"));
		p1.targetList.add(new TransferInfo(m3, "30"));
		p2.targetList.add(new TransferInfo(m2, "25"));
		p2.targetList.add(new TransferInfo(m3, "20"));
		Source src=new Source();
		src.setIdentity("S1");
		src.entityBuffer.add(p1);
		src.entityBuffer.add(p2);
		src.timeBetweenArrivals="5";
		src.arrivalTreshold=3;
		Date beginDate=getDate();
		simEngine.initEvent(new SimulationEvent[]{new SimulationEvent(null,src, null, 
				new Date(0), 
				EventType.ArrivalEvent)}, getDate(beginDate,1));
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
		*/
		

		Engine<?> simEngine=new Engine<>();
		TaskContainer m1=new TaskContainer(new Workstation("Ebatlama")),m2=new TaskContainer(new Workstation("Bantlama")),
				m3=new TaskContainer(new Workstation("Montaj\t")), m4=new TaskContainer(new Workstation("Paketleme"));

		m3.getElement().bufferCapacity=1;
		//List<SimulationEvent> events=new ArrayList<>();
		
		Entity p1=new Entity(),p2=new Entity(),p3=new Entity(),p4=new Entity();
		
		AssemblyContainer[] inputs=new AssemblyContainer[] {new AssemblyContainer(p1,1), new AssemblyContainer(p2,1)}; 
		AssemblyContainer[] outputs= {new AssemblyContainer(p3,1,1),new AssemblyContainer(p4,1,1)};
		
		
		p1.setIdentity("P1");
		p2.setIdentity("P2");
		p3.setIdentity("P3");
		p4.setIdentity("P4");
		

		m1.getElement().elementSelectionRule=Task.ElementSelectionRule.FirstInFirstOut;
		m2.getElement().elementSelectionRule=Task.ElementSelectionRule.FirstInFirstOut;
		m3.getElement().elementSelectionRule=Task.ElementSelectionRule.FirstInFirstOut;
		
		p1.addTarget(new TransferInfo(new Process(DispatchingStrategy.FirstAvailable ,new TaskNode(m1,new FixedTimeParameter(40, TimeUnit.second)),
				new TaskNode(m2,new FixedTimeParameter(50, TimeUnit.second)),
				new TaskNode(m3,new FixedTimeParameter(30, TimeUnit.second))))); //output, new TaskNode(m3,"30", inputs)
		//p1.addTarget(new TransferInfo(outputs, new Process(inputs, new TaskNode(m3,new FixedTimeParameter(30, TimeUnit.second)))));
		//p2.addTarget(new TransferInfo(new Process(new TaskNode(m2,new FixedTimeParameter(25, TimeUnit.second)))));
		//p2.addTarget(new TransferInfo(outputs,new Process(inputs, new TaskNode(m3,new FixedTimeParameter(30, TimeUnit.second)))));
		//p3.addTarget(new TransferInfo(new Process(new TaskNode(m3,new FixedTimeParameter(10, TimeUnit.second)))));
		//p4.addTarget(new TransferInfo(new Process(new TaskNode(m4,new FixedTimeParameter(15, TimeUnit.second)))));
		
		
		/**
		p1.addTarget(new TransferInfo(new TaskNode(m1,"40"), 
				new TaskNode(m3,"30")));
		//p1.targetList.add(new TransferInfo(new OperableElementContainer(m3,"30")));
		p2.addTarget(new TransferInfo(new TaskNode(m2,"25"),new TaskNode(m3,"20"))); //
		//p2.addTarget(new TransferInfo(new TaskNode(m3,"20")));
		*/
		
		Source src=new Source();
		src.setIdentity("S1\t");
		src.addElement(p1);
		//src.addElement(p2);

		src.timeBetweenArrivals=new BigDate(5000);
		src.arrivalTreshold=3;
		simEngine.initDefaultBlocks();
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, new BigDate(0))}, new BigDate(365*24*3600000));//getDate(beginDate,1));
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
		
	}
}
