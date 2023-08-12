package test;

import alcybe.data.BigDate;
import alcybe.simulation.Engine;
import alcybe.simulation.data.containers.EventContainer;
import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.events.ArrivalEvent;
import alcybe.simulation.events.ContinuousEvent;
import alcybe.simulation.events.UserDefinedEvent;
import alcybe.simulation.events.UserEvent;
import alcybe.simulation.events.utils.EventList;
import alcybe.simulation.events.utils.EventType;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.ContinuousParameter;
import alcybe.simulation.model.parameters.FixedTimeParameter;
import alcybe.simulation.model.parameters.OperationParameter;
import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.Source;
import alcybe.simulation.objects.Workstation;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;

public class UserDefinedEventTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("rawtypes")
		Engine<?> simEngine=new Engine();
		/**simEngine.addEvent(new ContinuousEvent(new BigDate(0), new BigDate(1000)) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void act(Engine<?> engine, BigDate simulationTime) {
				// TODO Auto-generated method stub
				System.out.println(simulationTime.toString());
			}
		});*/
		
		simEngine.eventList=new EventList() {
			
			@Override
			public void init() {
				// TODO Auto-generated method stub
				super.events=new UserDefinedEvent[] {
					new UserDefinedEvent() {
							
						private static final long serialVersionUID = 1L;

						@Override
						public void act(@SuppressWarnings("rawtypes") Engine simulationEngine, EventContainer eventContainer) {
							
							System.out.println("UD:"+simulationEngine.getSimulationTime().toString());
						}
					}
				};
			}
		};
		
		BigDate endDate=new BigDate(0);
		endDate.addSecond(50);
		simEngine.setSimulationEndTime(endDate);
		
		ContinuousParameter<Integer> param=new ContinuousParameter<Integer>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Integer get() {
				return value;
			}
			
			@Override
			public boolean update() {
				this.value+=50;
				System.out.println("parameter:"+this.value);
				return value>200;
			}
		};
		
		param.init(0, simEngine, null,null,new BigDate(0), new BigDate(1000), null);
		simEngine.addEvent(new UserEvent(EventType.UserDefinedEvent, null, new BigDate(0), 0));
		
		Workstation m1=new Workstation();
		m1.setIdentity("1.Kesim");
		
		TaskContainer t1=new TaskContainer(m1);
		
		Entity p1=new Entity();
		p1.setIdentity("P1");
			
		TaskNode tn=new TaskNode(t1,new FixedTimeParameter(40, TimeUnit.second));
		
		OperationParameter<Double> energyConsumption=new OperationParameter<Double>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean update() {
				value+=10;
				
				System.out.println("operation:"+value);
				
				//if(value<100)
				//	return false;
				//cancel();
				return false;
			}

			@Override
			public Double get() {
				
				return value;
			}
		};
		energyConsumption.init(0.0);
		energyConsumption.duration=new BigDate(0);
		energyConsumption.duration.addSecond(1);
		tn.operationParameters.add(energyConsumption);
		p1.addTarget(new TransferInfo(new Process(tn)));//, new Process(new TaskNode(m3,"30"))));
		
		Source src=new Source();
		src.setIdentity("S1");
		src.addElement(p1);
		src.timeBetweenArrivals=new BigDate(0);
		src.arrivalTreshold=1;
		//Date beginDate=getDate();
		simEngine.addEvent(new ArrivalEvent(src, new BigDate(0)));
		
		simEngine.initDefaultBlocks();
		simEngine.showTrace=true;
		long begin=System.nanoTime();
		simEngine.run();
		long end=System.nanoTime();
		System.out.println("param:"+param.value);
		System.out.println("energy:"+tn.operationParameters.get(0).value);
		System.out.println(((double)end-begin)/1E9d);
	}

}
