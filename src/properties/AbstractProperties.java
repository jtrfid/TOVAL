package properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AbstractProperties {

	protected Properties props;
	
	public AbstractProperties(String fileName) throws IOException{
		load(fileName);
	}
	
	public AbstractProperties(){
		props = getDefaultProperties();
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
	
	protected Properties getDefaultProperties(){
		return new Properties();
	}

}
