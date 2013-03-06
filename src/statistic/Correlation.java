package statistic;
import java.util.Arrays;
import java.util.List;


/**
 * Class for determining the correlation of two different value distributions in form of observations.
 * It provides methods for the calculating the covariance of the distributions,
 * their standard deviations and the corresponding correlation coefficient.
 * 
 * @author Thomas Stocker
 */
public class Correlation {
	/**
	 * First value distribution in form of an observation.
	 */
	private Observation observationA;
	/**
	 * Second value distribution in form of an observation.
	 */
	private Observation observationB;
	
	
//	private HashMap<Integer, Double> quadrants = new HashMap<Integer, Double>(4);
//	private Double matchingQuotient = null;
	
	/**
	 * Covariance of the two value distributions.<br>
	 * The covariance describes the linear relation between two random variables (how much they change together).
	 */
	private Double covariance = null;
	
	/**
	 * Correlation coefficient of the two distributions.<br>
	 * This value between -1 and 1 is a correlation measure indicating complete negative/positive correlation.<br>
	 */
	private Double correlationCoefficient = null;
	
	/**
	 * Standard deviation of the first value distribution.<br>
	 * The standard deviation shows the variation/dispersion from the average.<br>
	 * A low standard deviation indicates that the data points tend to be very close to the mean,<br> 
	 * whereas high standard deviation indicates that the data are spread out over a large range of values.<br>
	 */
	private Double standardDeviationA = null;
	
	/**
	 * Standard deviation of the first value distribution.
	 * The standard deviation shows the variation/dispersion from the average.<br>
	 * A low standard deviation indicates that the data points tend to be very close to the mean,<br> 
	 * whereas high standard deviation indicates that the data are spread out over a large range of values.<br>
	 */
	private Double standardDeviationB = null;

	/**
	 * Creates a new Correlation out of two given value distributions.<br>
	 * It creates {@link Observation}s out of the arrays and makes sure,<br>
	 * that it can access their variance for later calculations.
	 * @param valuesA First value distribution
	 * @param valuesB Second value distribution
	 */
	public Correlation(Double[] valuesA, Double[] valuesB) {
		if(valuesA == null || valuesB == null)
			throw new NullPointerException();
		if(valuesA.length != valuesB.length)
			throw new IllegalArgumentException("Lengths of value array do not match!");
		if(valuesA.length==0)
			throw new IllegalArgumentException("Empty value arrays!");
		
		observationA = new Observation(Arrays.asList(valuesA));
		observationA.setMomentDegrees(2);
		observationB = new Observation(Arrays.asList(valuesB));
		observationB.setMomentDegrees(2);
	}
	
	/**
	 * Returns the first value distribution in form of an observation.
	 * @return The first value distribution
	 */
	public Observation getObservationA() {
		return observationA;
	}
	
	/**
	 * Returns the second value distribution in form of an observation.
	 * @return The second value distribution
	 */
	public Observation getObservationB() {
		return observationB;
	}
	
	/**
	 * Returns the first value distribution in form of a list.
	 * @return The first value distribution
	 */
	public List<Double> getValuesA() {
		return observationA.getValues();
	}
	
	/**
	 * Returns the second value distribution in form of a list.
	 * @return The second value distribution
	 */
	public List<Double> getValuesB() {
		return observationB.getValues();
	}
	
	
	
	
	/**
	 * Returns the correlation coefficient of the two distributions.<br>
	 * This value between -1 and 1 is a correlation measure indicating complete negative/positive correlation.<br>
	 * @return The correlation coefficient of the two distributions
	 */
	public double getCorrelationCoefficient() {
		if(correlationCoefficient == null) {
			correlationCoefficient = getCovariance()/(getStandardDeviationA()*getStandardDeviationB());
		}
		return correlationCoefficient;
	}
	
	/**
	 * Returns the standard deviation of the first value distribution.<br>
	 * The standard deviation shows the variation/dispersion from the average.<br>
	 * A low standard deviation indicates that the data points tend to be very close to the mean,<br> 
	 * whereas high standard deviation indicates that the data are spread out over a large range of values.<br>
	 * @return The standard deviation of the first value distribution
	 */
	public double getStandardDeviationA() {
		if(standardDeviationA == null) {
			standardDeviationA = Math.sqrt(observationA.getMoments().get(2));
		}
		return standardDeviationA;
	}
	
	/**
	 * Returns the standard deviation of the second value distribution.<br>
	 * The standard deviation shows the variation/dispersion from the average.<br>
	 * A low standard deviation indicates that the data points tend to be very close to the mean,<br> 
	 * whereas high standard deviation indicates that the data are spread out over a large range of values.<br>
	 * @return The standard deviation of the second value distribution
	 */
	public double getStandardDeviationB() {
		if(standardDeviationB == null) {
			standardDeviationB = Math.sqrt(observationB.getMoments().get(2));
		}
		return standardDeviationB;
	}
	
	/**
	 * Return the covariance of the two value distributions.<br>
	 * The covariance describes the linear relation between two random variables (how much they change together).
	 * @return The covariance of the two value distributions 
	 */
	public double getCovariance() {
		if(covariance == null) {
			Observation o = new Observation();
			for(int i=0; i<observationA.getObservationCount(); i++)
				o.addValue(observationA.getValueAt(i)*observationB.getValueAt(i));
			covariance = o.getExpectation()-observationA.getExpectation()*observationB.getExpectation();
		}
		return covariance;
	}
	
//	public double getMatchingQuotient() {
//		if(matchingQuotient == null) {
//			fillQuadrants();
//			matchingQuotient = (quadrants.get(1)+quadrants.get(3))/(quadrants.get(2)+quadrants.get(4));
//		}
//		return matchingQuotient;
//	}
	
	
//	private void fillQuadrants() {
//		quadrants.put(1, 0.0);
//		quadrants.put(2, 0.0);
//		quadrants.put(3, 0.0);
//		quadrants.put(4, 0.0);
//		double valueA,valueB;
//		for(int i=0; i<observationA.getObservationCount(); i++) {
//			valueA = observationA.getValueAt(i);
//			valueB = observationB.getValueAt(i);
//			if(valueA < observationA.getAverage()) {
//				if(valueB < observationB.getAverage()) {
//					quadrants.put(3, quadrants.get(3)+1);
//				} else {
//					if(valueB > observationB.getAverage()) {
//						quadrants.put(4, quadrants.get(4)+1);
//					} else {
//						//valueB == averageB
//						quadrants.put(3, quadrants.get(3)+0.5);
//						quadrants.put(4, quadrants.get(4)+0.5);
//					}
//				} 
//			} else {
//				if(valueA > observationA.getAverage()) {
//					if(valueB < observationB.getAverage()) {
//						quadrants.put(2, quadrants.get(2)+1);
//					} else {
//						if(valueB > observationB.getAverage()) {
//							quadrants.put(1, quadrants.get(1)+1);
//						} else {
//							//valueB == averageB
//							quadrants.put(1, quadrants.get(1)+0.5);
//							quadrants.put(2, quadrants.get(2)+0.5);
//						}
//					} 
//				} else {
//					//valueA == averageA
//					if(valueB < observationB.getAverage()) {
//						quadrants.put(2, quadrants.get(2)+0.5);
//						quadrants.put(3, quadrants.get(3)+0.5);
//					} else {
//						if(valueB > observationB.getAverage()) {
//							quadrants.put(1, quadrants.get(1)+0.5);
//							quadrants.put(4, quadrants.get(4)+0.5);
//						} else {
//							//valueB == averageB
//							quadrants.put(1, quadrants.get(1)+0.25);
//							quadrants.put(2, quadrants.get(2)+0.25);
//							quadrants.put(3, quadrants.get(3)+0.25);
//							quadrants.put(4, quadrants.get(4)+0.25);
//						}
//					} 
//				}
//			} 
//		}
//	}

}
