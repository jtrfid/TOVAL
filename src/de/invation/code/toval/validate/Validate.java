package de.invation.code.toval.validate;

import java.io.File;

import de.invation.code.toval.validate.ParameterException.ErrorCode;


public class Validate {
	
	public static <O extends Object> void notNull(O o) throws ParameterException{
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER);
	}
	
	public static <O extends Object> void notNull(O o, String message) throws ParameterException{
		notNull(message);
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER, message);
	}
	
	public static <O extends Object> void noNullElements(Iterable<O> coll) throws ParameterException {
		notNull(coll);
		for(O o: coll){
			try{
				notNull(o);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	public static <O extends Object> void noNullElements(O[] arr) throws ParameterException {
		notNull(arr);
		for(int i=0; i<arr.length; i++){
			try{
				notNull(arr[i]);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	public static <O extends Object> void notEmpty(Iterable<O> coll) throws ParameterException {
		notNull(coll);
		if(!coll.iterator().hasNext())
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	public static <O extends Object> void notEmpty(O[] arr) throws ParameterException {
		notNull(arr);
		if(arr.length == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	public static void notEmpty(String string) throws ParameterException {
		notNull(string);
		if(string.length() == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value) throws ParameterException {
		Validate.notNull(start);
		Validate.notNull(end);
		Validate.notNull(value);
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]");
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]");
	}
	
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value, String message) throws ParameterException {
		Validate.notNull(start);
		Validate.notNull(end);
		Validate.notNull(value);
		Validate.notNull(message);
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static void negative(Integer value) throws ParameterException{
		Validate.notNull(value);
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Integer value, String message) throws ParameterException{
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void negative(Double value) throws ParameterException{
		Validate.notNull(value);
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Double value, String message) throws ParameterException{
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void negative(Long value) throws ParameterException{
		Validate.notNull(value);
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	public static void negative(Long value, String message) throws ParameterException{
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	public static void notNegative(Integer value) throws ParameterException{
		Validate.notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Integer value, String message) throws ParameterException{
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notNegative(Double value) throws ParameterException{
		notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Double value, String message) throws ParameterException{
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notNegative(Long value) throws ParameterException{
		notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	public static void notNegative(Long value, String message) throws ParameterException{
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	public static void notTrue(Boolean expression) throws ParameterException{
		notNull(expression);
		if(expression)
			throw new ParameterException(ErrorCode.CONSTRAINT);
	}
	
	public static void notTrue(Boolean expression, String constraint) throws ParameterException{
		notNull(expression);
		notNull(constraint);
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT, "Parameter does not fulfill constraint \""+constraint+"\"");
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference) throws ParameterException {
		Validate.notNull(value);
		Validate.notNull(reference);
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller or equal "+reference);
	}
	
	public static <T extends Object> void bigger(Comparable<T> value, T reference, String message) throws ParameterException {
		Validate.notNull(value);
		Validate.notNull(reference);
		Validate.notNull(message);
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference) throws ParameterException {
		Validate.notNull(value);
		Validate.notNull(reference);
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller than "+reference);
	}
	
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		Validate.notNull(value);
		Validate.notNull(reference);
		Validate.notNull(message);
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference) throws ParameterException {
		notNull(value);
		notNull(reference);
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger or equal "+reference);
	}
	
	public static <T extends Object> void smaller(Comparable<T> value, T reference, String message) throws ParameterException {
		notNull(value);
		notNull(reference);
		notNull(message);
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference) throws ParameterException {
		notNull(value);
		notNull(reference);
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger "+reference);
	}
	
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference, String message) throws ParameterException {
		notNull(value);
		notNull(reference);
		notNull(message);
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	public static Double probability(String value) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 1.0, doubleValue);
		return doubleValue;
	}
	
	public static void probability(Double value) throws ParameterException{
		inclusiveBetween(0.0, 1.0, value);
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
		inclusiveBetween(0.0, 100.0, value);
	}
	
	public static void percentage(Double value, String message) throws ParameterException{
		inclusiveBetween(0.0, 100.0, value, message);
	}
	
	public static Double percentage(String value, String message) throws ParameterException{
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 100.0, doubleValue, message);
		return doubleValue;
	}
	
	public static void minMax(Integer min, Integer max) throws ParameterException{
		Validate.notNull(min);
		Validate.notNull(max);
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	public static void minMax(Long min, Long max) throws ParameterException{
		Validate.notNull(min);
		Validate.notNull(max);
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	/**
	 * Check if a given value has a specific type.
	 * @param value
	 * @param type
	 * @throws ParameterException 
	 */
	public static <T,V extends Object> void type(V value, Class<T> type) throws ParameterException{
		notNull(value);
		notNull(type);
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
		Validate.notEmpty(value);
		Integer intValue = null;
		try{
			intValue = 	Integer.parseInt(value);
		} catch(NumberFormatException e){
			throw new TypeException("Integer");
		}
		return intValue;
	}
	
	public static void positive(Integer value) throws ParameterException{
		notNull(value);
		if(value <= 0)
			throw new ParameterException(ErrorCode.NOTPOSITIVE);
	}
	
	public static void positive(Long value) throws ParameterException{
		notNull(value);
		if(value <= 0)
			throw new ParameterException(ErrorCode.NOTPOSITIVE);
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
		Validate.notEmpty(value);
		Double doubleValue = null;
		try{
			doubleValue = Double.parseDouble(value);
		} catch(NumberFormatException e){
			throw new TypeException("Double");
		}
		return doubleValue;
	}
	
	public static void fileName(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		if(fileName.contains(File.separator))
			throw new ParameterException("File name contains path separator char!");
	}
	
	public static File noDirectory(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.noDirectory(new File(fileName));
	}
	
	public static File directory(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.directory(new File(fileName));
	}
	
	public static File exists(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.exists(new File(fileName));
	}
	
	public static File notExists(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.notExists(new File(fileName));
	}
	
	public static File noDirectory(File file) throws ParameterException{
		Validate.exists(file);
		if(file.isDirectory())
			throw new ParameterException("\""+file+"\" is a directory!");
		return file;
	}
	
	public static File directory(File file) throws ParameterException{
		Validate.exists(file);
		if(!file.isDirectory())
			throw new ParameterException("\""+file+"\" is not a directory!");
		return file;
	}
	
	public static File exists(File file) throws ParameterException{
		Validate.notNull(file);
		if(!file.exists())
			throw new ParameterException("\""+file+"\" does not exist!");
		return file;
	}
	
	public static File notExists(File file) throws ParameterException{
		Validate.notNull(file);
		if(file.exists())
			throw new ParameterException("\""+file+"\" exists!");
		return file;
	}

}
