package de.invation.code.toval.misc.valuegeneration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.invation.code.toval.validate.InconsistencyException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


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
	
	private static final String probFormat = "%s: %s%%\n";
	
	private List<E> keys = new ArrayList<E>();  // "a","b","c"
	private List<Double> limits = new ArrayList<Double>(); // 0.2,(0.2+0.3),(0.2+0.3+0.5)
	private Map<E, Double> probabilities = new HashMap<E, Double>(); // ("a",0.2),("b",0.3),("c",0.5)
	private boolean isValid = false;
	private Random rand = new Random();
	private double tolerance;

	/**
	 * Creates a new StochasticChooser.<br>
	 * The tolerance is defined as 1/<code>toleranceDenominator</code>.
	 * @param toleranceDenominator
	 * @throws ParameterException 
	 */
	public StochasticValueGenerator(int toleranceDenominator) throws ParameterException{
		Validate.biggerEqual(toleranceDenominator, 1, "Denominator must be >= 1");
		tolerance = 1.0/toleranceDenominator;
	}
	
	public StochasticValueGenerator() throws ParameterException{
		this(1000);
	}
	
	public void removeElement(Object key){
		if(!keys.contains(key))
			return;
		int index = keys.indexOf(key);
		keys.remove(index);
		limits.remove(index);
		probabilities.remove(index);
		isValid = (1.0-getSum()) <= tolerance;
	}
	
	/**
	 * Adds a new element together with its occurrence probability.<br>
	 * The method checks and sets the validity state of the chooser.
	 * Once valid, no more probabilities are accepted.
	 * @param o Element to add.
	 * @param p Occurrence probability of the given element.
	 * @throws InconsistencyException Thrown if the sum of all maintained probabilities is greater than 1.
	 */
	public boolean addProbability(E o, Double p) throws InconsistencyException, ParameterException {
		if(isValid())
			return false;
		Validate.probability(p);
	
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
		
		isValid = (1.0-getSum()) <= tolerance;
		probabilities.put(o, p);
		return true;
	}
	
	public Double getProbability(Object o){
		try{
			return probabilities.get(o);
		} catch (Exception e){
			return null;
		}
	}
	
	public Set<E> getElements(){
		return new HashSet<E>(keys);
	}
	
	/**
	 * Returns the sum of the given probabilities so far.
	 * @return The sum of all maintained probabilities.
	 */
	private Double getSum(){
		if(limits.isEmpty())
			return 0.0;
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
	 * @throws ValueGenerationException Thrown, if the chooser is in an invalid state.
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class getValueClass() throws InconsistencyException{
		if(keys.isEmpty())
			throw new InconsistencyException("Value generator is empty.");
		return keys.get(0).getClass();
	}
	
	@Override
	public StochasticValueGenerator<E> clone(){
		StochasticValueGenerator<E> result;
		try {
			result = new StochasticValueGenerator<E>();
			for(E element: getElements()){
				result.addProbability(element, getProbability(element));
			}
		} catch (ParameterException e) {
			return null;
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}
	
	@Override
	public String toString(){
		NumberFormat nf = new DecimalFormat("#0.##");
		StringBuilder builder = new StringBuilder();
		for(E e: keys){
			builder.append(String.format(probFormat, e, nf.format(getProbability(e)*100.0)));
		}
		return builder.toString();
	}
	
	
	//public static void main(String[] args) throws ParameterException{  // 如果异常，不易发现
	public static void main(String[] args) {
		StochasticValueGenerator<String> vg = new StochasticValueGenerator<String>(1000);
		try {
			vg.addProbability("a", 0.3);
			vg.addProbability("b", 0.2);
			vg.addProbability("c", 0.5);
		} catch (InconsistencyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(vg);
		
		int numa=0,numb=0,numc=0;
		String v;
		try {
			for(int i=0; i<10; i++) {
			   v = vg.getNextValue();
			   if (v.compareTo("a") == 0) numa++;
			   else if (v.compareTo("b") == 0) numb++;
			   else if (v.compareTo("c") == 0) numc++;
			   System.out.println("next: " + v + ",[a,b,c=]" + numa + "," + numb + "," + numc);
			}
		} catch (ValueGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("a,b,c%=" + numa/10.0 + "," + numb/10.0 + "," + numc/10.0);
		vg.removeElement("b");
		System.out.println(vg);
		System.out.println(vg.isValid());
	}
}
