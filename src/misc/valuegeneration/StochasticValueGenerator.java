package misc.valuegeneration;

import java.util.ArrayList;
import java.util.Random;

import validate.InconsistencyException;
import validate.ParameterException;
import validate.Validate;

/**
 * This class allows to choose elements according to given occurrence probabilities.
 * It maintains a number of elements and stochastically chooses an element
 * on demand based on the given probability.
 * 
 * Occurrence probabilities for maintained elements must sum up to 1,
 * otherwise the state of this chooser is invalid and no choices are made.<br>
 * To handle rounding errors, a tolerance t is defined.<br>
 * The difference between the sum of all probabilities and 1 must not exceed t.
 * <p>
 * @author Thomas Stocker
 * @param <E> element Type
 */
public class StochasticValueGenerator<E> implements ValueGenerator<E>{
	
	private ArrayList<E> keys = new ArrayList<E>();
	private ArrayList<Double> limits = new ArrayList<Double>();
	private boolean isValid = false;
	private Random rand = new Random();
	private double tolerance;

	/**
	 * Creates a new StochasticChooser.<br>
	 * The tolerance is defined as 1/{@link toleranceDenominator}.
	 * @param toleranceDenominator
	 * @throws ParameterException 
	 */
	public StochasticValueGenerator(int toleranceDenominator) throws ParameterException{
		Validate.biggerEqual(toleranceDenominator, 1, "Denominator must be >= 1");
		tolerance = 1.0/toleranceDenominator;
	}
	
	/**
	 * Adds a new element together with its occurrence probability.<br>
	 * The method checks and sets the validity state of the chooser.
	 * Once valid, no more probabilities are accepted.
	 * @param o Element to add.
	 * @param p Occurrence probability of the given element.
	 * @throws InconsistencyException, ParameterException 
	 * @throws Exception Thrown if the sum of all maintained probabilities is greater than 1.
	 */
	public boolean addProbability(E o, Double p) throws InconsistencyException, ParameterException {
		if(isValid())
			return false;
		Validate.notNull(p);
		Validate.notNegative(p);
		if(keys.isEmpty()){
			keys.add(o);
			limits.add(p);
		} else {
			if((getSum() + p) > 1.0+tolerance){
				throw new InconsistencyException("Probabilities must sum up to 1 ("+(getSum() + p)+" instead).");
			}
			keys.add(o);
			limits.add(getSum() + p);
		}
		if((1.0-getSum())<=tolerance)
			isValid = true;
		return true;
	}
	
	/**
	 * Returns the sum of the given probabilities so far.
	 * @return The sum of all maintained probabilities.
	 */
	private Double getSum(){
		return limits.get(limits.size()-1);
	}
	
	/**
	 * Returns the state of this element chooser.
	 * If all maintained probabilities sum up to 1, the state of this chooser is called valid.
	 * Otherwise it is incomplete (invalid) and refuses to conduct element choices.
	 * @return <code>true</code> if this chooser is valid,<br>
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean isValid(){
		return isValid;
	}
	
	/**
	 * Conducts a stochastic element choice based on the maintained occurrence probabilities.
	 * @return A randomly chosen element based on occurrence probabilities.
	 * @throws Exception Thrown, if the chooser is in an invalid state.
	 */
	public E getNextValue() throws ValueGenerationException{
		if(!isValid())
			throw new ValueGenerationException("Cannot provide elements in invalid state.");
		Double random = rand.nextDouble();
		for(int i=0; i<limits.size(); i++)
			if(random <= limits.get(i)){
				return keys.get(i);
			}
		return null;
	}
	
	@Override
	public Class getValueType() throws InconsistencyException{
		if(!isValid())
			throw new InconsistencyException("Cannot provide elements in invalid state.");
		return keys.get(0).getClass();
	}
	
}
