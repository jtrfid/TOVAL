package de.invation.code.toval.statistic;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;



public class StochasticUtils {
	
	//------EXPECTATION----------------------------------------------------------------------------------------
	
	public static double getExpectation(Collection<Double> values) {
		return new Observation(values).getExpectation();
	}	
	
	//------MOMENTS--------------------------------------------------------------------------------------------
	
	public static double getMoment(Collection<Double> values, int degree){
		return getMoments(values, Arrays.asList(degree)).get(degree);
	}
	
	public static Map<Integer, Double> getMoments(Collection<Double> values, Collection<Integer> degrees){
		Observation o = new Observation(values);
		o.setMomentDegrees(degrees);
		return o.getMoments();
	}
		
	//------TRANSFORMATION--------------------------------------------------------------------------------------

	
	//------AVERAGES--------------------------------------------------------------------------------------------
	public static <T extends Number> double getAverage(Collection<T> values, AVERAGE_TYPE type) {
		switch(type) {
			case ARITHMETIC:
				double sum = 0.0;
				for(T t: values) {
					sum += t.doubleValue();
				}
				return sum/values.size();
			default: throw new IllegalArgumentException("\""+type+"\" is not a valid verage-type!");
		}
	}
	
	public enum AVERAGE_TYPE {ARITHMETIC}

}
