package de.invation.code.toval.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Weekday implements Comparable<Weekday>{

	MONDAY("Monday"), 
	TUESDAY("Tuesday"), 
	WEDNESDAY("Wednesday"), 
	THURSDAY("Thursday"), 
	FRIDAY("Friday"), 
	SATURDAY("Saturday"),
	SUNDAY("Sunday");
	
	private static final String[] descriptors = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	
	private String descriptor = null;
	
	private Weekday(String descriptor){
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString(){
		return descriptor;
	}
	
	public static List<String> stringValues(){
		List<String> result = new ArrayList<String>(Arrays.asList(descriptors));
		return result;
	}

}
