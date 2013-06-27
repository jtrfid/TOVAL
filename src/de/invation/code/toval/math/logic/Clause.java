package de.invation.code.toval.math.logic;


import java.util.Comparator;
import java.util.Iterator;

import de.invation.code.toval.types.HashList;



public class Clause extends HashList<Literal> implements Comparator<Clause>{
	
	private static final long serialVersionUID = 1L;
	
	public Clause(){}
	
	public Clause(Literal l){
		add(l);
	}
	
	public Literal getFirst(){
		if(!isEmpty()){
			return super.get(0);
		} else {
			return null;
		}
	}
	
	public HashList<Object> getItems(){
		HashList<Object> set = new HashList<Object>();
		for(Iterator<Literal> iter=iterator(); iter.hasNext();)
			set.add(iter.next().getItem());
		return set;
	}
	
	public Clause union(Clause k){
		Clause nk = new Clause();
		nk.addAll(k);
		nk.addAll(this);
		return nk;
	}
	
	public boolean containsItem(Object item){
		for(Iterator<Literal> iter=iterator(); iter.hasNext();)
			if(item.equals(iter.next().getItem()))
				return true;
		return false;
	}
	
	/**
	 * Tries to accomplish a resolution along the variable {@link item}.
	 * @param clause
	 * @param item
	 * @return
	 */
	public ResolveResult resolve(Clause clause, Object item){
		Literal lPos = new Literal(item, true);
		Literal lNeg = new Literal(item, false);
		if( (contains(lPos) && clause.contains(lNeg)) || (contains(lNeg) && clause.contains(lPos)) ){
			Clause resolvent = union(clause);
			resolvent.remove(lPos);
			resolvent.remove(lNeg);
			return new ResolveResult(resolvent, item);
		}
		return null;
	}
	
	public boolean hasTwinLiterals(){
		for(int i=0; i<size()-1; i++)
			for(int j=i; j<size(); j++)
				if(get(i).getInverse().equals(get(j)))
					return true;
		return false;
	}

	public int compare(Clause o1, Clause o2) {
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
		if (!((Clause) o).containsAll(this))
			return false;
		if(!containsAll((Clause) o))
			return false;
		
		return true;
	}
	
	public String toString(){
		if(isEmpty()){
			return "¿";
		} else {
			String s = "{";
			for(Iterator<Literal> i = iterator(); i.hasNext();){
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
