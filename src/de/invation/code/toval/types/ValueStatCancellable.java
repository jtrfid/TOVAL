package de.invation.code.toval.types;

import java.util.HashMap;

/**
 * List implementation that stores additional information about the managed data,
 * such as the minimum and maximum value and the value range.
 * All List operations are delegated to the delegate list. 
 * Updates are cancellable.
 * @param <U> Type of list elements
 * 
 * @author Thomas Stocker
 */					 
public class ValueStatCancellable<U extends Number & Comparable<? super U>> extends ValueStat<U>{
	
	private HashMap<U, Integer> valueSupport = new HashMap<U, Integer>();
	
	public ValueStatCancellable(boolean acceptNulls) {
		super(acceptNulls);
	}
	
	@Override
	public void update(U number) {
		super.update(number);
		Integer support = valueSupport.get(number);
		if(support == null){
			valueSupport.put(number, 1);
		} else {
			valueSupport.put(number, support+1);
		}
	}
	
	public int getSupportFor(Number number){
		Integer support = valueSupport.get(number);
		if(support == null)
			return 0;
		return support;
	}
	
	public void updateCancel(U number) {
		Integer support = valueSupport.get(number);
		if(support == null){
			return;
		}
		if(support == 1){
			valueSupport.remove(number);
		} else {
			valueSupport.put(number, support-1);
		}
		valueCount--;
		if(number == null)
			nullCount--;
		min = null;
		max = null;
		for(U u: valueSupport.keySet()){
			if(u!=null){
				min = min==null ? u : (u.compareTo(min)<0 ? u : min);
				max = max==null ? u : (u.compareTo(max)>0 ? u : max);
			}
		}
		setRange();
	}
	
	//TODO: remove this function
	public HashMap<U, Integer> getSupport(){
		return valueSupport;
	}
	
	@Override
	public void reset(){
		valueSupport.clear();
		super.reset();
	}


}
