package alcybe.simulation.model;

import java.util.List;

import alcybe.data.BigDate;
import alcybe.simulation.objects.SerializableObject;

public class Shift extends SerializableObject implements Comparable<Shift> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BigDate begin;
	public BigDate end;
	
	public Shift() {}
	
	public Shift(BigDate begin, BigDate end) {
		this.begin=begin;
		this.end=end;
	}
	
	@Override
	public int compareTo(Shift shift) {
		// TODO Auto-generated method stub
		int result = this.begin.compareTo(shift.begin);
		if(result==0)
			return this.end.compareTo(shift.end);
		return result;
	}
	

	private static BigDate zero=new BigDate(0);
	public static BigDate[] getIdleDuration(BigDate operationBegin, BigDate operationEnd, List<Shift> shifts) {
		BigDate operationDuration=new BigDate(operationEnd.getTime());
		operationDuration.subtract(operationBegin);
		BigDate begin=new BigDate(operationBegin.getTime());
		
		Shift nextShift=shifts.get(0);
		Shift beginShift=null;
		int index=0, i=0, n=0;
		for(Shift shift:shifts) {
			if(operationBegin.compareTo(shift.begin)>=0 && operationBegin.compareTo(shift.end)<=0) {
				beginShift=shift;
				index=i;
				break;
			}else if(operationBegin.compareTo(shift.begin)<0 && nextShift.begin.compareTo(shift.begin) > 0) {
				nextShift = shift;
				n=i;
			}
			i++;
		}
		if(beginShift == null) {
			beginShift = nextShift;
			begin = new BigDate(nextShift.begin.getTime());
			index=n;
		}
		
		BigDate end=new BigDate(begin.getTime());
		end.add(operationDuration);
		
		BigDate idleDuration=new BigDate(0);
		if(end.compareTo(beginShift.end)>0) {
			int nextIndex=(index+1)%shifts.size();
			idleDuration.add(shifts.get(nextIndex).begin);
			idleDuration.subtract(shifts.get(index).end);
			operationDuration.subtract(shifts.get(index).end);
			operationDuration.subtract(operationBegin);
			while(operationDuration.compareTo(zero)>0) {
				operationDuration.subtract(shifts.get(index).end);
				operationDuration.subtract(shifts.get(index).begin);
				
				idleDuration.add(shifts.get(nextIndex).begin);
				idleDuration.subtract(shifts.get(index).end);
				index++;
				index%=shifts.size();
				nextIndex=(index+1)%shifts.size();
			}
		}
		end.add(idleDuration);
		return new BigDate[] {begin, end};
		//return new BigDate[] {operationBegin, operationEnd};
	}

}
