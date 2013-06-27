package de.invation.code.toval.time;


public class IntervalOverlapWithReference<T extends Object> extends AbstractIntervalOverlap<IntervalWithReference>{

	private Object newReference = null;
	
	public IntervalOverlapWithReference() {
		super();
	}

	@Override
	public void reportTimeInterval(long start, long end) {
		reportTimeInterval(start, end, null);
	}
	
	public void reportTimeInterval(long start, long end, T reference){
		newReference = reference;
		super.reportTimeInterval(start, end);
	}

	@Override
	protected IntervalWithReference getNewInterval(long start, long end) {
		return new IntervalWithReference(start, end, newReference);
	}
	
	@Override
	protected void notifyListeners(OverlapIntervalList<IntervalWithReference> intervalList){
		OverlapEvent<IntervalWithReference> overlapEvent = new OverlapEvent<IntervalWithReference>(intervalList.getIntervals());
		for(OverlapListener<IntervalWithReference> listener: overlapListeners){
			listener.overlapDetected(overlapEvent);
		}
	}
	
}
