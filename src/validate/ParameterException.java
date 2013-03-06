package validate;

public class ParameterException extends Exception{

	private static final long serialVersionUID = 3432333417522563960L;
	
	private ErrorCode errorCode = null;
	private final String msg_NullPointer = "Parameter is null";
	private final String msg_RangeViolation = "Parameter out of range";
	private final String msg_Negative = "Parameter is negative";
	private final String msg_Empty = "Parameter contains no elements";
	private final String msg_NullElements = "Parameter contains null elements";
	
	private boolean usePredefinedMessages = true;
	
	public ParameterException(ErrorCode errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public ParameterException(ErrorCode errorCode, String message){
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
			case EMPTY: return msg_Empty;
			case NULLELEMENTS: return msg_NullElements;
			case INCONSISTENCY: return super.getMessage();
			case INCOMPATIBILITY: return super.getMessage();
			case CONSTRAINT: return super.getMessage();
		}
		return null;
	}
	
	public ErrorCode getErrorCode(){
		return errorCode;
	}
	
	public enum ErrorCode { 
		NULLPOINTER, RANGEVIOLATION, NEGATIVE, EMPTY, NULLELEMENTS, INCONSISTENCY, TYPE, INCOMPATIBILITY, CONSTRAINT;
	}

}
