package de.invation.code.toval.constraint;

import de.invation.code.toval.validate.ParameterException;


public interface Operator<O extends Object> {
	
	public String getStringFormat();
	
	public int getRequiredArguments();
	
	public String[] getArgumentNames();
	
	public boolean validate(Object value, O... parameters) throws ParameterException;
	
}
