package test;

import java.io.IOException;
import java.util.ArrayList;

import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.SerializableObject;
import alcybe.simulation.objects.SimulationObject;
import alcybe.simulation.objects.TransferableElement;

public class Covid19MemTest {

	public static class AlcybeRunnable implements Runnable{

		int x=-1;
		public ArrayList<Entity> aL=new ArrayList<>(30_000_000);
		public AlcybeRunnable(final int x) {
			this.x=x;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println(x +"started!");
			
			for (int j = 0; j < 3_000_000; j++) {
				aL.add(new Entity());
			}
			System.out.println(x +"finished!");

		}
		
	}
	
	public static ArrayList<TransferableElement> aL=new ArrayList<>(1_000_000);
	
	public static void main(String[] args) throws InterruptedException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		// TODO Auto-generated method stub
		/**Thread[] t=new Thread[4];
		AlcybeRunnable[] r=new AlcybeRunnable[t.length];
		for(int i=0;i<t.length;i++) {
			r[i]=new AlcybeRunnable(i);
			t[i]=new Thread(r[i]);
			t[i].start();
		}
		for(int i=0;i<t.length;i++) 
			t[i].join();
		int size=0;
		for(int i=0;i<t.length;i++) 
			size+=r[i].aL.size();
		System.out.println(size+" finished...");*/
		int entityCount=84_000_000;
		for (int i = 0; i < entityCount; i++) {
			TransferableElement en=new TransferableElement();
			en.setIdentity("asdfg");
			aL.add(en);
			System.out.println(i);
		}

		System.out.println(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		System.out.println("serializing");
		/**long total=0;
		for (int i = 0; i < entityCount; i++) {
			int length=aL.get(i).serialize().length;
			total+=aL.get(i).serialize().length;
			System.out.println(i+" "+length);
		}
		System.out.println(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		System.out.println((double)total/(1024.0*1024.0*1024.0));*/
		//System.out.println(new Entity().serialize().length);
	}

}
