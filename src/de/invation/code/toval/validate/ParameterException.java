package de.invation.code.toval.validate;

public class ParameterException extends IllegalArgumentException {

	private static final long serialVersionUID = 3432333417522563960L;
	
	private ErrorCode errorCode = null;
	private final String msg_NullPointer = "Parameter is null";
	private final String msg_RangeViolation = "Parameter out of range";
	private final String msg_Negative = "Parameter is negative";
	private final String msg_NotNegative = "Parameter is 0 or positive";
	private final String msg_Positive = "Parameter is positive";
	private final String msg_NotPositive = "Parameter 0 or negative";
	private final String msg_Empty = "Parameter is empty";
	private final String msg_NullElements = "Parameter contains null elements";
	private final String msg_Constraint = "Parameter does not fulfill constraint";
	
	private boolean usePredefinedMessages = true;
	
	public ParameterException(ErrorCode errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public ParameterException(ErrorCode errorCode, String message){
		super(message);
		usePredefinedMessages = false;
	}
	
	public ParameterException(String message){
		super(message);
		usePredefinedMessages = false;
	}
	
	@Override
	public String getMessage(){
		if(!usePredefinedMessages){
			return super.getMessage();
		}
		switch(errorCode){
			case NULLPOINTER: return msg_NullPointer;
			case RANGEVIOLATION: return msg_RangeViolation;
			case NEGATIVE: return msg_Negative;
			case NOTNEGATIVE: return msg_NotNegative;
			case POSITIVE: return msg_Positive;
			case NOTPOSITIVE: return msg_NotPositive;
			case EMPTY: return msg_Empty;
			case NULLELEMENTS: return msg_NullElements;
			case INCONSISTENCY: return super.getMessage();
			case INCOMPATIBILITY: return super.getMessage();
			case MEMORY: return super.getMessage();
			case TYPE: return super.getMessage();
			case CONSTRAINT: return msg_Constraint;
		}
		return null;
	}
	
	public ErrorCode getErrorCode(){
		return errorCode;
	}
	
	public enum ErrorCode { 
		NULLPOINTER, RANGEVIOLATION, NEGATIVE, NOTNEGATIVE, POSITIVE, NOTPOSITIVE, EMPTY, NULLELEMENTS, INCONSISTENCY, TYPE, INCOMPATIBILITY, CONSTRAINT, MEMORY;
	}

}
