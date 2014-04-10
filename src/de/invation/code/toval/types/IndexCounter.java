package de.invation.code.toval.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IndexCounter<T> implements Iterator<Map<T, Integer>> {
	
	private Map<T, Integer> currentIndexes = new HashMap<T, Integer>();
	private Map<T, Integer> maxIndexes = new HashMap<T, Integer>();
	private List<T> order = new ArrayList<T>();
	private boolean acceptsNewIndexes = true;
	private int combinations = 1;
	private int currentCombination = 1;

	@Override
	public boolean hasNext() {
		if(order.isEmpty())
			return false;
		for(int i=0; i<order.size(); i++){
			if(currentIndexes.get(order.get(i)) < maxIndexes.get(order.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public int getCombinationNumber(){
		return currentCombination;
	}
	
	public int getCombinations(){
		return combinations;
	}

	@Override
	public Map<T, Integer> next() {
		if(order.isEmpty() || !hasNext())
			return null;
		for(int j=order.size()-1; j>=0; j--){
			T cEntry = order.get(j);
			if(currentIndexes.get(cEntry) < maxIndexes.get(cEntry)){
				//Index for list j can be incremented
				currentIndexes.put(cEntry, currentIndexes.get(cEntry)+1);
				if(j<order.size()-1){
					//Reset all following indexes to 0
					for(int k=j+1; k<order.size(); k++){
						currentIndexes.put(order.get(k), 0);
					}
				}
				currentCombination++;
				return currentIndexes;
			}
		}
		return null;
	}
	
	public synchronized void addNewIndex(T key, int maxIndex){ 
		if(acceptsNewIndexes){
			currentIndexes.put(key, -1);
			if(!order.isEmpty()){
				currentIndexes.put(order.get(order.size()-1), 0);
			}
			maxIndexes.put(key, maxIndex);
			order.add(key);
			calculateCombinations();
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	private void calculateCombinations(){
		combinations = 1;
		for(T key: maxIndexes.keySet()){
			combinations *= maxIndexes.get(key);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
