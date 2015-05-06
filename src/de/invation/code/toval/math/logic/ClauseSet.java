package de.invation.code.toval.math.logic;


import java.util.Comparator;
import java.util.Iterator;

import de.invation.code.toval.types.HashList;





public class ClauseSet extends HashList<Clause> implements Comparator<ClauseSet>{
	
	private static final long serialVersionUID = 1L;
	
	public ClauseSet(){}
	
	public ClauseSet(Clause k){
		add(k);
	}
	
	public HashList<Object> getItems(){
		HashList<Object> set = new HashList<Object>();
		for(Iterator<Clause> iter=iterator(); iter.hasNext();)
			set.addAll(iter.next().getItems());
		return set;
	}
	
	public ClauseSet union(ClauseSet k){
		ClauseSet r = new ClauseSet();
		r.addAll(this);
		r.addAll(k);
		return r;
	}

	public int compare(ClauseSet o1, ClauseSet o2) {
		return o1.size()-o2.size();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != getClass())
			return false;
		if (!((ClauseSet) o).containsAll(this))
			return false;
		if(!containsAll((ClauseSet) o))
			return false;
		
		return true;
	}
	
	public String toString(){
		if(isEmpty()){
			return "{}";
		} else {
			String s = "{";
			for(Iterator<Clause> i = iterator(); i.hasNext();){
				s += i.next().toString();
				if(i.hasNext()){
					s += ",";
				}
			}
			s += "}";
			return s;
		}
	}
	
}
