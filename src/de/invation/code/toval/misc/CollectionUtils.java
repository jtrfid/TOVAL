package de.invation.code.toval.misc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import de.invation.code.toval.reflect.GenericReflection;

public class CollectionUtils {

	/**
	 * String for value separation.<br>
	 * Used for generating String representations of collections.
	 */
	public static final String VALUE_SEPARATION = ", ";

	public static final String DEFAULT_START = "[";
	public static final String DEFAULT_END = "]";
	/**
	 * Default precision used for String representations of collection elements
	 * having type <code>Float</code> or <code>Double</code>.
	 */
	public static final int DEFAULT_PRECISION = 2;
	/**
	 * String representation for empty arrays.
	 */
	public static final String EMPTY_COLLECTION = "[]";

	/**
	 * Checks if the given basic list contains any element of the given value
	 * list.
	 * 
	 * @param <T>
	 *            Type of collection elements
	 * @param baseList
	 *            List to check for occurrences
	 * @param valueList
	 *            Values to search
	 * @return <code>true</code> if <code>baseList</code> contains any element
	 *         of <code>valueList</code>; <code>false</code> otherwise
	 */
	public static <T> boolean containsNo(Collection<T> baseList, Collection<T> valueList) {
		for (T v : valueList) {
			if (baseList.contains(v)) {
				return false;
			}
		}
		return true;
	}

	public static <T> List<List<T>> randomPartition(Collection<T> coll, int number) {
		return ListUtils.randomPartition(new ArrayList<T>(coll), number);
	}

	public static <T> List<List<T>> exponentialPartition(Collection<T> coll, int number) {
		return ListUtils.exponentialPartition(new ArrayList<T>(coll), number);
	}

	/**
	 * Returns a String representation of a Collection.<br>
	 * 
	 * @param coll
	 *            Collection for String representation
	 * @return String representation of <code>coll</code>
	 * @see CollectionUtils#getFormat(Collection, int, String, String, String)
	 */
	public static String toString(Collection<?> coll) {
		return toString(coll, DEFAULT_PRECISION);
	}

	/**
	 * Returns a String representation of a Collection.<br>
	 * The specified precision is only applicable for <code>Float</code> and
	 * <code>Double</code> elements.
	 * 
	 * @param coll
	 *            Collection for String representation
	 * @param precision
	 *            Desired precision for <code>Float</code> and
	 *            <code>Double</code> elements
	 * @return String representation of <code>coll</code>
	 * @see CollectionUtils#getFormat(Collection, int, String, String, String)
	 */
	public static String toString(Collection<?> coll, int precision) {
		if (coll == null)
			throw new NullPointerException();
		if (coll.isEmpty())
			return EMPTY_COLLECTION;
		return String.format(getFormat(coll, precision), coll.toArray());
	}

	/**
	 * Returns a format-String that can be used to generate a String
	 * representation of a collection using the String.format method.
	 * 
	 * @param coll
	 *            Collection for which a String representation is desired
	 * @param precision
	 *            Desired precision for <code>Float</code> and
	 *            <code>Double</code> elements
	 * @return Format-String for <code>coll</code>
	 * @see Formatter
	 * @see String#format(String, Object...)
	 */
	private static String getFormat(Collection<?> coll, int precision) {
		return getFormat(coll, precision, VALUE_SEPARATION, "[", "]");
	}

	public static String getFormat(Collection<?> coll, int precision, String valueSeparation, String start,
			String end) {
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		int c = 0;
		for (Object o : coll) {
			c++;
			builder.append(FormatUtils.getFormat(o, precision));
			if (c < coll.size())
				builder.append(valueSeparation);
		}
		builder.append(end);
		return builder.toString();
	}

	public static String toSimpleString(Collection<?> coll) {
		if (coll == null)
			throw new NullPointerException();
		if (coll.isEmpty())
			return EMPTY_COLLECTION;
		return String.format(getFormat(coll, 0, " ", "", ""), coll.toArray());
	}

	public static String toString(Collection<?> coll, String valueSeparation, String start, String end) {
		if (coll == null)
			throw new NullPointerException();
		if (coll.isEmpty())
			return EMPTY_COLLECTION;
		return String.format(getFormat(coll, 0, valueSeparation, start, end), coll.toArray());
	}

	public static <T> void print(Collection<T> coll) {
		if (coll == null)
			throw new NullPointerException();
		if (coll.isEmpty())
			return;
		for (T t : coll) {
			System.out.println(t);
		}
	}

	/**
	 * Checks if the given list is trivial.<br>
	 * A list is considered trivial if all its elements are equal (according to
	 * T.equals()).
	 * 
	 * @param <T>
	 *            List type.
	 * @param coll
	 *            Basic list for operation.
	 * @return <code>true</code> if this list is trivial;<br>
	 *         <code>false</code> otherwise.
	 */
	public static <T> boolean isTrivial(Collection<T> coll) {
		if (coll == null)
			throw new NullPointerException();
		if (coll.isEmpty() || coll.size() == 1)
			return true;
		Iterator<T> iterator = coll.iterator();
		T firstElement = iterator.next();
		while (iterator.hasNext()) {
			if (!firstElement.equals(iterator.next()))
				return false;
		}
		return true;
	}

	public static <T> boolean containSameElements(Collection<Collection<T>> collections) {
		if (collections.isEmpty() || collections.size() < 2)
			return false;
		Iterator<Collection<T>> iterator = collections.iterator();
		Collection<T> basicSet = iterator.next();
		Collection<T> actualSet;
		while (iterator.hasNext()) {
			actualSet = iterator.next();
			if (actualSet.size() != basicSet.size())
				return false;
			if (!actualSet.containsAll(basicSet))
				return false;
		}
		return true;
	}

	public static <T> boolean containSameElements(Collection<T>... sets) {
		if (sets.length == 0 || sets.length < 2)
			return false;
		Collection<T> basicSet = sets[0];
		for (int i = 1; i < sets.length; i++) {
			if (sets[i].size() != basicSet.size())
				return false;
			if (!sets[i].containsAll(basicSet))
				return false;
		}
		return true;
	}

	public static <T, C extends Collection<T>> C copy(C coll)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		C copy = GenericReflection.newInstance(coll);
		copy.addAll(coll);
		return copy;
	}

	public static <T> T[] toArray(Collection<T> coll, T[] a) {
		if (a.length < coll.size())
			a = GenericReflection.newArray(a, coll.size());
		int i = 0;
		for (T x : coll) {
			a[i++] = x;
		}
		if (i < a.length)
			a[i] = null;
		return a;
	}

}
