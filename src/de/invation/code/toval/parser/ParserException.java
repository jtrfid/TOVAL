package de.invation.code.toval.parser;


public class ParserException extends Exception {

	private static final long serialVersionUID = -3831064725302620641L;

	private final String msg_unsupportedFormat = "Unsupported parser format";
	private final String msg_unknownFileExtension = "Unknown file extension";

	private ErrorCode errorCode = null;
	protected Object object = null;
	
	public ParserException(){
		super();
	}
	
	public ParserException(String message){
		super(message);
	}

	public ParserException(ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ParserException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ParserException(ErrorCode errorCode, Object object) {
		super();
		this.errorCode = errorCode;
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	@Override
	public String getMessage() {
		StringBuffer msg = checkErrorCode();
		if (msg != null) {
			if (object == null) {
				msg.append(".");
			} else {
				msg.append(": ").append(object.toString());
			}
		}

		String msgSuper = super.getMessage();

		if (msg == null || msg.length() == 0)
			return msgSuper;

		if (msgSuper != null)
			return msg.append("\n").append(msgSuper).toString();

		return msg.toString();
	}
	
	protected StringBuffer checkErrorCode(){
		StringBuffer buffer = new StringBuffer();
		if(errorCode == null)
			return buffer;
		
		switch (errorCode) {
		case UNSUPPORTED_FORMAT:
			buffer.append(msg_unsupportedFormat);
			break;
		case UNKNOWN_FILE_EXTENSION:
			buffer.append(msg_unknownFileExtension);
			break;
		default:
			break;
		}
		return buffer;
	}

	public enum ErrorCode {
		UNSUPPORTED_FORMAT, UNKNOWN_FILE_EXTENSION;
	}

}
