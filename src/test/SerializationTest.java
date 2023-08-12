package test;

import alcybe.simulation.data.containers.TaskContainer;
import alcybe.simulation.model.TaskNode;
import alcybe.simulation.model.parameters.FixedTimeParameter;
import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.SerializableObject;
import alcybe.simulation.objects.Workstation;
import alcybe.simulation.types.Process;
import alcybe.simulation.types.TransferInfo;
import alcybe.utils.data.TimeUnit;


public class SerializationTest {

	public int[] data;
	
	public long[] data1;
	public byte[] data2;
	
	public long val;
	public Long val2;
	public Long[] val3;
	
	public Object o;
	
	static Integer[] intArr=new Integer[5];

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//SerializationTest t=new SerializationTest();
		//t.serialize();
		/**HashMap<String, OperationStatistics> t=new HashMap<>();
		t.put("asdf", new OperationStatistics(5));
		t.put("asdfg", new OperationStatistics(6));*/
		//List<OperationStatistics> t=new ArrayList<>();
		//t.add(new OperationStatistics(5));
		//t.add(new OperationStatistics(6));
		
		Entity t=new Entity(), t1=new Entity();;
		t.setIdentity("deneme");
		t1.setState("state2");
		t.setState("state1");
		
		
		
		
		
		Workstation m1=new Workstation(),m2=new Workstation(),m3=new Workstation(),
				m4=new Workstation();
		m1.setIdentity("M1");
		t.addTarget(new TransferInfo(new Process(new TaskNode(new TaskContainer(m1),new FixedTimeParameter(40, TimeUnit.second)))));
		
		
		byte[] data=t.serialize();
		System.out.println(data.length);
		Entity t2=new Entity();
		t2=(Entity) SerializableObject.deserialize(data);
		
		System.out.println(t2.getIdentity()+" "+t2.getState());
		/**List<Integer> l=new ArrayList<>();
		for (int i = 0; i < 4; i++) 
			l.add(i);
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(t);
        oos.close();
        baos.close();
		
        byte[] data=baos.toByteArray();
        
        ByteArrayInputStream bais=new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        
        HashMap<String, OperationStatistics> t1=(HashMap<String, OperationStatistics>) ois.readObject();
        System.out.println(t1.size());*/
        //List<OperationStatistics> t1 = (List<OperationStatistics>) ois.readObject();
        //System.out.println(t1.size());
        //Entity en=(Entity) ois.readObject();
        //System.out.println(en);
        //List<Integer> l1 = (List<Integer>) ois.readObject();
        //System.out.println(l1.size());
		//System.out.println(t.assemblySequence);
		//t.setValue("assemblySequence", "100");
		//System.out.println(t.assemblySequence);
		/**SerializationTest tt=new SerializationTest();
		Class<?> c=tt.getClass();
		while(c!=null) {
			for(Annotation a:c.getAnnotations())
				System.out.print(a.toString()+"...");
			for(Field f:c.getDeclaredFields()) {
				for(Annotation a:f.getAnnotations())
					System.out.print(a.annotationType().getSimpleName()+":");
				System.out.println(f.getName()+" "+f.getType().getTypeName()+" "+f.getGenericType().getTypeName());
			}
			c=c.getSuperclass();
		}*/
	}

}
