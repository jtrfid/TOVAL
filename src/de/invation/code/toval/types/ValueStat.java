package de.invation.code.toval.types;

import java.util.Collection;

/**
 * List implementation that stores additional information about the managed data,
 * such as the minimum and maximum value and the value range.
 * All List operations are delegated to the delegate list. 
 * @param <U> Type of list elements
 * 
 * @author Thomas Stocker
 */					 
public class ValueStat<U extends Number & Comparable<? super U>> {

	protected U min = null;
	protected U max = null;
	protected Double range = null;
	protected int valueCount = 0;
	protected final String toStringFormat = "[values: %s; min: %s; max: %s; range: %s; valid: %s]";
	protected boolean acceptNulls = true;

	public ValueStat(boolean acceptNulls){
		this.acceptNulls = acceptNulls;
	}
	
	public ValueStat(Collection<U> values, boolean acceptNulls) {
		this(acceptNulls);
		if(!acceptNulls && values.contains(null))
			throw new NullPointerException();
		for(U u: values)
			update(u);
	}
	
	public U min() {
		return min;
	}

	public U max() {
		return max;
	}

	public Double range() {
		return range;
	}
	
	public int valueCount() {
		return valueCount;
	}
	
	public void update(U number) {
		if(number == null)
			if(acceptNulls==true) {
				valueCount++;
				return;
			} else throw new NullPointerException();
	
		min = min==null ? number : (number.compareTo(min)<0 ? number : min);
		max = max==null ? number : (number.compareTo(max)>0 ? number : max);
		setRange();
		valueCount++;
	}
	
	public void reset() {
		reset(null, null, 0);
	}
	
	public boolean isValid(){
		return min != null;
	}
	
	protected void reset(U min, U max, int valueCount) {
		this.min = min;
		this.max = max;
		setRange();
		this.valueCount = valueCount;
	}
	
	protected void setRange() {
		if(max == null || min == null) {
			range = null;
			return;
		}
		range = max.doubleValue()-Math.signum(min.doubleValue())*Math.abs(min.doubleValue());
	}
	
	public String toString() {
		return String.format(toStringFormat, valueCount, min, max, range, isValid());
	}


}
