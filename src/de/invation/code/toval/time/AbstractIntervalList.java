package de.invation.code.toval.time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractIntervalList<T extends Interval> {
	protected List<T> timeIntervals = new ArrayList<>();
	protected Interval minBorders = new Interval(Long.MIN_VALUE, Long.MAX_VALUE);
	protected Interval maxBorders = null;
	
	public AbstractIntervalList(){}
	
	public AbstractIntervalList(T initialTimeInterval){
		timeIntervals.add(initialTimeInterval);
	}
	
	/**
	 * Returns the size of the interval list, i.e. the number of intervals.
	 * @return The number of intervals in the list.
	 */
	public int size(){
		return timeIntervals.size();
	}
	
	public boolean isEmpty(){
		return timeIntervals.isEmpty();
	}
	
	public List<T> getIntervals(){
		return Collections.unmodifiableList(timeIntervals);
	}
	
	/**
	 * Adds a new time interval to the list of time intervals
	 * and adjusts the minimum and maximum borders.
	 * @param interval The interval to add.
	 */
	public void addTimeInterval(T interval){
		timeIntervals.add(interval);
		minBorders.setStart(Math.max(minBorders.getStart(), interval.getStart()));
		minBorders.setEnd(Math.min(minBorders.getEnd(), interval.getEnd()));
		if(maxBorders == null){
			maxBorders = interval.clone();
		} else {
			maxBorders.setStart(Math.min(maxBorders.getStart(), interval.getStart()));
			maxBorders.setEnd(Math.max(maxBorders.getEnd(), interval.getEnd()));
		}
	}
	
	/**
	 * Checks if the given interval starts within the minimum borders.<br>
	 * If the minimum borders are a and b (a<b), then the start of the given interval has to lie in [a;b].
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval starts within the minimum borders;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean startsWithinMinBorders(Interval interval){
		return interval.getStart() >= minBorders.getStart() &&
			   interval.getStart() <= minBorders.getEnd();
	}
	
	/**
	 * Checks if the given interval starts within the maximum borders.<br>
	 * If the maximum borders are a and b (a<b), then the start of the given interval has to lie in [a;b].
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval starts within the maximum borders;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean startsWithinMaxBorders(Interval interval){
		return interval.getStart() >= maxBorders.getStart() &&
			   interval.getStart() <= maxBorders.getEnd();
	}
	
	/**
	 * Checks if the given interval lies within the minimum borders.<br>
	 * If the minimum borders are a and b (a<b), then the given interval has to lie in [a;b].
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval lies within the minimum borders;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean liesWithinMinBorders(Interval interval){
		return interval.getStart() >= minBorders.getStart() &&
			   interval.getEnd() <= minBorders.getEnd();
	}

	/**
	 * Checks if the given interval lies within the maximum borders.<br>
	 * If the maximum borders are a and b (a<b), then the given interval has to lie in [a;b].
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval lies within the maximum borders;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean liesWithinMaxBorders(Interval interval){
		return interval.getStart() >= maxBorders.getStart() &&
			   interval.getEnd() <= maxBorders.getEnd();
	}
	
	@Override
	public String toString(){
		return timeIntervals.toString();
	}
}