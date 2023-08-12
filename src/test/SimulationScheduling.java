package test;

import java.util.Calendar;
import java.util.Date;

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

public class SimulationScheduling {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Engine simEngine=new Engine();
		TaskContainer ebat=new TaskContainer(new Workstation("1.Ebatlama")),st=new TaskContainer(new Workstation("2.Serit Testere")),
				bant=new TaskContainer(new Workstation("3.Bantlama")), del=new TaskContainer(new Workstation("4.Delme")), 
				aym=new TaskContainer(new Workstation("5.Ayak Montaj")), mon=new TaskContainer(new Workstation("6.Montaj"));

		
		
		//m3.bufferCapacity=1;
		//List<SimulationEvent> events=new ArrayList<>();

		Entity sehpa=new Entity(),masa=new Entity(),s_ust_tabla=new Entity(),s_ayak1=new Entity(),s_ayak2=new Entity(),
				s_ayak_komplesi=new Entity(), m_ayak_komplesi=new Entity(), 
				m_ust_tabla=new Entity(),m_ayak1=new Entity(),m_ayak2=new Entity(),m_perde=new Entity();
		
		
		s_ust_tabla.setIdentity("1.S_Ust_Tabla"); 
		s_ayak1.setIdentity("2.S_Ayak1");
		s_ayak2.setIdentity("2.S_Ayak2");
		s_ayak_komplesi.setIdentity("3.S_Ayak_Komplesi");
		sehpa.setIdentity("4.Sehpa");
		m_ust_tabla.setIdentity("5.M_Ust_Tabla"); 
		m_ayak1.setIdentity("7.M_Ayak1");
		m_ayak2.setIdentity("7.M_Ayak2");
		m_perde.setIdentity("6.M_Perde");
		m_ayak_komplesi.setIdentity("8.M_Ayak_Komplesi");
		masa.setIdentity("9.Masa");
				
		//BomElement[] inputs=new BomElement[] {new BomElement(p1,1), new BomElement(p2,1)}; 
		AssemblyContainer[] s_outputs= {new AssemblyContainer(sehpa,1,1)};
		AssemblyContainer[] s_inputs=new AssemblyContainer[] {new AssemblyContainer(s_ust_tabla,1), new AssemblyContainer(s_ayak_komplesi,1)}; 
		
		AssemblyContainer[] sa_outputs= {new AssemblyContainer(s_ayak_komplesi,1,1)};
		AssemblyContainer[] sa_inputs=new AssemblyContainer[] {new AssemblyContainer(s_ayak1,1), 
				new AssemblyContainer(s_ayak2,1)}; 
		
		AssemblyContainer[] m_outputs= {new AssemblyContainer(masa,1,1)};
		AssemblyContainer[] m_inputs=new AssemblyContainer[] {new AssemblyContainer(m_ust_tabla,1), new AssemblyContainer(m_ayak_komplesi,1), 
				new AssemblyContainer(m_perde,1)}; 
		
		AssemblyContainer[] ma_outputs= {new AssemblyContainer(m_ayak_komplesi,1,1)};
		AssemblyContainer[] ma_inputs=new AssemblyContainer[] {new AssemblyContainer(m_ayak1,1), 
				new AssemblyContainer(m_ayak2,1)}; 
				
		s_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(ebat,new FixedTimeParameter(30, TimeUnit.second)))));
		s_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(bant,new FixedTimeParameter(60, TimeUnit.second)))));
		s_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(90, TimeUnit.second)))));
		s_ust_tabla.addTarget(new TransferInfo(s_outputs, new Process(1, s_inputs,new TaskNode(mon,new FixedTimeParameter(120, TimeUnit.second))))); 
		
		s_ayak1.addTarget(new TransferInfo(new Process(new TaskNode(st,new FixedTimeParameter(40, TimeUnit.second)))));
		s_ayak1.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(90, TimeUnit.second)))));
		s_ayak1.addTarget(new TransferInfo(sa_outputs, new Process(2, sa_inputs, new TaskNode(aym,new FixedTimeParameter(100, TimeUnit.second)))));
		
		s_ayak2.addTarget(new TransferInfo(new Process(new TaskNode(st,new FixedTimeParameter(40, TimeUnit.second)))));
		s_ayak2.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(90, TimeUnit.second)))));
		s_ayak2.addTarget(new TransferInfo(sa_outputs, new Process(3, sa_inputs, new TaskNode(aym,new FixedTimeParameter(100, TimeUnit.second)))));
		
		s_ayak_komplesi.addTarget(new TransferInfo(s_outputs, new Process(4, s_inputs,new TaskNode(mon,new FixedTimeParameter(120, TimeUnit.second))))); 
		
		
		m_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(ebat,new FixedTimeParameter(100, TimeUnit.second)))));
		m_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(bant,new FixedTimeParameter(120, TimeUnit.second)))));
		m_ust_tabla.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(60, TimeUnit.second)))));
		m_ust_tabla.addTarget(new TransferInfo(m_outputs, new Process(5, m_inputs,new TaskNode(mon,new FixedTimeParameter(300, TimeUnit.second))))); 
		
		m_perde.addTarget(new TransferInfo(new Process(new TaskNode(ebat,new FixedTimeParameter(90, TimeUnit.second)))));
		m_perde.addTarget(new TransferInfo(new Process(new TaskNode(bant,new FixedTimeParameter(70, TimeUnit.second)))));
		m_perde.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(60, TimeUnit.second)))));
		m_perde.addTarget(new TransferInfo(m_outputs, new Process(6, m_inputs,new TaskNode(mon,new FixedTimeParameter(300, TimeUnit.second))))); 
		
		m_ayak1.addTarget(new TransferInfo(new Process(new TaskNode(st,new FixedTimeParameter(140, TimeUnit.second)))));
		m_ayak1.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(120, TimeUnit.second)))));
		m_ayak1.addTarget(new TransferInfo(ma_outputs, new Process(7, ma_inputs, new TaskNode(aym, new FixedTimeParameter(120, TimeUnit.second)))));
		
		m_ayak2.addTarget(new TransferInfo(new Process(new TaskNode(st,new FixedTimeParameter(140, TimeUnit.second)))));
		m_ayak2.addTarget(new TransferInfo(new Process(new TaskNode(del,new FixedTimeParameter(120, TimeUnit.second)))));
		m_ayak2.addTarget(new TransferInfo(ma_outputs, new Process(8, ma_inputs, new TaskNode(aym,new FixedTimeParameter(120, TimeUnit.second)))));
		
		m_ayak_komplesi.addTarget(new TransferInfo(m_outputs, new Process(9, m_inputs,new TaskNode(mon, new FixedTimeParameter(300, TimeUnit.second))))); 
		
		
		/**
		p1.addTarget(new TransferInfo(new Bom(new TaskNode(m1,"40")), new Bom(new TaskNode(m3,"30"))));
		//p1.addTarget(new TransferInfo(new Bom(new TaskNode(m3,"30"))));
		p2.addTarget(new TransferInfo(new Bom(new TaskNode(m2,"25"))));// , new Bom(new TaskNode(m3,"20"))));
		p2.addTarget(new TransferInfo(new Bom(new TaskNode(m3,"20"))));
		*/
		
		Source src=new Source();
		src.setIdentity("S1");
		
		//aym.bufferCapacity=2;
		
		src.addElement(s_ayak1);
		src.addElement(m_ayak1);
		src.addElement(m_ayak2);
		src.addElement(m_perde);
		src.addElement(m_ust_tabla);
		
		src.addElement(s_ust_tabla);
		src.addElement(s_ayak2);

		
		
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		//Date beginDate=getDate();
		simEngine.initDefaultBlocks();
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, new BigDate(0))}, new BigDate(365*24*3600000));//getDate(beginDate,1));
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
