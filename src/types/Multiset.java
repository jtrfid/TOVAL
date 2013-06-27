package types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.invation.code.toval.misc.CollectionUtils;
import de.invation.code.toval.misc.SetUtils;

import validate.ParameterException;
import validate.ParameterException.ErrorCode;
import validate.Validate;


/**
 * Class for modeling multisets.<br>
 * A multiset can contain contain the same element multiple times.<br>
 * Given a set S, a multiset can be defined as a mapping m : S -> N.<br>
 * The number m(s) is called the multiplicity of s in m.<br>
 * <br>
 * Objects are considered equal if they have the same hash.<br>
 * 
 * @author Thomas Stocker
 *
 */
public class Multiset<O extends Object> {
	
	/**
	 * Map for managing the multiplicity of objects.
	 */
	protected Map<O,Integer> multiplicities= new HashMap<O,Integer>();
	
	/**
	 * Creates a new empty multiset.
	 */
	public Multiset(){}
	
	public Multiset(O... objects) throws ParameterException{
		Validate.notEmpty(objects);
		for(O o: objects)
			incMultiplicity(o);
	}
	
	/**
	 * Returns the number of elements, i.e. the sum of all multiplicities.
	 * @return The number of all elements.
	 */
	public int size(){
		int result = 0;
		for(O o: multiplicities.keySet()){
			try {
				result += multiplicity(o);
			} catch (ParameterException e) {}
		}
		return result;
	}
	
	/**
	 * Returns the support of this multiset.<br>
	 * The support of a multiset is the set of member objects.
	 * @return The set of member objects of this multiset.
	 */
	public Set<O> support(){
		return multiplicities.keySet();
	}
	
	/**
	 * Reduces the multiset to an ordinary set, i.e.<br>
	 * it sets the multiplicity of all elements to 1.
	 */
	public void reduceToSet(){
		for(O o: multiplicities.keySet()){
			setMultiplicity(o, 1);
		}
	}
	
	/**
	 * Adds an object as a member of this multiset.<br>
	 * The multiplicity is adjusted and returned.
	 * @return The multiplicity of the given object within the multiset.
	 * @throws ParameterException 
	 */
	public int add(O object) throws ParameterException{
		return incMultiplicity(object);
	}
	
	/**
	 * Adds all given objects to this multiset.
	 * @param objects The objects to be added.
	 * @throws ParameterException 
	 */
	public void addAll(O... objects) throws ParameterException{
		Validate.notNull(objects);
		for(O o: objects){
			incMultiplicity(o);
		}
	}
	
	/**
	 * Removes the given object from this multiset.
	 * @param object The object to remove.
	 * @return <code>true</code> if the removal was successful;<br>
	 * <code>false</code> if the multiset does not contain the given object.
	 */
	public boolean remove(O object){
		if(multiplicities.remove(object) == null)
			return false;
		return true;
	}
	
	/**
	 * Removes all elements from this multiset.
	 */
	public void clear(){
		multiplicities.clear();
	}
	
	/**
	 * Checks if the support of this multiset contains all given objects.
	 * @param objects The objects to check.
	 * @return <code>true</code> if the support contains all ojects;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean contains(O... objects){
		return multiplicities.keySet().containsAll(Arrays.asList(objects));
	}
	
	/**
	 * Checks if the support of this multiset contains all given objects.
	 * @param objects The objects to check.
	 * @return <code>true</code> if the support contains all ojects;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean contains(Collection<O> objects){
		return multiplicities.keySet().containsAll(objects);
	}
	
	/**
	 * Sets the multiplicity of the given object to the given number.<br>
	 * In case the multiplicity is 0 or negative, the given object is removed from the multiset.
	 * @param object The object whose multiplicity is set.
	 * @param multiplicity The multiplicity for the given object to set.
	 */
	@SuppressWarnings("unchecked")
	public void setMultiplicity(O object, int multiplicity) {
		if(!contains(object) && multiplicity < 1){
			return;
		}
		if(contains(object) && multiplicity < 1){
			remove(object);
			return;
		}
		multiplicities.put(object, multiplicity);
	}
	
	/**
	 * Increments the multiplicity of the given object by 1.<br>
	 * In case the multiset does not contain the object,
	 * it is inserted with multiplicity 1.
	 * @param object The object for which the multiplicity should be incremented.
	 * @throws ParameterException 
	 */
	public int incMultiplicity(O object) throws ParameterException{
		Validate.notNull(object);
		if(!multiplicities.containsKey(object)){
			multiplicities.put(object, 1);
		} else {
			multiplicities.put(object, multiplicities.get(object) + 1);
		}
		return multiplicity(object);
	}
	
	/**
	 * Decrements the multiplicity of the given object by 1.<br>
	 * In case the multiplicity gets 0, the object is completely removed.
	 * @param object The object for which the multiplicity should be decremented.
	 * @throws ParameterException 
	 */
	public int decMultiplicity(O object) throws ParameterException{
		Validate.notNull(object);
		if(!multiplicities.containsKey(object))
			return 0;
		if(multiplicity(object) > 1){
			multiplicities.put(object, multiplicities.get(object) - 1);
			return multiplicity(object);
		} else {
			multiplicities.remove(object);
			return 0;
		}
	}
	
	/**
	 * Returns the multiplicity for the given object.<br>
	 * @param object The object for which the multiplicity is desired.
	 * @return The multiplicity for the given object;<br> 
	 * 0 if there is no multiplicity for the given object.
	 * @throws ParameterException 
	 */
	public int multiplicity(O object) throws ParameterException{
		Validate.notNull(object);
		if(multiplicities.containsKey(object))
			return multiplicities.get(object);
		return 0;
	}
	
	public int minMultiplicity(){
		return Collections.min(multiplicities.values());
	}
	
	/**
	 * Returns the power of the multiset.<br>
	 * The power of a multiset is determined by the highest multiplicity for an object.
	 * @return The power of this multiset.
	 */
	public Integer power(){
		if(isEmpty()){
			return null;
		}
		return Collections.max(multiplicities.values());
	}
	
	/**
	 * checks if this multiset is empty, i.e. contains no objects with multiplicity > 0.
	 * @return <code>true</code> if this multiset is empty;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty(){
		return multiplicities.isEmpty();
	}
	
	/**
	 * Checks if this multiset is trivial.<br>
	 * A multiset is considered trivial if the multiplicity for every object s of a related set S is equal.
	 * @param basicSet The basic set for operation
	 * @return <code>true</code> if the multiset is trivial;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException 
	 */
	public boolean isTrivial(Collection<O> basicSet) throws ParameterException{
		Validate.notNull(basicSet);
		Validate.notEmpty(basicSet);
		Validate.noNullElements(basicSet);
		if(!basicSet.containsAll(multiplicities.keySet()))
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Incompatible set");
		
		if(!multiplicities.keySet().containsAll(basicSet))
			return false;
		return CollectionUtils.isTrivial(multiplicities.values());
	}
	
	/**
	 * Checks if this multiset id k-bounded.<br>
	 * A multiset is k-bounded if there is no object whose multiplicity is greater than k.
	 * @param k The maximum multiplicity of an object.
	 * @return <code>true</code> if the multiset is k-bounded;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException 
	 */
	public boolean isKBounded(int k) {
		if(isEmpty()){
			return true;
		}
		return power() <= k;
	}
	
	/**
	 * Scalar addition.<br>
	 * Adds k to the multiplicity of all objects.<br>
	 * In case the multiplicity of an object falls below 1, it is removed.
	 * @param k The value to add.
	 */
	public void addScalar(int k){
		Set<O> remove = new HashSet<O>();
		for(O o: multiplicities.keySet()){
			try {
				multiplicities.put(o, multiplicity(o) + k);
				if(multiplicity(o) < 1){
					remove.add(o);
				}
			} catch (ParameterException e) {
				// Cannot happen, since the objects are taken from the keyset.
				e.printStackTrace();
			}
		}
		multiplicities.keySet().removeAll(remove);
	}
	
	/**
	 * Scalar multiplication.<br>
	 * Multiplies the multiplicity of all objects with k.<br>
	 * In case the multiplicity of an object falls below 1, it is removed.
	 * @param k The multiplicator.
	 */
	public void multiplyScalar(int k){
		Set<O> remove = new HashSet<O>();
		for(O o: multiplicities.keySet()){
			multiplicities.put(o, multiplicities.get(o) * k);
			try {
				if(multiplicity(o) < 1){
					remove.add(o);
				}
			} catch (ParameterException e) {
				// Cannot happen, since the objects are taken from the keyset.
				e.printStackTrace();
			}
		}
		multiplicities.keySet().removeAll(remove);
	}
	
	/**
	 * Checks if this multiset is a subset of the given multiset.<br>
	 * A multiset m1 is a subset of another multiset m2 if for every object o:<br>
	 * m1.multiplicity(o) <= m2.multiplicity(o).
	 * @param multiset The given multiset.
	 * @return <code>true</code> if this multiset is a subset of the given multiset;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException 
	 */
	public boolean isSubsetOf(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		for(O o: multiplicities.keySet()){
			if(multiplicity(o) > multiset.multiplicity(o))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the multiset that results from the combination of this multiset and the give nmultiset.<br>
	 * This operation does not change this multiset.
	 * @param multiset The multiset to combine with this multiset.
	 * @return The union multiset.
	 * @throws ParameterException 
	 */
	@SuppressWarnings("unchecked")
	public Multiset<O> union(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<O>();
		for(O o: SetUtils.union(support(), multiset.support())){
			result.setMultiplicity(o, Math.max(multiplicity(o), multiset.multiplicity(o)));
		}
		return result;
	}
	
	/**
	 * Combines this multiset with the given multiset.<br>
	 * This operation changes only this multiset, not the given one.
	 * @param multiset The multiset to include.
	 * @throws ParameterException 
	 */
	public void unionWith(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		for(O o: multiset.support()){
			setMultiplicity(o, Math.max(multiplicity(o), multiset.multiplicity(o)));
		}
	}
	
	/**
	 * Returns the multiset that results from intersecting this multiset with the given multiset.<br>
	 * This operation does not change this multiset.
	 * @param multiset The multiset to intersect with.
	 * @return The intersection multiset.
	 * @throws ParameterException 
	 */
	public Multiset<O> intersection(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<O>();
		for(O o: support()){
			if(multiset.multiplicity(o)>0){
				result.setMultiplicity(o, Math.min(multiplicity(o), multiset.multiplicity(o)));
			}
		}
		return result;
	}
	
	/**
	 * Intersects this multiset with the given multiset.<br>
	 * This operation changes only this multiset, not the given one.
	 * @param multiset The multiset to intersect with.
	 * @throws ParameterException 
	 */
	public void intersectionWith(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		for(O o: support()){
			if(multiset.multiplicity(o)>0){
				setMultiplicity(o, Math.min(multiplicity(o), multiset.multiplicity(o)));
			} else {
				multiplicities.remove(o);
			}
		}
	}
	
	/**
	 * Returns the multiset resulting from subtracting the given multiset from the multiset.<br>
	 * This operation does not change this multiset.
	 * @param multiset The multiset to subtract.
	 * @return The multiset resulting from subtraction.
	 * @throws ParameterException 
	 */
	public Multiset<O> difference(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<O>();
		for(O o: support()){
			if(multiset.multiplicity(o) > 0){
				result.setMultiplicity(o, Math.max(0, multiplicity(o) - multiset.multiplicity(o)));
			} else {
				result.setMultiplicity(o, multiplicity(o));
			}
		}
		return result;
	}
	
	
	/**
	 * Subtracts the given multiset from this multiset.<br>
	 * This operation changes only this multiset, not the given one.
	 * @param multiset The multiset to subtract.
	 * @throws ParameterException 
	 */
	public void differenceWith(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		for(O o: support()){
			if(multiset.multiplicity(o)>0){
				setMultiplicity(o, Math.max(0, multiplicity(o) - multiset.multiplicity(o)));
			}
		}
	}
	
	/**
	 * Returns the multiset resulting from adding the given multiset to the multiset.<br>
	 * This operation does not change this multiset.
	 * @param multiset The multiset to add.
	 * @return The multiset resulting from addition.
	 * @throws ParameterException 
	 */
	@SuppressWarnings("unchecked")
	public Multiset<O> sum(Multiset<O> multiset) throws ParameterException{
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<O>();
		for(O o: SetUtils.union(support(), multiset.support())){
			result.setMultiplicity(o, multiplicity(o) + multiset.multiplicity(o));
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((multiplicities == null) ? 0 : multiplicities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Multiset))
			return false;
		@SuppressWarnings("rawtypes")
		Multiset other = (Multiset) obj;
		if (multiplicities == null) {
			if (other.multiplicities != null)
				return false;
		} else if (!multiplicities.equals(other.multiplicities))
			return false;
		return true;
	}
	
	@Override
	public Multiset<O> clone(){
		Multiset<O> result = new Multiset<O>();
		for(O o: support()){
			try {
				result.setMultiplicity(o, multiplicity(o));
			} catch (ParameterException e) {
				// Cannot happen, since the objects are taken from the keyset.
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@Override
	public String toString(){
		return multiplicities.toString();
	}

}
