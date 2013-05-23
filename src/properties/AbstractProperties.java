package properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import validate.ParameterException;
import validate.ParameterException.ErrorCode;
import validate.Validate;

public class AbstractProperties {

	protected Properties props;
	
	public AbstractProperties(String fileName) throws IOException{
		load(fileName);
	}
	
	public AbstractProperties(){
		loadDefaultProperties();
	}
	
	public Properties getProperties(){
		return props;
	}
	
	public void store(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		props.store(fos, "");
		fos.close();
	}
	
	public void load(String fileName) throws IOException{
		props = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		props.load(fis);
		fis.close();
	}
	
	protected void loadDefaultProperties(){
		props = getDefaultProperties();
	}
	
	protected Properties getDefaultProperties(){
		return new Properties();
	}
	
	//------- Helper methods --------------------------------------------------------------
	
	protected String[] encapsulateValues(Set<String> values){
		String[] result = new String[values.size()];
		int count = 0;
		for(String value: values)
			result[count++] = "'"+value+"'";
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
