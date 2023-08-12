package test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import alcybe.utils.io.FileAgent;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;


public class CSMDemoGenetic {

	
	private static Engine<?> buildModel(Genotype<IntegerGene> gt) {
		
		Engine<?> simEngine=new Engine<>();
		TaskContainer torna=new TaskContainer(new Workstation("Torna")),freze=new TaskContainer(new Workstation("Freze"));

		torna.getElement().bufferCapacity=3;
		freze.getElement().bufferCapacity=4;
		//List<SimulationEvent> events=new ArrayList<>();
		
		String data="";
		try {
			data = FileAgent.readTextFile(new URI("islem_sureleri.txt"), "UTF-8");
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] dataRows=data.replace("\r", "").split("\n");
		Source src=new Source();
		src.setIdentity("S1");
		
		torna.getElement().elementSelectionRule=Task.ElementSelectionRule.HighestPriority;
		freze.getElement().elementSelectionRule=Task.ElementSelectionRule.HighestPriority;
		
		for(int i=0; i<dataRows.length; i++) {
			String[] row=dataRows[i].split("\t");
			Entity entity = new Entity();
			entity.setIdentity(row[2]+":"+row[4]+" "+row[5]);
			
			TaskNode tornaTN=new TaskNode(torna,new FixedTimeParameter((int)(Float.parseFloat(row[11])*60), TimeUnit.second));
			TaskNode frezeTN=new TaskNode(freze,new FixedTimeParameter((int)(Float.parseFloat(row[12])*60), TimeUnit.second));
			tornaTN.priority=gt.chromosome().get(i).doubleValue();
			frezeTN.priority=gt.chromosome().get(i).doubleValue();
			
			entity.addTarget(new TransferInfo(new Process(DispatchingStrategy.FirstAvailable, tornaTN))); //output, new TaskNode(m3,"30", inputs)
			entity.addTarget(new TransferInfo(new Process(DispatchingStrategy.FirstAvailable, frezeTN))); //output, new TaskNode(m3,"30", inputs)
			src.addElement(entity);
		}

		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		simEngine.showTrace=false;
		simEngine.initDefaultBlocks();
		BigDate beginDate=new BigDate(0);
		//beginDate.addYear(2023);
		BigDate endDate=new BigDate(0);
		endDate.addYear(1);
		simEngine.initEvent(new DiscreteEvent[]{new ArrivalEvent(src, beginDate)}, endDate);//getDate(beginDate,1));
		//long begin=System.nanoTime();
		simEngine.run();
		//long end=System.nanoTime();
		
		
		return simEngine;
	}
	
	// 2.) Definition of the fitness function.
    private static int eval(Genotype<IntegerGene> gt){   
    	
    	Engine<?> simEngine=buildModel(gt);
    	System.out.println(simEngine.getSimulationTime().getTime().intValue());
    	return -simEngine.getSimulationTime().getTime().intValue();
        
    }
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
	
		// 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf =
            Genotype.of(IntegerChromosome.of(0, 1000000, 1406));

        // 3.) Create the execution environment.
        io.jenetics.engine.Engine<IntegerGene, Integer> engine = io.jenetics.engine.Engine
            .builder(CSMDemoGenetic::eval, gtf)
            .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(100).collect(EvolutionResult.toBestGenotype());

        System.out.println("*******************Best Solution*******************");
        System.out.println(result);
        buildModel(result);
		
	}

}
