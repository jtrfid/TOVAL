package validate;

import validate.ParameterException.ErrorCode;


public class Validate {
	
	public static <O extends Object> void notNull(O o) throws ParameterException{
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER);
	}
	
	public static <O extends Object> void notNull(O o, String message) throws ParameterException{
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER, message);
	}
	
	public static <O extends Object> void noNullElements(Iterable<O> coll) throws ParameterException {
		for(O o: coll){
			try{
				notNull(o);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	public static <O extends Object> void noNullElements(O[] arr) throws ParameterException {
		for(int i=0; i<arr.length; i++){
			try{
				notNull(arr[i]);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	public static <O extends Object> void notEmpty(Iterable<O> coll) throws ParameterException {
		if(!coll.iterator().hasNext())
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	public static <O extends Object> void notEmpty(O[] arr) throws ParameterException {
		if(arr.length == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	public static void notEmpty(String string) throws ParameterException {
		if(string.length() == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value) throws ParameterException {
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]");
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]");
	}
	
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value, String message) throws ParameterException {
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static void negative(Integer value) throws ParameterException{
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Integer value, String message) throws ParameterException{
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void negative(Double value) throws ParameterException{
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Double value, String message) throws ParameterException{
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void negative(Long value) throws ParameterException{
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Long value, String message) throws ParameterException{
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void notNegative(Integer value) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Integer value, String message) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notNegative(Double value) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Double value, String message) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notNegative(Long value) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Long value, String message) throws ParameterException{
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notTrue(Boolean expression) throws ParameterException{
		if(expression)
			throw new ParameterException(ErrorCode.CONSTRAINT);
	}
	
	public static void notTrue(Boolean expression, String constraint) throws ParameterException{
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT, "Parameter does not fulfill constraint \""+constraint+"\"");
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller or equal "+reference);
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller than "+reference);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger or equal "+reference);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger "+reference);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static Double probability(String value) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 1.0, doubleValue);
		return doubleValue;
	}
	
	public static void probability(Double value) throws ParameterException{
		Validate.inclusiveBetween(0.0, 1.0, value);
	}
	
	public static Double probability(String value, String message) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 1.0, doubleValue, message);
		return doubleValue;
	}
	
	public static void probability(Double value, String message) throws ParameterException{
		Validate.inclusiveBetween(0.0, 1.0, value, message);
	}
	
	public static Double percentage(String value) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 100.0, doubleValue);
		return doubleValue;
	}
	
	public static void percentage(Double value) throws ParameterException{
		Validate.inclusiveBetween(0.0, 100.0, value);
	}
	
	public static void percentage(Double value, String message) throws ParameterException{
		Validate.inclusiveBetween(0.0, 100.0, value, message);
	}
	
	public static Double percentage(String value, String message) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 100.0, doubleValue, message);
		return doubleValue;
	}
	
	public static void minMax(int min, int max) throws ParameterException{
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	public static void minMax(long min, long max) throws ParameterException{
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	/**
	 * Check if a given value has a specific type.
	 * @param value
	 * @param type
	 * @throws TypeException 
	 */
	public static <T,V extends Object> void type(V value, Class<T> type) throws TypeException{
		if(!type.isAssignableFrom(value.getClass())){
			throw new TypeException(type.getCanonicalName());
		}
	}
	
	public static Integer positiveInteger(String value) throws ParameterException{
		Integer intValue = Validate.isInteger(value);
		Validate.bigger(intValue, 0);
		return intValue;
	}
	
	public static Integer notPositiveInteger(String value) throws ParameterException{
		Integer intValue = Validate.isInteger(value);
		Validate.smallerEqual(intValue, 0);
		return intValue;
	}
	
	public static Integer negativeInteger(String value) throws ParameterException{
		Integer intValue = Validate.isInteger(value);
		Validate.smaller(intValue, 0);
		return intValue;
	}
	
	public static Integer notNegativeInteger(String value) throws ParameterException{
		Integer intValue = Validate.isInteger(value);
		Validate.biggerEqual(intValue, 0);
		return intValue;
	}
	
	public static Integer isInteger(String value) throws ParameterException{
		Validate.notNull(value);
		Integer intValue = null;
		try{
			intValue = 	Integer.parseInt(value);
		} catch(NumberFormatException e){
			throw new TypeException("Integer");
		}
		return intValue;
	}
	
	public static Double positiveDouble(String value) throws ParameterException{
		Double doubleValue = Validate.isDouble(value);
		Validate.bigger(doubleValue, 0.0);
		return doubleValue;
	}
	
	public static Double notPositiveDouble(String value) throws ParameterException{
		Double doubleValue = Validate.isDouble(value);
		Validate.smallerEqual(doubleValue, 0.0);
		return doubleValue;
	}
	
	public static Double negativeDouble(String value) throws ParameterException{
		Double doubleValue = Validate.isDouble(value);
		Validate.smaller(doubleValue, 0.0);
		return doubleValue;
	}
	
	public static Double notNegativeDouble(String value) throws ParameterException{
		Double doubleValue = Validate.isDouble(value);
		notNegative(doubleValue);
		return doubleValue;
	}
	
	public static Double isDouble(String value) throws ParameterException{
		Validate.notNull(value);
		Double doubleValue = null;
		try{
			doubleValue = 	Double.parseDouble(value);
		} catch(NumberFormatException e){
			throw new TypeException("Double");
		}
		return doubleValue;
	}
	
	public static void main(String[] args) throws Exception {
		Double d = 1.0;
		Validate.notNegative(d);
	}

}
