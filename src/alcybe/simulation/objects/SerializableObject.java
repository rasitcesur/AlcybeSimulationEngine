package alcybe.simulation.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;


public class SerializableObject implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**public enum SerializationType{
		
		Both,
		CloneOnly,
		SerializeOnly,
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Serializable{
	    SerializationType type() default SerializationType.Both;
	}*/
	
	public byte[] serialize() throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
        
		oos.writeObject(this);
		
		oos.close();
        baos.close();
        return baos.toByteArray();
		
	}
	
	public static byte[] serialize(Object object) throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
        
		oos.writeObject(object);
		
		oos.close();
        baos.close();
        return baos.toByteArray();
		
	}
	
	public static Object deserialize(byte[] data) throws IOException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		ByteArrayInputStream bais=new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
		
		Object o = ois.readObject();
		
		bais.close();
        ois.close();
        
        return o;
	}

	public SerializableObject clone() {
		try {
			byte[] data = serialize();
			SerializableObject object = (SerializableObject) SerializableObject.deserialize(data);
			return object;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void setValue(String name, ByteBuffer buffer) throws Exception {
		Class<?> c=this.getClass();
		Field field=null;
		while(c!=null && field==null) {
			for(Field f:c.getDeclaredFields())
				if(f.getName().contentEquals(name))
					field=f;
			c=c.getSuperclass();
		}	
		setValue(field, buffer);
	}
	
	
	public void setValue(Field field, ByteBuffer buffer) throws Exception {
		Class<?> t = field.getType();
		switch (t.getName()) {
		case "java.util.List":
		case "java.util.ArrayList":
		case "java.util.Vector":
		case "java.util.HashMap":
			break;
		case "java.lang.String":{
			int len=buffer.getInt();
			byte[] dst=new byte[len];
			buffer.get(dst);
			field.set(this, new String(dst));
			break;
		}
		case "byte[]":{
			int len=buffer.getInt();
			byte[] dst=new byte[len];
			buffer.get(dst);
			field.set(this, dst);
			break;
		}
		case "int[]":{
			int len=buffer.getInt();
			int[] dst=new int[len];
			buffer.asIntBuffer().get(dst);
			field.set(this, dst);
			break;
		}
		case "boolean":
		case "java.lang.Boolean":
			field.set(this, buffer.get()==1);
			break;
		case "byte":
		case "java.lang.Byte":
			field.set(this, buffer.get());
			break;
		case "short":
		case "java.lang.Short":
			field.set(this, buffer.getShort());
			break;
		case "int":
		case "java.lang.Integer":
			field.set(this, buffer.getInt());
			break;
		case "long":
		case "java.lang.Long":
			field.set(this, buffer.getLong());
			break;
		case "float":
		case "java.lang.Float":
			field.set(this, buffer.getFloat());
			break;
		case "double":
		case "java.lang.Double":
			field.set(this, buffer.getDouble());
			break;
		case "java.sql.Date":
			//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			field.set(this, new Date(buffer.getLong()));
			break;
		case "java.sql.Timestamp":
			field.set(this, new Timestamp(buffer.getLong()));
			break;
		}
	}
}
