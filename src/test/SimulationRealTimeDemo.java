package test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import alcybe.simulation.types.DispatchingStrategy;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.TransferInfo;
import alcybe.simulation.types.Task.ElementSelectionRule;
import alcybe.utils.data.TimeUnit;
import alcybe.utils.io.FileAgent;

public class SimulationRealTimeDemo {

	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		for(File f :FileAgent.getFileList("datasets/")) {
			if(f.isDirectory()) continue;
			System.out.print(f.getName().replaceAll(".fjs", ""));
			Object[][] dispatching= {{DispatchingStrategy.RealTime, ElementSelectionRule.RealTime},
					{DispatchingStrategy.PerformedTaskDuration, ElementSelectionRule.RealTime},
					{DispatchingStrategy.RealTime, ElementSelectionRule.RealTimeBottleneck},
					{DispatchingStrategy.PerformedTaskDuration, ElementSelectionRule.RealTimeBottleneck},
					{DispatchingStrategy.FirstAvailable, ElementSelectionRule.FirstInFirstOut},
					{DispatchingStrategy.FirstAvailable, ElementSelectionRule.SlackTime}};
			for(int i=0;i<dispatching.length;i++) {
				initEngine(f.getName(), (DispatchingStrategy)dispatching[i][0], 
						(ElementSelectionRule)dispatching[i][1]);
			}
			System.out.println();
			
		}
	}
	
	public static void initEngine(String fileName, DispatchingStrategy wsDispatching, 
			ElementSelectionRule eSelection) throws IOException, URISyntaxException {
		Engine simEngine=new Engine();
		String data=FileAgent.readTextFile(new URI("datasets/"+fileName)); //Barnes_mt10xx.fjs
		String[] lines=data.replaceAll("\r", "").split("\n");
		String[] lengthData=lines[0].replaceAll("   "," ").replaceAll("  ", " ").split(" ");
		int numEntity=Integer.parseInt(lengthData[0]),
				numWS=Integer.parseInt(lengthData[1]);
		
		Source src=new Source();
		src.setIdentity("S1");
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		Entity[] entityList=new Entity[numEntity];
		for(int i=0;i<entityList.length;i++) {
			entityList[i]=new Entity();
			entityList[i].setIdentity("P"+(i+1));
			src.addElement(entityList[i]);
		}
		
		TaskContainer[] wsList=new TaskContainer[numWS];
		for(int i=0;i<wsList.length;i++) {
			wsList[i]=new TaskContainer(new Workstation());
			Workstation ws=(Workstation) wsList[i].getElement();
			ws.elementSelectionRule = eSelection;
			ws.setIdentity("W"+(i+1));
		}
		
		for(int i=1,e=0;e<numEntity;i++,e++) {
			String[] routeData=lines[i].replaceAll("   ", "  ").replaceAll("  ", " ").split(" ");			
			for(int j=1;j<routeData.length;j++) {
				if(!routeData[j].equals("")) {
					int taskNum=Integer.parseInt(routeData[j]);
					TaskNode[] tn=new TaskNode[taskNum];
					j++;
					for(int k=0;k<taskNum;k++) {
						int target=Integer.parseInt(routeData[j])-1;
						tn[k]=new TaskNode(wsList[target],new FixedTimeParameter(Long.parseLong(routeData[j+1]),TimeUnit.second));
						j+=2;
						
					}
					entityList[e].addTarget(new TransferInfo(new Process(wsDispatching, tn)));
					j--;
				}
			}
		}
		//Date beginDate=getDate();
		simEngine.initDefaultBlocks();
		simEngine.showTrace=true;
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, new BigDate(0))}, 
				new BigDate(365*24*3600000));//getDate(beginDate,1));
		simEngine.initDefaultBlocks();
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.print("\t"+((double)end-begin)/1E9d);
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
