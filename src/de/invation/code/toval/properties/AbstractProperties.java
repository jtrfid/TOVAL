package de.invation.code.toval.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;


public class AbstractProperties {

	protected Properties props;
	
	public AbstractProperties(String fileName) throws IOException{
		load(fileName);
	}
	
	public AbstractProperties(){
		loadDefaultProperties();
	}
	
	protected AbstractProperties(Properties properties){
		setProperties(properties);
	}
	
	public Properties getProperties(){
		return props;
	}
	
	public void store(String filename) throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			props.store(fos, "");
			fos.close();
		} catch (Exception e) {
			throw new IOException("Cannot store properties file to disk.");
		}
	}
	
	public void load(String fileName) throws IOException{
		props = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		props.load(fis);
		fis.close();
	}
	
	public void setProperties(Properties properties){
		this.props = properties;
	}
	
	protected void loadDefaultProperties(){
		props = getDefaultProperties();
	}
	
	protected Properties getDefaultProperties(){
		return new Properties();
	}
	
	//------- Helper methods --------------------------------------------------------------
	
	protected <T extends Object> String[] encapsulateValues(Set<T> values){
		String[] result = new String[values.size()];
		int count = 0;
		for(Object value: values)
			result[count++] = "'"+value.toString()+"'";
		return result;
	}
	
	//------- Validation -------------------------------------------------------------------
	
	public static void validateStringValue(String value) throws ParameterException{
		Validate.notNull(value);
		Validate.notEmpty(value);
	}
	
	public static void validatePath(String logPath) throws ParameterException {
		validateStringValue(logPath);
		File cPath = new File(logPath);
		if(!cPath.isDirectory())
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, logPath + " is not a valid path!");
	}
	
	public static void validateStringCollection(Collection<String> coll) throws ParameterException{
		Validate.notNull(coll);
		Validate.notEmpty(coll);
		for(String string: coll)
			validateStringValue(string);
	}
	

}
