package test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import alcybe.simulation.objects.SimulationObject;

public class Node extends SimulationObject implements  Comparable<Node>{

	private static final long serialVersionUID = 1L;
	public BigDecimal x = new BigDecimal(0), y = new BigDecimal(0), z = new BigDecimal(0), 
			relativeX = new BigDecimal(0), relativeY = new BigDecimal(0), relativeZ = new BigDecimal(0);
	 
	private static final BigDecimal SQRT_DIG = new BigDecimal(150);
	private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

	public Node() { 
		
	}
	
	public Node(long relativeX) { 
		this.relativeX = new BigDecimal(relativeX);
	}
	
	
	public Node(long relativeX, long relativeY) { 
		this.relativeX = new BigDecimal(relativeX);
		this.relativeY = new BigDecimal(relativeY);
	}
	
	public Node(long relativeX, long relativeY, long relativeZ) { 
		this.relativeX = new BigDecimal(relativeX);
		this.relativeY = new BigDecimal(relativeY);
		this.relativeZ = new BigDecimal(relativeZ);
	}
	
	/**
	 * Private utility method used to compute the square root of a BigDecimal.
	 * 
	 * @author Luciano Culacciatti 
	 * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
	 */
	private static BigDecimal sqrtNewtonRaphson  (BigDecimal c, BigDecimal xn, BigDecimal precision){
	    BigDecimal fx = xn.pow(2).add(c.negate());
	    BigDecimal fpx = xn.multiply(new BigDecimal(2));
	    BigDecimal xn1 = fx.divide(fpx,2*SQRT_DIG.intValue(),RoundingMode.HALF_DOWN);
	    xn1 = xn.add(xn1.negate());
	    BigDecimal currentSquare = xn1.pow(2);
	    BigDecimal currentPrecision = currentSquare.subtract(c);
	    currentPrecision = currentPrecision.abs();
	    if (currentPrecision.compareTo(precision) <= -1){
	        return xn1;
	    }
	    return sqrtNewtonRaphson(c, xn1, precision);
	}

	/**
	 * Uses Newton Raphson to compute the square root of a BigDecimal.
	 * 
	 * @author Luciano Culacciatti 
	 * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
	 */
	public static BigDecimal sqrt(BigDecimal c){
	    return sqrtNewtonRaphson(c,new BigDecimal(1),new BigDecimal(1).divide(SQRT_PRE));
	}
	
	
	public BigDecimal getRelativeDistance() {
		return sqrt(relativeX.pow(2).add(relativeY.pow(2)));
	}
	
	@Override
	public int compareTo(Node o) {
		return getRelativeDistance().compareTo(o.getRelativeDistance());
	}

}
