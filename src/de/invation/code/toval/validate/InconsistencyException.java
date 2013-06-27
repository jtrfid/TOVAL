package de.invation.code.toval.validate;

public class InconsistencyException extends ParameterException{

	private static final long serialVersionUID = -6702218050990120801L;

	public InconsistencyException(String message) {
		super(ErrorCode.INCONSISTENCY, message);
	}

}
