package de.invation.code.toval.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.invation.code.toval.misc.SetUtils;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * Class for modeling multisets.<br>
 * A multiset can contain contain the same element multiple times.<br>
 * Given a set S, a multiset can be defined as a mapping m : S -> N.<br>
 * The number m(s) is called the multiplicity of s in m.<br>
 * <br>
 * Objects are considered equal if they have the same hash.<br>
 *
 * @author Thomas Stocker
 * @param <O>
 *
 */
public class Multiset<O extends Object> implements Comparable<Multiset<O>> {

	/**
	 * Map for managing the multiplicity of objects.
	 */
	protected Map<O, Integer> multiplicities = new HashMap<>();

	protected Map<Integer, Integer> multiplicityCount = new HashMap<>();

	public Multiset() {
	}

	public Multiset(Collection<O> objects) {
		Validate.notNull(objects);
		for (O o : objects) {
			incMultiplicity(o);
		}
	}

	public Multiset(O... objects) {
		Validate.notNull(objects);
		for (O o : objects) {
			incMultiplicity(o);
		}
	}

	/**
	 * Returns the number of elements, i.e. the sum of all multiplicities.
	 *
	 * @return The number of all elements.
	 */
	public int size() {
		int result = 0;
		for (O o : multiplicities.keySet()) {
			result += multiplicity(o);
		}
		return result;
	}

	/**
	 * Returns the support of this multiset.<br>
	 * The support of a multiset is the set of member objects.
	 *
	 * @return The set of member objects of this multiset.
	 */
	public Set<O> support() {
		return Collections.unmodifiableSet(multiplicities.keySet());
	}

	public boolean equalMultiplicities() {
		return multiplicityCount.isEmpty() || multiplicityCount.keySet().size() == 1;
	}

	public int getEqualMultiplicity() {
		if (isEmpty() || !equalMultiplicities()) {
			return -1;
		}
		return multiplicityCount.keySet().iterator().next();
	}

	/**
	 * Reduces the multiset to an ordinary set, i.e.<br>
	 * it sets the multiplicity of all elements to 1.
	 */
	public void reduceToSet() {
		for (O o : multiplicities.keySet()) {
			setMultiplicity(o, 1);
		}
	}

	/**
	 * Adds an object as a member of this multiset.<br>
	 * The multiplicity is adjusted and returned.
	 *
	 * @param object
	 * @return The multiplicity of the given object within the multiset.
	 * @throws ParameterException
	 */
	public int add(O object) throws ParameterException {
		return incMultiplicity(object);
	}

	/**
	 * Adds all given objects to this multiset.
	 *
	 * @param objects The objects to be added.
	 * @throws ParameterException
	 */
	public void addAll(O... objects) throws ParameterException {
		Validate.notNull(objects);
		for (O o : objects) {
			incMultiplicity(o);
		}
	}

	/**
	 * Removes the given object from this multiset.
	 *
	 * @param object The object to remove.
	 * @return <code>true</code> if the removal was successful;<br>
	 * <code>false</code> if the multiset does not contain the given object.
	 */
	public boolean remove(O object) {
		Integer multiplicity = multiplicities.get(object);
		if (multiplicities.remove(object) == null) {
			return false;
		}
		decMultiplicityCount(multiplicity);
		return true;
	}

	private void decMultiplicityCount(Integer multiplicity) {
//		System.out.println("dec count of mult " + multiplicity);
//		System.out.println(multiplicityCount);
		int actualCount = multiplicityCount.get(multiplicity);
		if (actualCount == 1) {
			multiplicityCount.remove(multiplicity);
		} else {
			multiplicityCount.put(multiplicity, actualCount - 1);
		}
//		System.out.println(multiplicityCount);
	}

	private void incMultiplicityCount(Integer multiplicity) {
//		System.out.println("inc count of mult " + multiplicity);
//		System.out.println(multiplicityCount);
		if (!multiplicityCount.containsKey(multiplicity)) {
			multiplicityCount.put(multiplicity, 1);
		} else {
			multiplicityCount.put(multiplicity, multiplicityCount.get(multiplicity) + 1);
		}
//		System.out.println(multiplicityCount);
	}

	/**
	 * Removes all elements from this multiset.
	 */
	public void clear() {
		multiplicities.clear();
		multiplicityCount.clear();
	}

	public void printMultiplicityCount() {
		System.out.println(multiplicityCount);
	}

	/**
	 * Checks if the support of this multiset contains all given objects.
	 *
	 * @param objects The objects to check.
	 * @return <code>true</code> if the support contains all ojects;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean contains(O... objects) {
		return multiplicities.keySet().containsAll(Arrays.asList(objects));
	}

	/**
	 * Checks if the support of this multiset contains all given objects.
	 *
	 * @param objects The objects to check.
	 * @return <code>true</code> if the support contains all ojects;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean contains(Collection<O> objects) {
		return multiplicities.keySet().containsAll(objects);
	}

	/**
	 * Checks if the support of this multiset contains the given object.
	 *
	 * @param object The object to check.
	 * @return <code>true</code> if the support contains the oject;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean contains(O object) {
		return multiplicities.keySet().contains(object);
	}

	/**
	 * Sets the multiplicity of the given object to the given number.<br>
	 * In case the multiplicity is 0 or negative, the given object is
	 * removed from the multiset.
	 *
	 * @param object The object whose multiplicity is set.
	 * @param multiplicity The multiplicity for the given object to set.
	 */
	public void setMultiplicity(O object, int multiplicity) {
		if (!contains(object) && multiplicity < 1) {
			return;
		}
		if (contains(object) && multiplicity < 1) {
			remove(object);
			return;
		}
		if (multiplicities.containsKey(object)) {
			int oldMultiplicity = multiplicities.get(object);
			decMultiplicityCount(oldMultiplicity);
		}
		incMultiplicityCount(multiplicity);
		multiplicities.put(object, multiplicity);
	}

	/**
	 * Increments the multiplicity of the given object by 1.<br>
	 * In case the multiset does not contain the object, it is inserted with
	 * multiplicity 1.
	 *
	 * @param object The object for which the multiplicity should be
	 * incremented.
	 * @return
	 * @throws ParameterException
	 */
	public final int incMultiplicity(O object) throws ParameterException {
		Validate.notNull(object);
		if (!multiplicities.containsKey(object)) {
			multiplicities.put(object, 1);
			incMultiplicityCount(1);
		} else {
			Integer oldMultiplicity = multiplicities.get(object);
			multiplicities.put(object, oldMultiplicity + 1);
			decMultiplicityCount(oldMultiplicity);
			incMultiplicityCount(oldMultiplicity + 1);
		}
		return multiplicity(object);
	}

	/**
	 * Decrements the multiplicity of the given object by 1.<br>
	 * In case the multiplicity gets 0, the object is completely removed.
	 *
	 * @param object The object for which the multiplicity should be
	 * decremented.
	 * @return
	 * @throws ParameterException
	 */
	public int decMultiplicity(O object) throws ParameterException {
		Validate.notNull(object);
		if (!multiplicities.containsKey(object)) {
			return 0;
		}
		if (multiplicity(object) > 1) {
			Integer oldMultiplicity = multiplicities.get(object);
			multiplicities.put(object, oldMultiplicity - 1);
			decMultiplicityCount(oldMultiplicity);
			incMultiplicityCount(oldMultiplicity - 1);
			return multiplicity(object);
		} else {
			remove(object);
			return 0;
		}
	}

	/**
	 * Returns the multiplicity for the given object.<br>
	 *
	 * @param object The object for which the multiplicity is desired.
	 * @return The multiplicity for the given object;<br>
	 * 0 if there is no multiplicity for the given object.
	 * @throws ParameterException
	 */
	public int multiplicity(O object) {
		if (multiplicities.containsKey(object)) {
			return multiplicities.get(object);
		}
		return 0;
	}

	public int minMultiplicity() {
		return Collections.min(multiplicities.values());
	}

	/**
	 * Returns the power of the multiset.<br>
	 * The power of a multiset is determined by the highest multiplicity for
	 * an object.
	 *
	 * @return The power of this multiset.
	 */
	public Integer power() {
		if (isEmpty()) {
			return null;
		}
		return Collections.max(multiplicities.values());
	}

	/**
	 * checks if this multiset is empty, i.e. contains no objects with
	 * multiplicity > 0.
	 *
	 * @return <code>true</code> if this multiset is empty;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return multiplicities.isEmpty();
	}

	/**
	 * Checks if this multiset id k-bounded.<br>
	 * A multiset is k-bounded if there is no object whose multiplicity is
	 * greater than k.
	 *
	 * @param k The maximum multiplicity of an object.
	 * @return <code>true</code> if the multiset is k-bounded;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException
	 */
	public boolean isKBounded(int k) {
		if (isEmpty()) {
			return true;
		}
		return power() <= k;
	}

	/**
	 * Scalar addition.<br>
	 * Adds k to the multiplicity of all objects.<br>
	 * In case the multiplicity of an object falls below 1, it is removed.
	 *
	 * @param k The value to add.
	 */
	public void addScalar(int k) {
		Set<O> remove = new HashSet<>();
		for (O o : multiplicities.keySet()) {
			setMultiplicity(o, multiplicity(o) + k);
			if (multiplicity(o) < 1) {
				remove.add(o);
			}
		}
		for (O o : remove) {
			remove(o);
		}
	}

	/**
	 * Scalar multiplication.<br>
	 * Multiplies the multiplicity of all objects with k.<br>
	 * In case the multiplicity of an object falls below 1, it is removed.
	 *
	 * @param k The multiplicator.
	 */
	public void multiplyScalar(int k) {
		Set<O> remove = new HashSet<>();
		for (O o : multiplicities.keySet()) {
			setMultiplicity(o, multiplicity(o) * k);
			if (multiplicity(o) < 1) {
				remove.add(o);
			}
		}
		for (O o : remove) {
			remove(o);
		}
	}

	/**
	 * Checks if this multiset is a subset of the given multiset.<br>
	 * A multiset m1 is a subset of another multiset m2 if for every object
	 * o:<br>
	 * m1.multiplicity(o) <= m2.multiplicity(o). @param multiset T
	 *
	 * he given multiset. @param multiset @return <code>true</code> if this
	 * multiset
	 *
	 * i
	 * s a subset of the given multiset;<br>
	 * <code>false</code> otherwise.
	 * @throws ParameterException
	 */
	public boolean isSubsetOf(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		for (O o : multiplicities.keySet()) {
			if (multiplicity(o) > multiset.multiplicity(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the multiset that results from the combination of this
	 * multiset and the give nmultiset.<br>
	 * This operation does not change this multiset.
	 *
	 * @param multiset The multiset to combine with this multiset.
	 * @return The union multiset.
	 * @throws ParameterException
	 */
	@SuppressWarnings("unchecked")
	public Multiset<O> union(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<>();
		for (O o : SetUtils.union(support(), multiset.support())) {
			result.setMultiplicity(o, Math.max(multiplicity(o), multiset.multiplicity(o)));
		}
		return result;
	}

	/**
	 * Combines this multiset with the given multiset.<br>
	 * This operation changes only this multiset, not the given one.
	 *
	 * @param multiset The multiset to include.
	 * @throws ParameterException
	 */
	public void unionWith(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		for (O o : multiset.support()) {
			setMultiplicity(o, Math.max(multiplicity(o), multiset.multiplicity(o)));
		}
	}

	/**
	 * Returns the multiset that results from intersecting this multiset
	 * with the given multiset.<br>
	 * This operation does not change this multiset.
	 *
	 * @param multiset The multiset to intersect with.
	 * @return The intersection multiset.
	 * @throws ParameterException
	 */
	public Multiset<O> intersection(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<>();
		for (O o : support()) {
			if (multiset.multiplicity(o) > 0) {
				result.setMultiplicity(o, Math.min(multiplicity(o), multiset.multiplicity(o)));
			}
		}
		return result;
	}

	/**
	 * Intersects this multiset with the given multiset.<br>
	 * This operation changes only this multiset, not the given one.
	 *
	 * @param multiset The multiset to intersect with.
	 * @throws ParameterException
	 */
	public void intersectionWith(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		for (O o : support()) {
			if (multiset.multiplicity(o) > 0) {
				setMultiplicity(o, Math.min(multiplicity(o), multiset.multiplicity(o)));
			} else {
				remove(o);
			}
		}
	}

	/**
	 * Returns the multiset resulting from subtracting the given multiset
	 * from the multiset.<br>
	 * This operation does not change this multiset.
	 *
	 * @param multiset The multiset to subtract.
	 * @return The multiset resulting from subtraction.
	 * @throws ParameterException
	 */
	public Multiset<O> difference(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<>();
		for (O o : support()) {
			if (multiset.multiplicity(o) > 0) {
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
	 *
	 * @param multiset The multiset to subtract.
	 * @throws ParameterException
	 */
	public void differenceWith(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		for (O o : support()) {
			if (multiset.multiplicity(o) > 0) {
				setMultiplicity(o, Math.max(0, multiplicity(o) - multiset.multiplicity(o)));
			}
		}
	}

	/**
	 * Returns the multiset resulting from adding the given multiset to the
	 * multiset.<br>
	 * This operation does not change this multiset.
	 *
	 * @param multiset The multiset to add.
	 * @return The multiset resulting from addition.
	 * @throws ParameterException
	 */
	@SuppressWarnings("unchecked")
	public Multiset<O> sum(Multiset<O> multiset) throws ParameterException {
		Validate.notNull(multiset);
		Multiset<O> result = new Multiset<>();
		for (O o : SetUtils.union(support(), multiset.support())) {
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Multiset)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		Multiset other = (Multiset) obj;
		if (multiplicities == null) {
			if (other.multiplicities != null) {
				return false;
			}
		} else if (!multiplicities.equals(other.multiplicities)) {
			return false;
		}
		return true;
	}

	@Override
	public Multiset<O> clone() {
		Multiset<O> result = new Multiset<>();
		for (O o : support()) {
			result.setMultiplicity(o, multiplicity(o));
		}
		return result;
	}

	@Override
	public int compareTo(Multiset<O> o) {
		return size() - o.size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		int count = 0;
		List<O> supportList = new ArrayList<>(multiplicities.keySet());
		Collections.sort(supportList, new ElementComparator<>());
		for (O object : multiplicities.keySet()) {
			int mult = multiplicities.get(object);
			builder.append(object);
			if (mult > 1) {
				builder.append("(").append(mult).append(")");
			}
			if (++count < multiplicities.keySet().size()) {
				builder.append(", ");
			}
		}
		builder.append('}');
		return builder.toString();
//		return multiplicities.toString();
	}

	private class ElementComparator<OO> implements Comparator<OO> {

		@Override
		public int compare(OO o1, OO o2) {
			if (o1 instanceof Number && o2 instanceof Number) {
				Double o1Number = ((Number) o1).doubleValue();
				Double o2Number = ((Number) o2).doubleValue();
				return o1Number.compareTo(o2Number);
			} else {
				return o1.toString().compareTo(o2.toString());
			}
		}
	}

	public static <T extends Multiset<?>> String toString(Collection<T> multisets, String collName) {
		StringBuilder builder = new StringBuilder();
		List<T> multisetList = new ArrayList<>(multisets);
		Collections.sort(multisetList);
		builder.append(collName);
		builder.append(" {");
//		String fill = StringUtils.createString(' ', collName.length()+2);
		builder.append('\n');
		for (T multiset : multisetList) {
//			builder.append(fill);
			builder.append(multiset);
			builder.append('\n');
		}
//		builder.append(fill);
		builder.append('}');
		builder.append('\n');
		return builder.toString();

	}

	public static void main(String[] args) throws ParameterException {
		Multiset<Integer> m = new Multiset<>();
		System.out.println(m.equalMultiplicities());
		m.incMultiplicity(1);
		System.out.println(m.equalMultiplicities());
		m.incMultiplicity(1);
		System.out.println(m.equalMultiplicities());
		m.incMultiplicity(2);
		System.out.println(m.equalMultiplicities());
		m.decMultiplicity(1);
		System.out.println(m);
		System.out.println(m.equalMultiplicities());
		m.setMultiplicity(4, 1);
		System.out.println(m.equalMultiplicities());
		m.setMultiplicity(4, 2);
		System.out.println(m.equalMultiplicities());
	}

}
