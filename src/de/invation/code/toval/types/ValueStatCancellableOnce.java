package de.invation.code.toval.types;

/**
 * List implementation that stores additional information about the managed data,
 * such as the minimum and maximum value and the value range.
 * All List operations are delegated to the delegate list. 
 * @param <U> Type of list elements
 * 
 * @author Thomas Stocker
 */					 
public class ValueStatCancellableOnce<U extends Number & Comparable<? super U>> extends ValueStat<U>{

	private U lastMin = null;
	private U lastMax = null;
	private boolean updateCancelled;
	
	public ValueStatCancellableOnce(boolean acceptNulls) {
		super(acceptNulls);
	}
	
	@Override
	public void update(U number) {
		if(number == null) {
			if(acceptNulls)
				valueCount++;
			return;
		}
		lastMin = min;
		lastMax = max;
		super.update(number);
		updateCancelled = false;
	}
	
	public void updateCancel(U number) {
		if(number == null){
			reset(min, lastMax, valueCount, nullCount-1);
			return;
		}
		if(updateCancelled)
			throw new UnsupportedOperationException();
		int compMin = (int) Math.signum(number.compareTo(min));
		int compMax = (int) Math.signum(number.compareTo(max));

		if(compMax==0 && compMin==1) {
			reset(min, lastMax, valueCount-1, nullCount);
			updateCancelled = true;
			return;
		}
		if (compMax==-1 && compMin==0) {
			reset(lastMin, max, valueCount-1, nullCount);
			updateCancelled = true;
			return;
		}
		if (compMax==0 && compMin==0) {
			reset(lastMin, lastMax, valueCount-1, nullCount);
			updateCancelled = true;
			return;
		}
	}
	
	


}
