package test;

import java.math.BigDecimal;
import java.math.MathContext;

import alcybe.data.BigDate;
import alcybe.utils.data.TimeUnit;

public class TransportTest {

	public static class TransportationInfo{
		public static MathContext mathContext=MathContext.DECIMAL128;
	}
	
	public static class MotionInfo{
		public BigDate duration;
		public BigDecimal acceleration;
		
		public MotionInfo() {}
		
		public MotionInfo(BigDate duration, BigDecimal acceleration) {
			this.duration = duration;
			this.acceleration = acceleration;
		}
	}
	
	public static class DisplacementInfo{
		public BigDate duration;
		public BigDecimal displacement;
		public BigDecimal velocity;
		
		public DisplacementInfo() { }
		
		public DisplacementInfo(BigDate duration, BigDecimal displacement, BigDecimal velocity) { 
			this.duration = duration;
			this.displacement=displacement;
			this.velocity=velocity;
		}
		
		@Override
		public String toString() {
			return duration+" "+displacement+" "+velocity;
		}
	}
	
	public static class StopInfo extends DisplacementInfo{
		public BigDate motionTime;
		public MotionInfo motionInfo;
		public int motionIndex;
		
		
		public StopInfo() { }
		
		public StopInfo(BigDate duration, BigDecimal displacement, BigDecimal velocity, BigDate motionTime, MotionInfo motionInfo,
				int motionIndex) { 
			super(duration, displacement, velocity);
			this.motionTime=motionTime;
			this.motionInfo=motionInfo;
			this.motionIndex=motionIndex;
		}
		
		@Override
		public String toString() {
			return duration+" "+displacement+" "+velocity;
		}
	}
	
	static BigDecimal secondDivider=new BigDecimal(TimeUnit.second.getDoubleValue()), zero=new BigDecimal(0), two=new BigDecimal(2),
			threeSix=new BigDecimal(36).movePointLeft(1);
	
	public static DisplacementInfo getDisplacement(BigDecimal velocity, MotionInfo[] motionInfo) {
		return getDisplacement(velocity, motionInfo, motionInfo.length);
	}
	
	public static DisplacementInfo getDisplacement(BigDecimal velocity, MotionInfo[] motionInfo, int length) {
		BigDecimal oldSpeed = velocity, newSpeed = new BigDecimal(0), displacement = new BigDecimal(0);
		BigDate duration=new BigDate(0);
		for (int i=0; i < length; i++){
        	MotionInfo a = motionInfo[i];
        	duration.add(a.duration);
            BigDecimal t=a.duration.getTime().divide(secondDivider);
            newSpeed = oldSpeed.add(t.multiply(a.acceleration));
            displacement = displacement.add(oldSpeed.add(newSpeed).multiply(t).divide(two));
            oldSpeed=newSpeed.add(zero);
        }
		return new DisplacementInfo(duration, oldSpeed,displacement);
	}
	
	public static StopInfo getDisplacementToStop(BigDecimal velocity, MotionInfo[] motionInfo) throws Exception {
		BigDecimal oldSpeed = velocity, newSpeed = new BigDecimal(0), displacement = new BigDecimal(0);
		BigDate duration=new BigDate(0);
		MotionInfo lastInfo=null;
		BigDate motionTime=null;
		for (int i=0; i < motionInfo.length && oldSpeed.compareTo(zero)>0; i++){
        	lastInfo = motionInfo[i];
            BigDecimal t=lastInfo.duration.getTime().divide(secondDivider, TransportationInfo.mathContext);
            newSpeed = oldSpeed.add(t.multiply(lastInfo.acceleration));
            if(newSpeed.compareTo(zero)<0) {
            	newSpeed=zero;
            	t=oldSpeed.divide(lastInfo.acceleration, TransportationInfo.mathContext).negate();
            }
            duration.add(new BigDate(t.multiply(secondDivider)));
            displacement = displacement.add(oldSpeed.add(newSpeed).multiply(t).divide(two));
            oldSpeed=newSpeed.add(zero);
        }
		if(oldSpeed.compareTo(zero)!=0)
			throw new Exception();
		return null;//new StopInfo(duration, displacement, oldSpeed,);
	}
	
	public static BigDecimal kmhToMs(BigDecimal value) {
		return value.divide(threeSix);
	}
	
	public static BigDecimal msToKmh(BigDecimal value) {
		return value.multiply(threeSix);
	}
	
	public static BigDate getDuration(BigDecimal velocity, BigDecimal displacement) {
		return new BigDate(displacement.movePointRight(3).divide(velocity, TransportationInfo.mathContext));
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		double newLocation=0;
        double oldSpeed=0 , newSpeed = 0;
        MotionInfo[] speedUp = {new MotionInfo(new BigDate(3000), new BigDecimal(1)), new MotionInfo(new BigDate(4000), new BigDecimal(3))};
        
        MotionInfo[] slowDown = {new MotionInfo(new BigDate(1000), new BigDecimal(-1)), new MotionInfo(new BigDate(4000), new BigDecimal(-2)),
        		new MotionInfo(new BigDate(2000), new BigDecimal(-3))};
        
        DisplacementInfo sUDisplacement=getDisplacement(new BigDecimal(0), speedUp);
        DisplacementInfo sDDisplacement=getDisplacement(new BigDecimal(15), slowDown);
        BigDecimal totalDisplacement=sUDisplacement.displacement.add(sDDisplacement.displacement);
        
        System.out.println(sUDisplacement);
        System.out.println(getDisplacementToStop(new BigDecimal(13), slowDown));
        System.out.println(sDDisplacement);
        
        BigDecimal endTime=new BigDecimal(155000), secondDivider=new BigDecimal(TimeUnit.second.getDoubleValue());
        Way way=new Way(600);
        way.addNode(300);
        way.addNode(600);
        
        BigDecimal start=new BigDecimal(0);
        for(int k= way.getNodeContainers().length - 1;k>-1;k--) {
        	
        	NodeContainer c=way.getNodeContainers()[k];
        	Node n=c.nodes.get(0);

        	BigDecimal wayLength = n.relativeX.subtract(start);
        	if(totalDisplacement.compareTo(wayLength)>0) {
        		BigDecimal residual=totalDisplacement.subtract(wayLength);
        		BigDecimal totalDecrement=new BigDecimal(0);
        		/**while(residual.compareTo(totalDecrement) > 0) {
        			DisplacementInfo suInfo=getDisplacement(new BigDecimal(0), speedUp, speedUp.length-1);
        			totalDecrement=totalDisplacement.subtract(suInfo.displacement.add(start));
        			totalDecrement.subtract(getDisplacementToStop(new BigDecimal(12), slowDown).displacement);
        		}*/
        	}else {
        	
	            wayLength=wayLength.subtract(totalDisplacement);
	            
	            int i=0;
	            
	            BigDate time = new BigDate();
	            for (; i < speedUp.length && time.getTime().compareTo(endTime) < 0;time.add(speedUp[i].duration), i++){
	            	MotionInfo a = speedUp[i];
	            	
	                BigDecimal t=a.duration.getTime().divide(secondDivider);
	                newSpeed = oldSpeed + t.multiply(a.acceleration).doubleValue();
	                newLocation += (oldSpeed+newSpeed)/2*t.doubleValue();
	                BigDate end=new BigDate(time.getTime());
	                end.add(a.duration);
	                System.out.println(
	                        time + " - " + end +" " +
	                                newSpeed + " m/s hızla x ekseninde "+ newLocation + " konumundadır."
	                );
	                oldSpeed=newSpeed;
	            }
	            
	            {
	            	MotionInfo a = new MotionInfo(getDuration(new BigDecimal(15), wayLength), new BigDecimal(0));
	    	    	BigDecimal t=a.duration.getTime().divide(secondDivider);
	    	        newSpeed = oldSpeed + t.multiply(a.acceleration).doubleValue();
	    	        newLocation += (oldSpeed+newSpeed)/2*t.doubleValue();
	    	        BigDate end=new BigDate(time.getTime());
	    	        end.add(a.duration);
	    	        System.out.println(
	    	                time + " - " + end +" " +
	    	                        newSpeed + " m/s hızla x ekseninde "+
	    	                        newLocation + " konumundadır."
	    	        );
	    	        oldSpeed=newSpeed;
	    	        time.add(a.duration);
	            }
	            
	            i = 0;
	            for (; i < slowDown.length && time.getTime().compareTo(endTime) < 0;time.add(slowDown[i].duration), i++){
	            	MotionInfo a = slowDown[i];
	            	
	                BigDecimal t=a.duration.getTime().divide(secondDivider);
	                newSpeed = oldSpeed + t.multiply(a.acceleration).doubleValue();
	                newLocation += (oldSpeed+newSpeed)/2*t.doubleValue();
	                BigDate end=new BigDate(time.getTime());
	                end.add(a.duration);
	                System.out.println(
	                        time + " - " + end +" " +
	                                newSpeed + " m/s hızla x ekseninde "+
	                                newLocation + " konumundadır."
	                );
	                oldSpeed=newSpeed;
	            }
        	}
            
            start=n.relativeX;
            
        }
	}

}
