package time;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This class can be used to determine all different overlapping groups for a set of time intervals.<br>
 * An interval with borders a,b with a<b is said to overlap another interval with borders c,d with c<d, iff<br>
 * c lies within [a;b).
 * The algorithm works in an online fashion and reports detected overlap groups to interested listeners 
 * (registration via {@link AbstractIntervalOverlap#registerListener(OverlapListener)} is required) as they occur.<br>
 * Please use the method {@link AbstractIntervalOverlap#reportTimeInterval(long, long)} to report new intervals.
 * 
 * @author Thomas Stocker
 */
public abstract class AbstractIntervalOverlap<T extends Interval> {
	
	protected List<OverlapIntervalList<T>> intervalLists = new CopyOnWriteArrayList<OverlapIntervalList<T>>();
	protected Set<OverlapListener<T>> overlapListeners = new HashSet<OverlapListener<T>>();
	
	public void registerListener(OverlapListener<T> listener){
		this.overlapListeners.add(listener);
	}
	
	public void reportTimeInterval(long start, long end){
		reportTimeInterval(getNewInterval(start, end));
	}
	
	protected void reportTimeInterval(T interval){
		boolean insertGranted = true;
		for(Iterator<OverlapIntervalList<T>> iterator=intervalLists.iterator(); iterator.hasNext();){
			OverlapIntervalList<T> nextList = iterator.next();
			if(nextList.isCompatible(interval)){
				if(!nextList.belongsToCoexistenceGroup(interval)){
					removeIntervalList(nextList);
					notifyListeners(nextList);
					addIntervalList(nextList.refineBy(interval));
				} else {
					//Add the interval to the interval list
					nextList.addTimeInterval(interval);
				}
				insertGranted = false;
			} else {
				//TimeInterval list has to be closed
				removeIntervalList(nextList);
				notifyListeners(nextList);
			}
		}
		if(insertGranted){
			addIntervalList(new OverlapIntervalList<T>(interval));
		}
	}
	
	protected abstract T getNewInterval(long start, long end);
	
	protected void addIntervalList(OverlapIntervalList<T> intervalList){
		intervalLists.add(intervalList);
	}
	
	protected void removeIntervalList(OverlapIntervalList<T> intervalList){
		intervalLists.remove(intervalList);
	}
	
	protected void notifyListeners(OverlapIntervalList<T> intervalList){
		OverlapEvent<T> overlapEvent = new OverlapEvent<T>(intervalList.getIntervals());
		for(OverlapListener<T> listener: overlapListeners){
			listener.overlapDetected(overlapEvent);
		}
	}
	
	public void closeTimeIntervalReporting(){
		for(OverlapIntervalList<T> remainingList: intervalLists){
			if(remainingList.size() > 1){
				notifyListeners(remainingList);
			}
		}
	}

}
