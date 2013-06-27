package de.invation.code.toval.types;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.invation.code.toval.validate.ParameterException.ErrorCode;


public enum DataUsage {
	
	READ("R"), WRITE("W"), CREATE("C"), DELETE("D");
	
	private String abbreviation;
	
	private DataUsage(String abbreviation){
		this.abbreviation = abbreviation;
	}
	
	public String abbreviation(){
		return abbreviation;
	}
	
	public static DataUsage fromAbbreviation(String abbreviation){
		if(abbreviation == null)
			return null;
		if(abbreviation.equals("R"))
			return READ;
		if(abbreviation.equals("W"))
			return WRITE;
		if(abbreviation.equals("C"))
			return WRITE;
		if(abbreviation.equals("D"))
			return DELETE;
		return null;
	}
	
	public static DataUsage parse(String dataUsageString) throws ParameterException{
		Validate.notNull(dataUsageString);
		Validate.notEmpty(dataUsageString);
		
		for(DataUsage usageMode: DataUsage.values()){
			if(dataUsageString.toUpperCase().equals(usageMode.toString()))
				return usageMode;
		}
		
		throw new ParameterException(ErrorCode.INCOMPATIBILITY);
	}

}
