package properties;


public class PropertyException extends Exception {
	
	private final String messageFormat = "Invalid property [%s]: %s";
	private final String extendedMessageFormat = "Invalid property [%s]: %s, %s";
	private String property = null;
	private String value = null;
	private String notification = null;

	public PropertyException(Object property, Object value) {
		super();
		this.property = property.toString();
		this.value = value.toString();
	}
	
	public PropertyException(Object property, Object value, String notification) {
		this(property, value);
		this.notification = notification;
	}
	
	@Override
	public String getMessage(){
		if(notification == null){
			return String.format(messageFormat, property, value);
		} else {
			return String.format(extendedMessageFormat, property, value, notification);
		}
	}

}
