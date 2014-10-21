package de.invation.code.toval.event;

import java.io.Serializable;
import java.util.HashSet;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractListenerSupport<L extends Object> implements Serializable {

	protected HashSet<L> listeners = new HashSet<L>();
	
	public void addListener(L l) {
		Validate.notNull(l);
		listeners.add(l);
	}
	
	public void removeListener(L l) {
		Validate.notNull(l);
		listeners.remove(l);
	}

}
