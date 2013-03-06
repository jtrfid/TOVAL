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
			notNull(o);
		}
	}
	
	public static <O extends Object> void noNullElements(O[] arr) throws ParameterException {
		for(int i=0; i<arr.length; i++){
			notNull(arr[i]);
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
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
	}
	
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value, String message) throws ParameterException {
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
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
	
	public static void notTrue(Boolean expression, String message) throws ParameterException{
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT, message);
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference) throws ParameterException {
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static void probability(Double value) throws ParameterException{
		Validate.inclusiveBetween(0.0, 1.0, value);
	}
	
	public static void probability(Double value, String message) throws ParameterException{
		Validate.inclusiveBetween(0.0, 1.0, value, message);
	}
	
	public static void minMax(int min, int max) throws ParameterException{
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION);
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
	
	public static void main(String[] args) throws Exception {
		Integer i = 5;
		Validate.smallerEqual(i, 6);
	}

}
