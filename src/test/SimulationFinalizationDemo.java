package test;

import java.util.Calendar;
import java.util.Date;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
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

public class SimulationFinalizationDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Engine simEngine=new Engine();
		Workstation m1=new Workstation(),m2=new Workstation(),m3=new Workstation();
		m1.setIdentity("1.Kesim");
		m2.setIdentity("2.Yüzey İşleme");
		m3.setIdentity("3.Montaj");
		
		TaskContainer t1=new TaskContainer(m1), t2=new TaskContainer(m2),t3=new TaskContainer(m3);
		//List<SimulationEvent> events=new ArrayList<>();

		Entity p1=new Entity(),p2=new Entity();//,p3=new Entity(),p4=new Entity();
		
		
		p1.setIdentity("P1");
		p2.setIdentity("P2");
		
		
		
		p1.addTarget(new TransferInfo(new Process(new TaskNode(t1, new FixedTimeParameter(40, TimeUnit.second))), new Process(new TaskNode(t3, 
				new FixedTimeParameter(30, TimeUnit.second)))));
		//p1.addTarget(new TransferInfo(new Process(new TaskNode(t3,"30"))));
		p2.addTarget(new TransferInfo(new Process(new TaskNode(t2, new FixedTimeParameter(25, TimeUnit.second))) , new Process(new TaskNode(t3,
				new FixedTimeParameter(20, TimeUnit.second)))));
		//p2.addTarget(new TransferInfo(new Process(new TaskNode(t3,"20"))));
		
		
		Source src=new Source();
		src.setIdentity("S1\t");
		src.addElement(p1);
		src.addElement(p2);
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		//Date beginDate=getDate();
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, 
				new BigDate(0))}, new BigDate(365*24*3600000));//getDate(beginDate,1));
		simEngine.initDefaultBlocks();
		simEngine.showTrace=true;
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
	}
	
	public static Date getDate(){
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	public static Date getDate(Date begin, int timespan){
		Calendar cal = Calendar.getInstance();
		cal.setTime(begin);
		cal.add(Calendar.YEAR, timespan);
		return cal.getTime();
	}
}
