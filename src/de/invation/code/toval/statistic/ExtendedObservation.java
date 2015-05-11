package de.invation.code.toval.statistic;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Extension of the class {@link Observation}, that adds additional information.<br>
 * By using Observations for the expectation value and the moments,<br>
 * it is possible to analyze their progress along the insertion steps.
 * 
 * @author Thomas Stocker
 */
public class ExtendedObservation extends Observation {
	
	/**
	 * Observation for expectations.
	 * @see Observation#expectation
	 * @see Observation#getExpectation()
	 */
	private Observation expectations = new Observation("expectation", true);
	
	/**
	 * Observations for moments.
	 * @see Observation#moments
	 * @see Observation#getMoments()
	 */
	private HashMap<Integer, Observation> momentObservation = new HashMap<Integer, Observation>(DEFAULT_MOMENTS.size());
	
	
	public ExtendedObservation() {
		this(DEFAULT_NAME);
	}
	
	public ExtendedObservation(String name) {
		super(name, true);
		resetMomentObservation();
	}
	
	/**
	 * Returns the expectations for every insertion step in form of an observation.
	 * @return The expectation observation
	 */
	public Observation getExpectationObservation(){
		return expectations;
	}

	/**
	 * Returns the expectations for every insertion step in form of a list.
	 * @return The expectation progress
	 */
	public List<Double> getExpectations(){
		return Collections.unmodifiableList(expectations.getValues());
	}
	
	/**
	 * Returns the expectation for a certain insertion step specified by the given index.
	 * @param index Index of the desired insertion step
	 * @return The expectation for the specified insertion step
	 */
	public double getExpectationAt(int index){
		if(index <0 || index>=expectations.getObservationCount())
			throw new IndexOutOfBoundsException();
		return expectations.getValueAt(index);
	}
	
	/**
	 * Returns the values of all moments for a certain insertion step specified by the given index.
	 * @param index Index of the desired insertion step
	 * @return moment values for the specified insertion step
	 */
	public HashMap<Integer, Double> getMomentsAt(int index){
		if(index <0 || index>=getObservationCount())
			throw new IndexOutOfBoundsException();
		HashMap<Integer, Double> ret = new HashMap<Integer, Double>();
		for(Integer m: momentObservation.keySet()) {
			Double value = momentObservation.get(m).getValueAt(index);
			if(value != null)
				ret.put(m, value);
		}
		return ret;
	}
	
	/**
	 * Determines the expectation which is a probabilistic measure,<br>
	 * estimating the next inserted value based on all values inserted so far.
	 * Overrides a superclass method to additionally consider the expectation observation {@link #expectations}.
	 * @see Observation#setExpectation()
	 */
	@Override
	protected void setExpectation() {
		super.setExpectation();
		expectations.addValue(expectation);
	}

	/**
	 * Determines moments (probabilistic measure) of all degrees specified by {@link #momentDegrees}.
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Woelbung)</li>
	 * <li>...</li>
	 * </ul>
	 * <br>
	 * Overrides a superclass method to additionally co0nsider the moment observations {@link #momentObservation}.
	 * @see Observation#setMoments()
	 */
	@Override
	protected void setMoments() {
		super.setMoments();
		for(Integer degree: momentDegrees) {
			momentObservation.get(degree).addValue(moments.get(degree));
		}
	}
	
	/**
	 * Resets the extended observation to the initial state after creation.<br>
	 * Potential changed values for update behavior or moment degrees are kept.<br>
	 * Overrides a superclass method to additionally reset observations for moments and expectation.
	 */
	@Override
	public void reset() {
		super.reset();
		resetMomentObservation();
		expectations.reset();
	}
	
	/**
	 * Resets the moment observations.<br>
	 * If there are no observations yet, new observations are created and added to the map.<br>
	 * Otherwise existing observations are reset.
	 */
	private void resetMomentObservation() {
		if (momentObservation.isEmpty()) {
			Observation o;
			for (int i : momentDegrees) {
				o = new Observation("moment "+i, true);
				o.setStandardPrecision(momentPrecision);
				momentObservation.put(i, o);
			}
		} else {
			for (Observation o : momentObservation.values())
				o.reset();
		}
	}
	
	/**
	 * Returns a String representation of the extended observation containing all values and characteristics<br>
	 * using identation which is specified by <code>identation</code> as the number of spaces.
	 * @param identation The number of spaces used for identation
	 * @return A String representation of the extended observation with identation
	 */
	@Override
	public String toString(int identation) {
		if(identation <0)
			throw new IllegalArgumentException("identation cannot be <0");
		String header;
		if(identation ==0) header="";
		  else header = String.format(String.format("%%%ss", identation), "");
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString(identation));
		builder.append(header);
		builder.append("   Observations:\n\n");
		builder.append(header);
		builder.append(expectations.toString(6));
		builder.append("\n");
		for(Integer i: momentObservation.keySet()) {
			builder.append(header);
			builder.append(momentObservation.get(i).toString(6));
		}
		builder.append("\n");
		return builder.toString();
	}

}
