package de.invation.code.toval.misc.soabase;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.misc.StringUtils;
import de.invation.code.toval.properties.AbstractProperties;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.Validate;

public class SOABaseProperties extends AbstractProperties{
	
	//------- Property setting -------------------------------------------------------------
	
	private void setProperty(SOABaseProperty contextProperty, Object value){
		props.setProperty(contextProperty.toString(), value.toString());
	}
	
	private String getProperty(SOABaseProperty contextProperty){
		return props.getProperty(contextProperty.toString());
	}
	
	//-- Context name
	
	public void setName(String name){
		Validate.notNull(name);
		Validate.notEmpty(name);
		setProperty(SOABaseProperty.NAME, name);
	}
	
	public String getName() throws PropertyException {
		String propertyValue = getProperty(SOABaseProperty.NAME);
		if(propertyValue == null)
			throw new PropertyException(SOABaseProperty.NAME, propertyValue);
		return propertyValue;
	}
	
	//-- Subjects
	
	public void setSubjects(Set<String> subjects){
		Validate.notNull(subjects);
		if(subjects.isEmpty())
			return;
		Validate.noNullElements(subjects);
		setProperty(SOABaseProperty.SUBJECTS, ArrayUtils.toString(encapsulateValues(subjects)));
	}
	
	public Set<String> getSubjects(){
		Set<String> result = new HashSet<String>();
		String propertyValue = getProperty(SOABaseProperty.SUBJECTS);
		if(propertyValue == null)
			return result;
		StringTokenizer subjectTokens = StringUtils.splitArrayString(propertyValue, String.valueOf(ArrayUtils.VALUE_SEPARATION));
		while(subjectTokens.hasMoreTokens()){
			String nextToken = subjectTokens.nextToken();
			result.add(nextToken.substring(1, nextToken.length()-1));
		}
		return result;
	}
	
	//-- Objects
	
	public void setObjects(Set<String> objects) {
		Validate.notNull(objects);
		if(objects.isEmpty())
			return;
		Validate.noNullElements(objects);
		setProperty(SOABaseProperty.OBJECTS, ArrayUtils.toString(encapsulateValues(objects)));
	}
	
	public Set<String> getObjects(){
		Set<String> result = new HashSet<String>();
		String propertyValue = getProperty(SOABaseProperty.OBJECTS);
		if(propertyValue == null)
			return result;
		StringTokenizer subjectTokens = StringUtils.splitArrayString(propertyValue, String.valueOf(ArrayUtils.VALUE_SEPARATION));
		while(subjectTokens.hasMoreTokens()){
			String nextToken = subjectTokens.nextToken();
			result.add(nextToken.substring(1, nextToken.length()-1));
		}
		return result;
	}
	
	//-- Activities
	
	public void setActivities(Set<String> transactions){
		Validate.notNull(transactions);
		if(transactions.isEmpty())
			return;
		Validate.noNullElements(transactions);
		setProperty(SOABaseProperty.ACTIVITIES, ArrayUtils.toString(encapsulateValues(transactions)));
	}
	
	public Set<String> getActivities(){
		Set<String> result = new HashSet<String>();
		String propertyValue = getProperty(SOABaseProperty.ACTIVITIES);
		if(propertyValue == null)
			return result;
		StringTokenizer subjectTokens = StringUtils.splitArrayString(propertyValue, String.valueOf(ArrayUtils.VALUE_SEPARATION));
		while(subjectTokens.hasMoreTokens()){
			String nextToken = subjectTokens.nextToken();
			result.add(nextToken.substring(1, nextToken.length()-1));
		}
		return result;
	}

}
