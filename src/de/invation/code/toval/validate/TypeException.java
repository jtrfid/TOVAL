package de.invation.code.toval.validate;

public class TypeException extends ParameterException {

	private static final long serialVersionUID = 1893571005305868258L;
	private static final String msg_format = "Parameter is not of type %s";

	public TypeException(String requiredType) {
		super(ErrorCode.TYPE, String.format(msg_format, requiredType));
	}

}
