package test;

import java.math.BigDecimal;

public class BigNumberTest {

	public static void main(String[] args) {
		System.out.println(new BigDecimal(1).movePointLeft(9));
		
		// TODO Auto-generated method stub
		BigDecimal bd=new BigDecimal(5000);
		bd.pow(0);
		bd=bd.divide(new BigDecimal(10000000000l));
		bd=bd.add(new BigDecimal(100000l));
		System.out.println(bd);
	}

}
