package de.invation.code.toval.statistic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.invation.code.toval.misc.CollectionUtils;
import de.invation.code.toval.misc.FormatUtils;
import de.invation.code.toval.misc.MapUtils;


/**
 * Class for managing a set of observation values.<br>
 * Inserted values are kept in a sequential list in insertion order and additionally 
 * in a map containing distinct values together with their frequency of occurrence.
 * Basic information like the average value, minimum and maximum
 * are extended by the probabilistic measures of value expectation
 * and a configurable set of discrete moments.
 * 
 * @author Thomas Stocker
 */
public class Observation {
	
	protected int momentPrecision = 6;
	protected int standardPrecision = 2;
	
	/**
	 * Sequential list of all values in insertion order.
	 */
	protected ArrayList<Double> insertSeq = new ArrayList<Double>();
	/**
	 * Map of distinct inserted values together with their frequency of occurrence.
	 */
	protected HashMap<Double, Integer> insertStat = new HashMap<Double, Integer>();
	/**
	 * Number of inserted values.
	 */
	protected double observationCount = 0.0;
	/**
	 * Average over the inserted values (arithmetic).
	 */
	private double average = 0.0;
	/**
	 * Minimum inserted value.
	 */
	private double minimum = Double.MAX_VALUE;
	/**
	 * Maximal inserted value.
	 */
	private double maximum = Double.MIN_VALUE;
	/**
	 * The expected value (probabilistic measure).
	 */
	protected Double expectation = null;
	/**
	 * Controls which central moments are calculated and kept in {@link Observation#moments}
	 * Set to {@link Observation#DEFAULT_MOMENTS} if not set explicitly.
	 */
	protected Collection<Integer> momentDegrees = DEFAULT_MOMENTS;
	/**
	 * Predefined (default) set of moment degrees that comprises the values [2,3,4].
	 */
	protected static final List<Integer> DEFAULT_MOMENTS = Arrays.asList(2,3,4);
	
	/**
	 * Central moments (probabilistic measures).<br>
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 */
	HashMap<Integer, Double> moments = new HashMap<Integer, Double>(momentDegrees.size());
	
	/**
	 * Controls the observations' update behavior.<br>
	 * If <code>true</code> the expectation and all moments are recalculated after every value insertion.<br>
	 * Default value is <code>false</code>.
	 */
	private boolean alwaysUpToDate = false;
	/**
	 * Name of the observation.<br>
	 * Set to {@link Observation#DEFAULT_NAME} if not set explicitly.
	 */
	protected String name = DEFAULT_NAME;
	/**
	 * Default observation name.
	 */
	protected static String DEFAULT_NAME = ""; 
	
	/**
	 * Index-pointer used for resetting the state of the observation.<br>
	 * In case of a reset, all values having a higher index are deleted.
	 */
	private int resetIndex = -1;
	
	
	//-----CONSTRUCTORS--------------------------------------------------------------------------
	
	/**
	 * Creates a new empty observation.<br>
	 * @see #Observation(String)
	 */
	public Observation() {
		this(DEFAULT_NAME);
	}
	
	/**
	 * Creates a new empty observation using the given name.<br>
	 * @param name The observations' name
	 * @see Observation#name
	 */
	public Observation(String name) {
		this.name = name;
	}
	
	/** 
	 * Creates a new empty observation with the given update behavior.<br>
	 * @param alwaysUpToDate The observations' update behavior.
	 * @see Observation#alwaysUpToDate
	 * @see #Observation(String, boolean)
	 */
	public Observation(boolean alwaysUpToDate){
		this(DEFAULT_NAME, alwaysUpToDate);
	}
	
	/**
	 * Creates a new empty observation with the given name and update behavior.<br>
	 * @param name The observations' name
	 * @param alwaysUpToDate The observations' update behavior
	 * @see #name
	 * @see #alwaysUpToDate
	 */
	public Observation(String name, boolean alwaysUpToDate){
		this.name = name;
		this.alwaysUpToDate = alwaysUpToDate;
	}
	
	/**
	 * Creates an observation with the given initial set of values.<br>
	 * @param values Initial set of values.
	 * @see #Observation(String, Collection)
	 */
	public Observation(Collection<Double> values) {
		this(DEFAULT_NAME, values);
	}
	
	/**
	 * Creates an observation with the given name and initial set of values.<br>
	 * @param name The observations' name
	 * @param values Initial set of values.
	 * @see #Observation(String)
	 * @see #addValue(double)
	 */
	public Observation(String name, Collection<Double> values) {
		this(name);
		for(Double d: values) {
			addValue(d);
		}
		resetIndex = values.size()-1;
	}
	
	/**
	 * Creates an observation with the given update behavior and initial set of values.<br>
	 * @param values Initial set of values.
	 * @param alwaysUpToDate The observations' update behavior
	 * @see #Observation(String, Collection, boolean)
	 */
	public Observation(Collection<Double> values, boolean alwaysUpToDate) {
		this(DEFAULT_NAME, values, alwaysUpToDate);
	}
	
	/**
	 * Creates an observation with the given name, update behavior and initial set of values.<br>
	 * @param name The observations' name
	 * @param values Initial set of values.
	 * @param alwaysUpToDate The observations' update behavior
	 * @see #Observation(String, Collection)
	 * @see #update()
	 */
	public Observation(String name, Collection<Double> values, boolean alwaysUpToDate) {
		this(name, values);
		this.alwaysUpToDate = alwaysUpToDate;
		if(alwaysUpToDate)
			update();
	}
	
	//-----GETTER + SETTER--------------------------------------------------------------------------
	
	/**
	 * Sets the standard precision used for String conversions of inserted values.<br>
	 * @param format The String format for inserted values
	 */
	public void setStandardPrecision(int precision) {
		if(precision < 0)
			throw new IllegalArgumentException("Floating-point format expected.");
		this.standardPrecision = precision;
	}
	
	/**
	 * Sets the observations' update behavior.<br>
	 * If <code>true</code> the expectation and all moments are recalculated after every value insertion.
	 * @param alwaysUpToDate 
	 * @see #alwaysUpToDate
	 */
	public void setAlwaysUpToDate(boolean alwaysUpToDate) {
		this.alwaysUpToDate = alwaysUpToDate;
	}

	/**
	 * Returns the observations' update behavior.<br>
	 * If <code>true</code> the expectation and all moments are recalculated after every value insertion.
	 * @return The observations' update behavior
	 * @see #alwaysUpToDate
	 */
	public boolean isAlwaysUpToDate() {
		return alwaysUpToDate;
	}
	
	/**
	 * Returns the minimal inserted value.
	 * @return The minimal inserted value
	 * @see #minimum
	 */
	public double getMinimum() {
		return minimum;
	}
	
	/**
	 * Returns the maximal inserted value.
	 * @return The maximal inserted value
	 * @see #maximum
	 */
	public double getMaximum() {
		return maximum;
	}
	
	/**
	 * Returns the degrees of the moments that are calculated.
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 * @return The degrees of the moments that are calculated.
	 * @see #momentDegrees
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<Integer> getMomentDegrees() {
		return Collections.unmodifiableCollection(momentDegrees);
	}
	
	/**
	 * Sets the degrees of the moments that are calculated using a collection of integers.<br>
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 * If there are calculated moments a recalculation is triggered.
	 * @param degrees The degrees of the moments that are calculated.
	 * @see #momentDegrees
	 */
	public void setMomentDegrees(Collection<Integer> degrees){
		momentDegrees = degrees;
		if(!moments.isEmpty())
			setMoments();
	}

	/**
	 * Sets the degrees of the moments that are calculated using integer varargs.<br>
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 * If there are calculated moments a recalculation is triggered.
	 * @param degrees The degrees of the moments that are calculated.
	 * @see #momentDegrees
	 * @see #setMomentDegrees(Collection)
	 */
	public void setMomentDegrees(Integer... degrees) {
		setMomentDegrees(Arrays.asList(degrees));
	}
	
	/**
	 * Returns the number of inserted values.
	 * @return The number of inserted values
	 * @see #observationCount
	 */
	public double getObservationCount() {
		return observationCount;
	}
	
	/**
	 * Returns the number of occurrences of the given value.
	 * @param value The value for which the number of occurrences is desired
	 * @return The number of occurrences of the given value
	 * @throws IllegalArgumentException if there are no occurrences of {@link value}
	 */
	public Integer getOccurrencesOf(Double value) {
		if(!insertStat.containsKey(value))
			throw new IllegalArgumentException("No occurrences of value \""+value+"\"");
		return insertStat.get(value);
	}
	
	/**
	 * Returns the number of distinct values amongst all inserted values.
	 * @return The number of distinct values
	 */
	public Set<Double> getDistinctValues(){
		return insertStat.keySet();
	}
	
	/**
	 * Returns the average (arithmetic) of all inserted values.
	 * @return The average (arithmetic) of all inserted values
	 */
	public double getAverage() {
		return average;
	}
	
	/**
	 * Returns the last inserted value.
	 * @return The last inserted value
	 */
	public double getLastValue() {
		return getValueAt(insertSeq.size()-1);
	}
	
	/**
	 * Returns the inserted value of a given insertion step.
	 * @param index Index of the desired insertion step
	 * @return The inserted value of a given insertion step.
	 */
	public double getValueAt(int index) {
		if(index <0 || index>=insertSeq.size())
			throw new IndexOutOfBoundsException();
		return insertSeq.get(index);
	}
	
	/**
	 * Returns a list containing all inserted values.
	 * @return A list containing all inserted values
	 * @see Collections#unmodifiableList(List)
	 * @see #insertSeq
	 */
	public List<Double> getValues(){
		return Collections.unmodifiableList(insertSeq);
	}
	
	/**
	 * Returns the value statistic which consists of a list <br>
	 * containing for every distinct inserted value the number of its occurrences.
	 * @return A statistic of all inserted values
	 * @see Collections#unmodifiableMap(Map)
	 * @see #insertStat
	 */
	public Map<Double, Integer> getValueStatistic(){
		return Collections.unmodifiableMap(insertStat);
	}
	
	/**
	 * Returns the expected value (probabilistic measure).
	 * @return The expected value (probabilistic measure)
	 * @see #expectation
	 */
	public double getExpectation() {
		if(expectation == null)
			setExpectation();
		return expectation;
	}
	
	/**
	 * Returns a map containing all calculated moments (probabilistic measures), accessible by their degree.
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 * @return The calculated moments (probabilistic measure)
	 * @see #moments
	 */
	public HashMap<Integer, Double> getMoments() {
		if(moments.isEmpty())
			setMoments();
		return moments;
	}
	
	//-----FUNCTIONALITY--------------------------------------------------------------------------
	
	/**
	 * Adds a new value to the observation.<br>
	 * The given value is inserted in a list containing sequential inserts ({@link #insertSeq})<br>
	 * and in a map containing distinct values and occurrences ({@link #insertStat}).<br>
	 * The {@link #average}, {@link #maximum} and {@link #minimum} are adjusted,<br>
	 * just like the {@link #expectation} and all {@link #moments} in case the update behavior is set accordingly.
	 * finally it increments the {@link #observationCount}.
	 * @param value New value to be inserted
	 * @see #alwaysUpToDate
	 * @see #update()
	 */
	public void addValue(double value) {
		average = (average*observationCount + value) / (observationCount + 1);
		if(value < minimum)
			minimum = value;
		if(value > maximum)
			maximum = value;
		insertSeq.add(value);
		Integer c = insertStat.get(value);
		if(c == null)
			c = 0;
		insertStat.put(value, ++c);
		observationCount++;
		
		if(alwaysUpToDate)
			update();
	}
	
	/**
	 * Determines the expectation which is a probabilistic measure,<br>
	 * estimating the next inserted value based on all values inserted so far.
	 * @see #expectation
	 */
	protected void setExpectation() {
		expectation = 0.0;
		for(Double d: insertStat.keySet()) {
			expectation += d*(insertStat.get(d)/observationCount);
		}
	}
	
	/**
	 * Determines moments (probabilistic measure) of all degrees specified by {@link #momentDegrees}.
	 * Moments are characteristics of random variables (in this case relating to the inserted values)
	 * <ul>
	 * <li>2nd moment: variance</li>
	 * <li>3rd moment: skewness (Schiefe)</li>
	 * <li>4th moment: kurtosis (Wölbung)</li>
	 * <li>...</li>
	 * </ul>
	 */
	protected void setMoments() {
		moments.clear();
		for(Integer i: momentDegrees)
			moments.put(i, 0.0);
		for(Double d: insertStat.keySet()) {
			for(int j: momentDegrees)
				moments.put(j, moments.get(j)+Math.pow(d-expectation, j)*(insertStat.get(d)/observationCount));
		}
	}
	
	/**
	 * Triggers a recalculation of the expectation and all moments.
	 */
	public void update() {
		setExpectation();
		setMoments();
	}
	
	/**
	 * Resets the observation to the initial state after creation.<br>
	 * Potential changed values for update behavior or moment degrees are kept.
	 */
	public void reset() {
		Double[] init = null;
		if(resetIndex>-1) init = Arrays.copyOfRange(insertSeq.toArray(new Double[resetIndex+1]), 0, resetIndex);
		insertSeq.clear();
		insertStat.clear();
		observationCount = 0.0;
		average = 0.0;
		minimum = Double.MAX_VALUE;
		maximum = Double.MIN_VALUE;
		expectation = null;
		moments.clear();
		if(init != null)
			for(Double value: init) {
				addValue(value);
			}
	}
	
	/**
	 * Returns a String representation of the observation containing all values and characteristics<br>
	 * using identation which is specified by {@link identation} as the number of spaces.
	 * @param identation The number of spaces used for identation
	 * @return A String representation of the observation with identation
	 */
	public String toString(int identation) {
		if(identation <0)
			throw new IllegalArgumentException("identation cannot be <0");
		String header;
		if(identation ==0) header="";
		  else header = String.format(String.format("%%%ss", identation), "");
		StringBuilder builder = new StringBuilder();
		builder.append(header);
		builder.append("[Obs] ");
		builder.append(name);
		builder.append("\n");
		builder.append(header);
		builder.append("   inserts: ");
		builder.append(CollectionUtils.toString(insertSeq, standardPrecision));
		builder.append("\n");
		builder.append(header);
		builder.append("     count: ");
		builder.append((int) getObservationCount());
		builder.append("\n");
		builder.append(header);
		builder.append(" statistic: ");
		builder.append(MapUtils.toString(insertStat, standardPrecision));
		builder.append("\n");
		builder.append(header);
		builder.append("    expect: ");
		builder.append(FormatUtils.format(getExpectation()));
		builder.append("\n");
		builder.append(header);
		builder.append("   moments: ");
		HashMap<Integer, Double> m = getMoments();
		if(m.isEmpty()) {
			builder.append("- \n");
		} else {
			for(Integer i: getMoments().keySet()) {
				builder.append(i);
				builder.append("=");
				builder.append(FormatUtils.format(m.get(i), momentPrecision));
				builder.append(" ");
			}
			builder.append("\n");
		}
		builder.append(header);
		builder.append("   updates: ");
		builder.append(alwaysUpToDate ? "update on insert" : "update on get");
		builder.append("\n");
		return builder.toString();
	}
	
	/**
	 * Returns a String representation of the observation containing all values and characteristics.
	 * @return A String representation of the observation;
	 * @see #toString(String)
	 */
	@Override
	public String toString() {
		return toString(0);
	}

}
