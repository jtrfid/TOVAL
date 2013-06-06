package misc;


public class FormatUtils {
	
	private static final double BASE = 1024, KB = BASE, MB = KB*BASE, GB = MB*BASE;
	
	/**
	 * Format used for representing integers.
	 */
	public static final String INTEGER_FORMAT = "%d";
	/**
	 * Generator-String used for creating formats for representing non-integers.<br>
	 * It can be parameterized by the desired precision.
	 * @see #getFloatFormat(int)
	 */
	private static final String FLOAT_FORMAT = "%%.%df";
	/**
	 * Format used for representing strings.
	 */
	public static final String STRING_FORMAT = "%s";
	/**
	 * Generator-String used for creating indexed formats.<br>
	 * It can be parameterized by the desired index and the format itself (integer, float, string).
	 * @see #getIndexedFormat(int, String)
	 */
	private static final String INDEXED_FORMAT = "%%%s$%s";
	/**
	 * Generator-String used for creating generator-strings for creating formats with trim-constraints.<br>
	 * It provides a basis for generating formatted output with a fixed length
	 * and can be parameterized by the desired length.
	 * @see #getTrimFormat(int)
	 * @see #trimToLength(Object, int)
	 */
	private static final String TRIM_BASE_FORMAT = "%%%s%%s";
	/**
	 * Default precision used for generating formats for non-integers.
	 */
	public static final int DEFAULT_PRECISION = 2;
	
	/**
	 * Returns a String representation of the given object using an appropriate format.
	 * @param o Object for which a String representation is desired.
	 * @return String representation of the given object using an appropriate format
	 * @see #getFormat(Object)
	 * @see #getFormat(Object, int)
	 */
	public static String format(Object o) {
		return format(o, DEFAULT_PRECISION);
	}
	
	/**
	 * Returns a String representation of the given object using an appropriate format 
	 * and the specified precision in case the object is a non-integer number.
	 * @param o Object for which a String representation is desired.
	 * @param precision Desired precision
	 * @return String representation of the given object using an appropriate format
	 */
	public static String format(Object o, int precision) {
		return String.format(getFormat(o, precision), o);
	}
	
	/**
	 * Returns a String representation of the given object using an appropriate format
	 * trimmed to the specified length.
	 * @param o Object for which a String representation is desired.
	 * @return String representation of the given object using an appropriate format
	 * @see #getFormat(Object)
	 * @see #getFormat(Object, int)
	 */
	public static String formatTrimmed(Object o, int length) {
		return formatTrimmed(o, DEFAULT_PRECISION, length);
	}
	
	/**
	 * Returns a String representation of the given object using an appropriate format 
	 * and the specified precision in case the object is a non-integer number,
	 * trimmed to the specified length.
	 * @param o Object for which a String representation is desired.
	 * @param precision Desired precision
	 * @return String representation of the given object using an appropriate format
	 */
	public static String formatTrimmed(Object o, int precision, int length) {
		return String.format(getTrimmedFormat(getFormat(o, precision), length), o);
	}
	
	
	
	/**
	 * Returns a format for non-integers using the default precision.
	 * @return The default format for non-integers
	 */
	public static String getFloatFormat() {
		return getFloatFormat(DEFAULT_PRECISION);
	}
	
	/**
	 * Returns a format for non-integers using the specified precision.
	 * @param precision Desired precision
	 * @return Format for non-integers with the specified precision
	 */
	public static String getFloatFormat(int precision) {
		if(precision<0)
			throw new IllegalArgumentException();
		return String.format(FLOAT_FORMAT, precision);
	}
	
	/**
	 * Returns an appropriate format for the given object.<br>
	 * <ul>
	 * <li>case 1: object is of type <code>Float</code> or <code>Double</code> -> float format</li>
	 * <li>case 2: object is a whole number -> integer format</li>
	 * <li>case 3: object is of any other type -> string format</li>
	 * </ul>
	 * @param o Object for which a format has to be assigned
	 * @return An appropriate format for the given object
	 */
	public static String getFormat(Object o) {
		return getFormat(o, DEFAULT_PRECISION);
	}
	
	/**
	 * Returns an appropriate format for the given object,
	 * considering the specified precision in case the object is a non-integer number.<br>
	 * <ul>
	 * <li>case 1: object is of type <code>Float</code> or <code>Double</code> -> float format with the specified precision</li>
	 * <li>case 2: object is a whole number -> integer format</li>
	 * <li>case 3: object is of any other type -> string format</li>
	 * </ul>
	 * @param o Object for which a format has to be assigned
	 * @param precision Desired precision
	 * @return An appropriate format for the given object
	 */
	public static String getFormat(Object o, int precision) {
		if(o instanceof Double || o instanceof Float)
			return getFloatFormat(precision);
		if(o instanceof Integer || o instanceof Long || o instanceof Short || o instanceof Byte)
			return INTEGER_FORMAT;
		return STRING_FORMAT;
	}
	
	/**
	 * Returns an indexed format by placing the specified index before the given format.
	 * @param index Desired index for the given format
	 * @param format Format to be indexed
	 * @return The format {@link format} indexed with {@link index}
	 */
	public static String getIndexedFormat(int index, String format) {
		if(index<1)
			throw new IllegalArgumentException();
		if(format == null)
			throw new NullPointerException();
		if(format.length()==0)
			throw new IllegalArgumentException();
		return String.format(INDEXED_FORMAT, index, format);
	}
	
	/**
	 * Extends a given format by additional constraints for trimmed (length-limited) outputs.<br>
	 * It assumes that the given String is a valid format.
	 * @param format Format to extend
	 * @param length Desired fixed output length for formatted output
	 * @return Extended version of {@link format} by trim-constraints
	 */
	public static String getTrimmedFormat(String format, int length) {
		return String.format('%'+String.format(TRIM_BASE_FORMAT, length), format.substring(1));
	}
	
	public static String formatFileSize(double size) {
        if(size >= GB) {
            return format(size/GB, 2) + " GB";
        }
        if(size >= MB) {
            return format(size/MB, 2) + " MB";
        }
        if(size >= KB) {
            return format(size/KB, 2) + " KB";
        }
        return "" + (int)size + " bytes";
    }



}
