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

public class ShiftTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Engine<?> simEngine=new Engine<>();
		Workstation m1=new Workstation(),m2=new Workstation();
		m1.setIdentity("M1");
		m2.setIdentity("M2");
		
		//List<SimulationEvent> events=new ArrayList<>();

		TaskContainer t1=new TaskContainer(m1), t2=new TaskContainer(m2);
		Entity p1=new Entity();
		
		p1.setIdentity("P1");
		
		p1.addTarget(new TransferInfo(new Process(new TaskNode(t1,new FixedTimeParameter(6, TimeUnit.hour))))); 
		p1.addTarget(new TransferInfo(new Process(new TaskNode(t2,new FixedTimeParameter(6, TimeUnit.hour))))); 
		
		Source src=new Source();
		src.setIdentity("S1");
		src.addElement(p1);
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		//Date beginDate=getDate();
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, 
				new BigDate(0))}, new BigDate(360000000));//getDate(beginDate,1));
		simEngine.initDefaultBlocks();
		simEngine.showTrace=true;
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
	}

}
