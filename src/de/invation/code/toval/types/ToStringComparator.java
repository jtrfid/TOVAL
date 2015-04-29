package de.invation.code.toval.types;

import java.util.Comparator;

public class ToStringComparator<T> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		if(o1 == null && o2 != null)
			return -1;
		else if(o2 == null && o1 != null)
			return 1;
		else if(o1 == null && o2 == null)
			return 0;
		else
			return o1.toString().compareTo(o2.toString());
	}

}
