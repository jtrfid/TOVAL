package de.invation.code.toval.constraint;

import validate.ParameterException;
import validate.ParameterException.ErrorCode;
import validate.Validate;


public enum NumberOperator implements Operator<Number>{
	
	SMALLER(		OperatorFormats.SMALLER, 		String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.SMALLER)), 
	SMALLER_EQUAL(	OperatorFormats.SMALLER_EQUAL, 	String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.SMALLER_EQUAL)), 
	EQUAL(			OperatorFormats.EQUAL, 			String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.EQUAL)), 
	NOT_EQUAL(		OperatorFormats.NOT_EQUAL, 		String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.NOT_EQUAL)),
	LARGER(			OperatorFormats.LARGER, 		String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.LARGER)), 
	LARGER_EQUAL(	OperatorFormats.LARGER_EQUAL, 	String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.LARGER_EQUAL)),
	IN_INTERVAL(	new String[] {"Attribute", "Interval start", "Interval end"}, OperatorFormats.IN_INTERVAL, 		OperatorFormats.IN_INTERVAL_FORMAT), 
	NOT_IN_INTERVAL(new String[] {"Attribute", "Interval start", "Interval end"}, OperatorFormats.NOT_IN_INTERVAL, 	OperatorFormats.NOT_IN_INTERVAL_FORMAT);
	
	private String[] argumentNames = new String[] {"Attribute", "Comparator"};
	private String sign = null;
	private String toStringFormat = null;
	
	private NumberOperator(String sign, String toStringFormat){
		this.sign = sign;
		this.toStringFormat = toStringFormat;
	}
	
	private NumberOperator(String[] arguments, String sign, String toStringFormat){
		this(sign, toStringFormat);
		this.argumentNames = arguments;
	}
	
	@Override
	public int getRequiredArguments(){
		return argumentNames.length;
	}
	
	@Override
	public String[] getArgumentNames(){
		return argumentNames;
	}
	
	protected void validateValueType(Object value) throws ParameterException{
		if(!Number.class.isAssignableFrom(value.getClass()))
			throw new ParameterException(ErrorCode.TYPE, "Wrong type of validation value, expected type: " + Number.class);
	}
	
	@Override
	public boolean validate(Object value, Number... parameters) throws ParameterException {
		Validate.notNull(value);
		validateValueType(value);
		Validate.notNull(parameters);
		Validate.noNullElements(parameters);
		Validate.notTrue(parameters.length + 1 != getRequiredArguments());
		
		double numberValue = ((Number) value).doubleValue();
		
		switch(this){
		case SMALLER: 		return numberValue < parameters[0].doubleValue();
		case SMALLER_EQUAL: return numberValue <= parameters[0].doubleValue();
		case EQUAL:			return numberValue == parameters[0].doubleValue();
		case NOT_EQUAL:		return numberValue != parameters[0].doubleValue();
		case LARGER:		return numberValue > parameters[0].doubleValue();
		case LARGER_EQUAL:	return numberValue >= parameters[0].doubleValue();
		case IN_INTERVAL:	return numberValue >= parameters[0].doubleValue() && numberValue <= parameters[1].doubleValue();
		case NOT_IN_INTERVAL: return numberValue < parameters[0].doubleValue() || numberValue > parameters[1].doubleValue();
		default: return false;
		}
	}

	
	@Override
	public String getStringFormat(){
		return toStringFormat;
	}
	
	@Override
	public String toString(){
		return sign;
	}
	
	public static NumberOperator parse(String operatorString){
		if(operatorString.equals("<"))
			return NumberOperator.SMALLER;
		if(operatorString.equals("<="))
			return NumberOperator.SMALLER_EQUAL;
		if(operatorString.equals(">"))
			return NumberOperator.LARGER;
		if(operatorString.equals(">="))
			return NumberOperator.LARGER_EQUAL;
		if(operatorString.equals("=="))
			return NumberOperator.EQUAL;
		if(operatorString.equals("!="))
			return NumberOperator.NOT_EQUAL;
		return null;
	}

}
