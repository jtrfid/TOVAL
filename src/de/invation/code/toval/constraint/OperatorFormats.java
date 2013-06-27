package de.invation.code.toval.constraint;

import java.util.HashMap;
import java.util.Map;

public class OperatorFormats {
	
	public static final String SMALLER = "<";
	
	public static final String SMALLER_EQUAL = "<=";
	
	public static final String EQUAL = "==";
	
	public static final String NOT_EQUAL = "!=";
	
	public static final String LARGER = ">";
	
	public static final String LARGER_EQUAL = ">=";
	
	public static final String IN_INTERVAL = "[]";
	
	public static final String NOT_IN_INTERVAL = "][";
	
	public static final Map<String, String> descriptors = new HashMap<String, String>();
	static {
		descriptors.put(SMALLER, "Attribute value is smaller than a comparator.");
		descriptors.put(SMALLER_EQUAL, "Attribute value is smaller than or equal a comparator.");
		descriptors.put(EQUAL, "Attribute value equals a comparator.");
		descriptors.put(NOT_EQUAL, "Attribute value does not equal a comparator.");
		descriptors.put(LARGER, "Attribute value is larger than a comparator.");
		descriptors.put(LARGER_EQUAL, "Attribute value is larger than or equal a comparator.");
		descriptors.put(IN_INTERVAL, "Attribute value lies within a specific interval.");
		descriptors.put(NOT_IN_INTERVAL, "Attribute value does not lie within a specific interval.");
	}
	
	public static final String COMPARISON_FORMAT = "%%s %s %%s";
	
	public static final String IN_INTERVAL_FORMAT = "%s ? [%s;%s]";
	
	public static final String NOT_IN_INTERVAL_FORMAT = "%s ? ]%s;%s[";
	
	public static String getDescriptor(Operator<?> operator){
		return descriptors.get(operator.toString());
	}

}
