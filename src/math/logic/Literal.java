package math.logic;

import java.util.Comparator;

public class Literal implements Comparator<Literal>{
	
	private Object item = null;
	private boolean negated = false;
	
	public Literal(Object item){
		this.item = item;
	}
	
	public Literal(Object item, boolean positive){
		this(item);
		this.negated = !positive;
	}
	
	public boolean isNegated(){
		return negated;
	}
	
	public Object getItem(){
		return item;
	}
	
	public Literal getInverse(){
		try{
			return new Literal(item, negated);
		}catch(Exception e){
			return null;
		}
	}
	
	public Literal clone(){
		try{
			return new Literal(item, negated);
		}catch(Exception e){
			return null;
		}
	}
	
	public String toString(){
		if(negated){
			return "\u00AC"+item.toString();
		}
		return item.toString();
	}

	public int compare(Literal o1, Literal o2) {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != getClass())
			return false;
			   
		if (negated != ((Literal) o).isNegated())
			return false;
		if(!item.equals(((Literal) o).getItem()))
			return false;
		
		return true;

	}
	
}
