package test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import alcybe.simulation.objects.Entity;
import alcybe.simulation.objects.Store;
import alcybe.simulation.objects.TransferableElement;
import alcybe.utils.io.FileAgent;
import alcybe.utils.io.FileAgent.FileCreateMode;
import alcybe.utils.io.SerializationAgent;

public class ElementSizeTest {

	public static void main(String[] args) throws IOException, URISyntaxException {
		Entity e=new Entity();
		TransferableElement te=new TransferableElement();
		Store s=new Store();
		SerialElement se=new SerialElement();
		byte[] b = SerializationAgent.serialize(s);
		System.out.println(b.length);
		/**for (int i = 0; i < b.length; i++) {
			if(b[i]==0) b[i]=-1;
		}
		FileAgent.writeFile(new URI("entity.txt"), b, FileCreateMode.IfNotExists);
		System.out.println(new String(b));*/
		//System.out.println((long)b.length*84_000_000/(1024*1024*1024));
		/**List<Entity> eList=new ArrayList<>(1000);
		for (int i = 0; i < 10_000_000; i++) {
			eList.add(new Entity());
			System.out.println(i);
		}*/
	}

}
