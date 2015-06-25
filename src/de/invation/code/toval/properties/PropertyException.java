package de.invation.code.toval.properties;

public class PropertyException extends Exception {

    private static final long serialVersionUID = 1555494643930159891L;

    private final String messageFormat = "Invalid property [%s]: %s";
    private final String extendedMessageFormat = "Invalid property [%s]: %s, %s";
    private String property = null;
    private String value = null;
    private String notification = null;

    public PropertyException(Object property, Object value) {
        super();
        this.property = property.toString();
        if (value != null) {
            this.value = value.toString();
        }
    }
    
    public PropertyException(Object property, Object value, Throwable cause) {
        super(cause);
        this.property = property.toString();
        if (value != null) {
            this.value = value.toString();
        }
    }
    
    public PropertyException(Object property, Object value, String notification, Throwable cause) {
        this(property, value, cause);
        this.notification = notification;
    }

    public PropertyException(Object property, Object value, String notification) {
        this(property, value);
        this.notification = notification;
    }

    @Override
    public String getMessage() {
        if (notification == null) {
            return String.format(messageFormat, property, value);
        } else {
            return String.format(extendedMessageFormat, property, value, notification);
        }
    }

}
