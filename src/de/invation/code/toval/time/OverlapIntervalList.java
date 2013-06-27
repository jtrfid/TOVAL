package de.invation.code.toval.time;


/**
 * In contrast to conventional interval lists, this type of interval list
 * is tailored for coexistence analysis of process traces where the start and end 
 * times of traces are specified as time intervals.<br>
 * The algorithm that determines coexisting process instances on the basis of such
 * interval definitions maintains a basic time interval per interval group.
 * The basic interval is the first interval added to the interval group
 * and serves as a basis for the determination of compatible intervals
 * that in the end can be considered as time bounds for coexisting process traces.
 * 
 * @author Thomas Stocker
 */
public class OverlapIntervalList<T extends Interval> extends AbstractIntervalList<T> {
	
	public OverlapIntervalList(){
		super();
	}
	
	/**
	 * Generates a new interval list, using the given time interval as basic interval.
	 * @param basicTimeInterval The interval to be used as basic interval.
	 */
	public OverlapIntervalList(T basicTimeInterval){
		super(basicTimeInterval);
	}
	
	/**
	 * Returns the basic interval of the interval list.<br>
	 * This is always the first added interval.
	 * @return The basic interval or <code>null</code> if the list does not contain any intervals yet.
	 */
	public T getBasicInterval(){
		if(timeIntervals.isEmpty())
			return null;
		return timeIntervals.get(0);
	}

	/**
	 * Checks if the given time interval is compatible with the interval list.<br>
	 * A time interval is considered compatible if it starts within the basic interval.<br>
	 * If the basic interval starts with a and ends with b, the start time of the given interval has to lie in [a;b).
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval is compatible with the interval list;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean isCompatible(Interval interval){
		return interval.getStart() >= getBasicInterval().getStart() &&
			   interval.getStart() < getBasicInterval().getEnd();
	}
	
	/**
	 * Checks if the given interval belongs to the coexistence group which consists of all list intervals.<br>
	 * This is only the case when it starts within the minimum borders.<br>
	 * If the minimum borders are a and b, the start time of the given interval has to lie in [a;b).
	 * @param interval The interval to check.
	 * @return <code>true</code> if the given interval belongs to the same coexistence group;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean belongsToCoexistenceGroup(Interval interval){
		return interval.getStart() >= minBorders.getStart() &&
			   interval.getStart() < minBorders.getEnd();
	}
	
	/**
	 * Creates a new interval list out of this list according to the given interval.<br>
	 * The new list will only contain those intervals of this list that end after the given interval starts.
	 * @param interval The interval that is used for generating a refined version of this list.
	 * @return The refined version of this list according to the given interval.
	 */
	public OverlapIntervalList<T> refineBy(T interval){
		OverlapIntervalList<T> newList = new OverlapIntervalList<T>();
		for(T inter: timeIntervals){
			if(inter.getEnd() > interval.getStart()){
				newList.addTimeInterval(inter);
			}
		}
		newList.addTimeInterval(interval);
		return newList;
	}
	
}
