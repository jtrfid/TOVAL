package de.invation.code.toval.math.logic;


/**
 * This class represents the result of a resolution operation.
 * 
 * @author stocker
 *
 */
public class ResolveResult {
	private Clause resolvent = new Clause();
	private Object item =  null;
	
	
	public ResolveResult(Clause resolvent, Object item){
		this.resolvent = resolvent;
		this.item = item;
	}
	
	public Object getItem(){
		return item;
	}
	
	public Clause getClause(){
		return this.resolvent;
	}
	
	public boolean isFinal(){
		return resolvent.isEmpty();
	}
	
	public String toString(){
		return "ResolveResult("+item.toString()+", "+resolvent.toString()+")";
	}

}
