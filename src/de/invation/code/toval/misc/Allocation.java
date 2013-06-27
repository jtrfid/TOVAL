package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Allocation {
	
	private HashMap<Object, Object> exclusions = new HashMap<Object, Object>();
	private HashMap<Object, ArrayList<Object>> mapping = new HashMap<Object, ArrayList<Object>>();
	private HashMap<Object, Integer> insertStat = new HashMap<Object, Integer>();
	private Object[] subjects;
	private Object[] objects;
	private int allocationCount = 1;
	private Random rand = new Random();
	
	public Allocation(Object[] subjects, Object[] objects) {
		this.subjects = subjects;
		this.objects = objects;
	}
	
	public void addExclusion(Object key, Object value) {
		exclusions.put(key, value);
	}
	
	public void setAllocationCount(int count) {
		this.allocationCount = count;
	}
	
	private int getMappingSize() {
		int result = 0;
		for(Object a: subjects)
			if(mapping.get(a) != null)
				result += mapping.get(a).size();
		return result;
	}
	
	public Map<Object, ArrayList<Object>> getMapping(){
		mapping.clear();
		insertStat.clear();
		Object nextObject;
		ArrayList<Object> possibleObjects = new ArrayList<Object>();
		while(getMappingSize()<subjects.length*allocationCount) {
			possibleObjects.clear();
			while(insertStat.containsKey((nextObject = objects[rand.nextInt(objects.length)])) && insertStat.get(nextObject)==allocationCount) {}
			for(Object a:subjects)
				if((mapping.get(a) == null || !mapping.get(a).contains(nextObject)) && (!exclusions.containsKey(a) || !exclusions.get(a).equals(nextObject)))
					possibleObjects.add(a);
			if(possibleObjects.isEmpty()) {
				removeMapping();
			} else {
				boolean inserted = false;
				for(Object nextKey: possibleObjects)
					if(!inserted) {
						ArrayList<Object> valueList = mapping.get(nextKey);
						if(valueList==null) {
							valueList = new ArrayList<Object>();
							mapping.put(nextKey, valueList);
						}
						if(valueList.size()<allocationCount) {
							mapping.get(nextKey).add(nextObject);
							if(insertStat.get(nextObject) == null)
								insertStat.put(nextObject, 0);
							insertStat.put(nextObject, insertStat.get(nextObject)+1);
							inserted = true;
						}
					}
				if(!inserted)
					removeMapping();
				
			}
		}
		return mapping;
	}
	
	private void removeMapping() {
		if(mapping.isEmpty())
			return;
		Object a2;
		while(mapping.get(a2 = subjects[rand.nextInt(subjects.length)]) == null) {}
		Object value = mapping.get(a2).get(0);
		if(mapping.get(a2).size()<=1)
			mapping.remove(a2);
		else mapping.get(a2).remove(0);
		if(insertStat.get(value)<=1)
			insertStat.remove(value);
		else insertStat.put(value, insertStat.get(value)-1);
	}

}
