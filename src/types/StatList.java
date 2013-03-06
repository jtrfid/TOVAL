package types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.MathUtils;

/**
 * List implementation that stores additional information about the managed data,
 * such as the minimum and maximum value and the value range.
 * All List operations are delegated to the delegate list. 
 * @param <U> Type of list elements
 * 
 * @author Thomas Stocker
 */					 
@SuppressWarnings("serial")
public class StatList<U extends Number & Comparable<? super U>> extends ArrayList<U>{

	private U min = null;
	private U max = null;
	private Double range = null;
	private Integer maxRHD = null;
	private boolean acceptNulls = false;
	
	public StatList(boolean acceptNulls) {
		super();
		this.acceptNulls = acceptNulls;
	}
	
	public StatList(int initialCapacity, boolean acceptNulls) {
		super(initialCapacity);
		this.acceptNulls = acceptNulls;
	}

	public StatList(List<U> l, boolean acceptNulls) {
		this.acceptNulls = acceptNulls;
		addAll(l);
		updateStats();
	}

	public U min() {
		return min;
	}

	public U max() {
		return max;
	}
	
	public Integer maxRHD() {
		return maxRHD;
	}

	public Double range() {
		return range;
	}
	
	private void CheckInsert(U number, boolean updateRange) {
		if(number == null)
			return;
		min = min==null ? number : (number.compareTo(min)<0 ? number : min);
		max = max==null ? number : (number.compareTo(max)>0 ? number : max);
		int rhd = MathUtils.getRHD(number);
		maxRHD = maxRHD==null ? rhd : (rhd>maxRHD ? rhd : maxRHD);	
		if(updateRange)
			range = max.doubleValue()-Math.signum(min.doubleValue())*Math.abs(min.doubleValue());
	}
	
	private void CheckRemoval(U number) {
		if(number == null)
			return;
		if(number.compareTo(min)==0 || number.compareTo(max)==0 || MathUtils.getRHD(number)==maxRHD)
			updateStats();
	}
	
	private void updateStats() {
		maxRHD = MathUtils.getRHD(get(0));
		min = null;
		max = null;
		for(U u: this)
			CheckInsert(u, false);
		if(min!=null && max !=null)
			range = max.doubleValue()-Math.signum(min.doubleValue())*Math.abs(min.doubleValue());
	}

	@Override
	public boolean add(U element) {
		if(element==null && acceptNulls==false)
			throw new IllegalArgumentException("null values are not accepted");
		boolean result = super.add(element);
		CheckInsert(element, true);
		return result;
	}
	
	@Override
	public U set(int index, U element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(int index, U element) {
		if(element==null && acceptNulls==false)
			throw new IllegalArgumentException("null values are not accepted");
		super.add(index, element);
		CheckInsert(element, true);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends U> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends U> c) {
		if(c.contains(null) && acceptNulls==false)
			throw new IllegalArgumentException("null values are not accepted");
		boolean result = super.addAll(c);
		if(result)
			updateStats();
		return result;
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public U remove(int index) {
		U result = super.remove(index);
		CheckRemoval(result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		if(result)
			CheckRemoval((U) o);
		return result;
	}
	
	@Override
	public void clear() {
		super.clear();
		min = null;
		max = null;
		range = null;
		maxRHD = null;
	}


}
