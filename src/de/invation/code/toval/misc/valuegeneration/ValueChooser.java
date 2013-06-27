package de.invation.code.toval.misc.valuegeneration;

import java.util.List;

import validate.ParameterException;

public interface ValueChooser<E extends Object> {
	
	public E chooseValue(List<E> candidates) throws ParameterException;
	
	public boolean isValid();

}