package time;

import java.util.List;

public class OverlapEvent<T extends Interval> {

	private Object reference = null;
	private List<T> timeIntervals = null;

	public OverlapEvent(Object reference){
		this.reference = reference;
	}
	
	public OverlapEvent(List<T> timeIntervals){
		this.timeIntervals = timeIntervals;
	}

	public OverlapEvent(Object reference, List<T> timeIntervals) {
		this.reference = reference;
		this.timeIntervals = timeIntervals;
	}

	public Object getReference() {
		return reference;
	}

	public List<T> getTimeIntervals() {
		return timeIntervals;
	}
	
}
