package constraint;

import validate.ParameterException;
import validate.Validate;
import validate.ParameterException.ErrorCode;

public enum StringOperator implements Operator<String> {
	
	EQUAL(OperatorFormats.EQUAL, String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.EQUAL)), 
	NOT_EQUAL(OperatorFormats.NOT_EQUAL, String.format(OperatorFormats.COMPARISON_FORMAT, OperatorFormats.NOT_EQUAL));
	
	private String[] argumentNames = new String[] {"Attribute", "Comparator"};
	private String sign = null;
	private String toStringFormat = null;
	
	private StringOperator(String sign, String toStringFormat){
		this.sign = sign;
		this.toStringFormat = toStringFormat;
	}
	
	private StringOperator(String[] arguments, String sign, String toStringFormat){
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
		if(!String.class.isAssignableFrom(value.getClass()))
			throw new ParameterException(ErrorCode.TYPE, "Wrong type of validation value, expected type: " + String.class);
	}
	
	@Override
	public boolean validate(Object value, String... parameters) throws ParameterException {
		Validate.notNull(value);
		validateValueType(value);
		Validate.notNull(parameters);
		Validate.noNullElements(parameters);
		Validate.notTrue(parameters.length + 1 != getRequiredArguments());
		
		switch(this){
		case EQUAL:			return value.equals(parameters[0]);
		case NOT_EQUAL:		return !value.equals(parameters[0]);
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
	
}
