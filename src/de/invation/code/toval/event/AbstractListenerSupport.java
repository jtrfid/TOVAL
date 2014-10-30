package de.invation.code.toval.event;

import java.io.Serializable;
import java.util.HashSet;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractListenerSupport<L extends Object> implements Serializable {

	private static final long serialVersionUID = -7522608459092207941L;
	
	protected HashSet<L> listeners = new HashSet<L>();
	
	public boolean addListener(L l) {
		Validate.notNull(l);
		return listeners.add(l);
	}
	
	public boolean removeListener(L l) {
		Validate.notNull(l);
		return listeners.remove(l);
	}

}
