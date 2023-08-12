package test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import alcybe.simulation.types.Task;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;
import alcybe.utils.io.FileAgent;


public class CSMDemo {

	public static void main(String[] args) throws IOException, URISyntaxException {
		
		Engine<?> simEngine=new Engine<>();
		TaskContainer[] taskContainers=new TaskContainer[9];
		taskContainers[0]=new TaskContainer(new Workstation("Torna1"));
		taskContainers[1]=new TaskContainer(new Workstation("Torna2"));
		taskContainers[2]=new TaskContainer(new Workstation("Torna3"));
		taskContainers[3]=new TaskContainer(new Workstation("Torna4"));
		taskContainers[4]=new TaskContainer(new Workstation("Torna5"));
		taskContainers[5]=new TaskContainer(new Workstation("Freze1"));
		taskContainers[6]=new TaskContainer(new Workstation("Freze2"));
		taskContainers[7]=new TaskContainer(new Workstation("Freze3"));
		taskContainers[8]=new TaskContainer(new Workstation("Freze4"));

		
		//List<SimulationEvent> events=new ArrayList<>();
		
		String data=FileAgent.readTextFile(new URI("islem_sureleri.txt"), "UTF-8");
		String[] dataRows=data.replace("\r", "").split("\n");
		Source src=new Source();
		src.setIdentity("S1");
		for(TaskContainer taskContainer:taskContainers) {
			taskContainer.getElement().elementSelectionRule=Task.ElementSelectionRule.FirstInFirstOut;
			taskContainer.getElement().bufferCapacity=1;
		}
		
		for(int i=0; i<dataRows.length; i++) {
			String[] row=dataRows[i].split("\t");
			int r=Integer.parseInt(row[13]);
			for(int j=0;j<r;j++) {
				Entity entity = new Entity();
				entity.setIdentity(row[2]+":"+row[4]+" "+row[5]);
				entity.addTarget(new TransferInfo(new Process(DispatchingStrategy.FirstAvailable, 
						new TaskNode(taskContainers[0],new FixedTimeParameter((int)(Float.parseFloat(row[8])*60), TimeUnit.second), 
								new FixedTimeParameter((int)(Float.parseFloat(row[7])*60), TimeUnit.second)),
						new TaskNode(taskContainers[1],new FixedTimeParameter((int)(Float.parseFloat(row[8])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[7])*60), TimeUnit.second)),
						new TaskNode(taskContainers[2],new FixedTimeParameter((int)(Float.parseFloat(row[8])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[7])*60), TimeUnit.second)),
						new TaskNode(taskContainers[3],new FixedTimeParameter((int)(Float.parseFloat(row[8])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[7])*60), TimeUnit.second)),
						new TaskNode(taskContainers[4],new FixedTimeParameter((int)(Float.parseFloat(row[8])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[7])*60), TimeUnit.second))))); //output, new TaskNode(m3,"30", inputs)
				entity.addTarget(new TransferInfo(new Process(DispatchingStrategy.FirstAvailable, 
						new TaskNode(taskContainers[5],new FixedTimeParameter((int)(Float.parseFloat(row[10])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[9])*60), TimeUnit.second)),
						new TaskNode(taskContainers[6],new FixedTimeParameter((int)(Float.parseFloat(row[10])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[9])*60), TimeUnit.second)),
						new TaskNode(taskContainers[7],new FixedTimeParameter((int)(Float.parseFloat(row[10])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[9])*60), TimeUnit.second)),
						new TaskNode(taskContainers[8],new FixedTimeParameter((int)(Float.parseFloat(row[10])*60), TimeUnit.second),
								new FixedTimeParameter((int)(Float.parseFloat(row[9])*60), TimeUnit.second))))); //output, new TaskNode(m3,"30", inputs)
				src.addElement(entity);
			}
		}

		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		simEngine.initDefaultBlocks();
		BigDate beginDate=new BigDate(2023, 8, 1);
		BigDate endDate=new BigDate(0);
		endDate.addYear(2024);//2024
		simEngine.saveGanttFile=true;
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, beginDate)}, endDate);//getDate(beginDate,1));
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println(((double)end-begin)/1E9d);
		System.out.println(simEngine.getSimulationTime().getTime().intValue());
		
	}
}
