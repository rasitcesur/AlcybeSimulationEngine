package test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.events.ArrivalEvent;
import alcybe.simulation.events.DiscreteEvent;
import alcybe.simulation.events.utils.EventType;
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

public class LineBalancing {
	
	public static void initEngine(String fileName, int entityCount, DispatchingStrategy wsDispatching, 
			ElementSelectionRule eSelection) throws IOException, URISyntaxException {
		Engine simEngine=new Engine();
		
		
		Source src=new Source();
		src.setIdentity("S1");
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		
		Entity[] entityList=new Entity[entityCount];
		
		for(int i=0;i<entityList.length;i++) {
			entityList[i]=new Entity();
			entityList[i].setIdentity("P"+(i+1));
			src.addElement(entityList[i]);
		}
		
		String data=FileAgent.readTextFile(new URI(fileName));
		HashMap<String, TaskContainer> wsList=new HashMap<>(); 
		String[] lines=data.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if(!lines[i].equals("")) {
				String[] row=lines[i].split("\t");
				TaskContainer tc=null;
				if(!wsList.containsKey(row[0])) {
					Workstation ws=new Workstation();
					ws.identity=row[0];
					ws.elementSelectionRule=eSelection;
					tc=new TaskContainer(ws);
					wsList.put(row[0], tc);
					
				} else
					tc=wsList.get(row[0]);
				
				for(int j=0;j<entityList.length;j++) {
					TaskNode tn=new TaskNode(tc, new FixedTimeParameter(Long.parseLong(row[2].trim()), TimeUnit.second));
					tn.priority=j;
					entityList[j].addTarget(new TransferInfo(new Process(wsDispatching , tn)));
				}
				
			}
		}

		simEngine.showTrace=true;
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, new BigDate(0))}, 
				new BigDate(365*24*3600000));//getDate(beginDate,1));
		simEngine.initDefaultBlocks();
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.print("\t"+((double)end-begin)/1E9d);
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		initEngine("datasets_line/data.txt", 2, 
				DispatchingStrategy.FirstAvailable, ElementSelectionRule.LowestPriority);
		
	}

}
