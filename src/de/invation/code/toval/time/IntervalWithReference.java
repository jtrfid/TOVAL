package de.invation.code.toval.time;

public class IntervalWithReference extends Interval {
	
	private Object reference = null;

	public IntervalWithReference(long startTime, long endTime, Object reference) {
		super(startTime, endTime);
		this.reference = reference;
	}

	public Object getReference(){
		return reference;
	}
	
}
