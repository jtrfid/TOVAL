package de.invation.code.toval.validate;

public class CompatibilityException extends ParameterException{

	private static final long serialVersionUID = -6702218050990120801L;

	public CompatibilityException(String message) {
		super(ErrorCode.INCONSISTENCY, message);
	}

	public CompatibilityException(String message, Throwable t) {
		super(ErrorCode.INCONSISTENCY, message, t);
	}

}
