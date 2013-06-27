package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.invation.code.toval.math.Permutations;
import de.invation.code.toval.types.HashList;


public class ListUtils {
	
	private static java.util.Random rand = new Random();
	
	/**
	 * Returns a random element of the given list.
	 * @param <T> Type of list elements
	 * @param list List
	 * @return Random element of {@link list}
	 */
	public static <T> T getRandomItem(List<T> list){
		return list.get(rand.nextInt(list.size()));
	}
	
	/**
	 * Inserts a heade
	 * @param <T>
	 * @param list
	 * @param headerValue
	 * @param headerSize
	 */
	public static <T> void insertHeader(List<T> list, T headerValue, int headerSize){
		for(int i=0; i<headerSize; i++)
			list.add(0, headerValue);
	}
	
	/**
	 * Expands an integer list to a size equal to its value range
	 * and adds <code>null</code>-value entries for every missing intermediate integer value.
	 * @param list List containing integer values
	 * @return An expanded list containing null values for missing intermediate integer values
	 */
	public static List<Integer> fillUpWithNulls(List<Integer> list){
		return fillUpWithNulls(list, null);
	}
	
	/**
	 * Expands an integer list to a size equal to its value range
	 * and adds <code>null</code>-value entries for every missing intermediate integer value.
	 * If {@link replace} is not <code>null</code>, all original values are replaced by {@link replace}.
	 * @param list List containing integer values
	 * @param replace Replacement for existing values within {@link list}
	 * @return An expanded list containing null values for missing intermediate integer values
	 * and optionally replaced original values
	 */
	public static List<Integer> fillUpWithNulls(List<Integer> list, Integer replace){
		Collections.sort(list);
		int minValue = list.get(0);
		int maxValue = list.get(list.size()-1);
		int range = (int) (maxValue-Math.signum(minValue)*Math.abs(minValue));
		
		ArrayList<Integer> result = new ArrayList<Integer>(range+1);
		for(int i=0; i<list.size()-1; i++) {
			result.add(replace!=null ? replace : list.get(i));
			for(int j=0; j<list.get(i+1)-list.get(i)-1; j++)
				result.add(null);
		}
		result.add(replace!=null ? replace : maxValue);
		return result;
	}
	
	public static <T> void swapElements(List<T> list, T element1, T element2){
		int index1 = list.indexOf(element1);
		int index2 = list.indexOf(element2);
		if(index1 == -1 || index1 == -1){
			return;
		}
		Collections.swap(list, index1, index2);
	}
	
	/**
	 * Returns a new list containing all elements of the original list but {@link exclude}
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param exclude Element to exclude
	 * @return A new List containing all elements of the original list but {@link exclude}
	 */
	public static <T> List<T> getListWithout(List<T> list, T exclude){
		List<T> result = new ArrayList<T>(list.size());
		for(T t: list)
			if(!t.equals(exclude))
				result.add(t);
		return result;
	}
	
	/**
	 * Returns a new list containing all elements of the original list but the elements in {@link exclude}
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param exclude Elements to exclude
	 * @return A new List containing all elements of the original list but the elements in {@link exclude}
	 */
	public static <T> List<T> getListWithout(List<T> list, List<T> exclude){
		List<T> result = new ArrayList<T>(list.size());
		for(T t: list)
			if(!exclude.contains(t))
				result.add(t);
		return result;
	}
	
	public static <T> boolean containsOnlyNulls(List<T> list){
		for(T t: list)
			if(t!=null)
				return false;
		return true;
	}

	/**
	 * Converts a list to an array of the same type.
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @return An array of the same type containing all elements of {@link list}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] asArray(List<T> list) {
		return (T[]) list.toArray();
	}
	
	/**
	 * Creates a mutable list containing {@link n} copies of {@link value}.
	 * @param <T> Type of list elements
	 * @param value Basic value for list generation
	 * @param n Number of copies
	 * @return A mutable list containing {@link n} copies of {@link value}
	 */
	public static <T> List<T> createList(T value, int n){
		ArrayList<T> result = new ArrayList<T>(n);
		for(int i=0; i<n; i++)
			result.add(value);
		return result;
	}
	
	/**
	 * Creates a mutable list containing {@link size} incrementing integer values beginning with {@link begin}.
	 * @param size Number of integer values within the result list
	 * @param begin First integer to start with
	 * @return A list containing incrementing integer values beginning with the specified value
	 */
	public static List<Integer> createAndInitializeList(int size, int begin){
		ArrayList<Integer> result = new ArrayList<Integer>(size);
		for(int i=begin; i<size+begin; i++)
			result.add(i);
		return result;
	}


	
	/**
	 * Divides the given list using the boundaries in {@link cuts}.<br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut at position i
	 * divides the given list in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start and end-indexes<br>
	 * @param <T> Type of list elements
	 * @param list The list to divide
	 * @param cuts Cut positions for divide operations
	 * @return A list of sublists of {@link list} according to the given cut positions
	 */
	public static <T> List<List<T>> divideList(List<T> list, Integer... cuts) {
		Arrays.sort(cuts);
		int c = cuts.length;
		if(cuts[0]<0 || cuts[c-1]>list.size()-1)
			throw new IllegalArgumentException();
		int startIndex = cuts[0]==0 ? 1 : 0;
		if(cuts[c-1]!=list.size()-1) {
			cuts = Arrays.copyOf(cuts, cuts.length+1);
			cuts[cuts.length-1] = list.size()-1;
			c++;
		}
		List<List<T>> result = new ArrayList<List<T>>(c-startIndex);
		int lastEnd = 0;
		for(int i=startIndex; i<=c-1; i++) {
			int c2 = i<c-1 ? 0 : 1;
			result.add(copyOfRange(list, lastEnd, cuts[i]+c2-1));

			lastEnd = cuts[i];
		}
		return result;
	}
	
	/**
	 * Divides the given object-list using the boundaries in {@link cuts}.<br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut at position i
	 * divides the given list in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start and end-indexes<br>
	 * @param list List to divide
	 * @param cuts Cut positions for divide operations
	 * @return A list of sublists of {@link list} according to the given cut positions
	 * @see #divideList(List, Integer...)
	 */
	public static List<List<Object>> divideObjectList(List<Object> list, Integer... cuts) {
		return divideList(list, cuts);
	}
	
	public static <T> List<List<T>> randomPartition(List<T> coll, int number){
		List<List<T>> checkResult = checkPartitionConditions(coll, number);
		if(checkResult!=null)
			return checkResult;
		HashList<Integer> cuts = new HashList<Integer>();
		while(cuts.size()<number-1){
			int nextInt = rand.nextInt(coll.size());
			if(nextInt>0 && nextInt<coll.size()-1)
				cuts.add(nextInt);
		}
		return divideList(coll, cuts.toArray(new Integer[1]));
	}
	
	public static <T> List<List<T>> exponentialPartition(List<T> coll, int number){
		List<List<T>> checkResult = checkPartitionConditions(coll, number);
		if(checkResult!=null)
			return checkResult;
		HashList<Integer> cuts = new HashList<Integer>();
		Double factor = Math.max(3.0, 3.0 + (3.0-number))/Math.max(3, 3.0 + (number-3.0));
		for(int i=-1; i>(-1)*number; i--){
			int cut = (int) Math.ceil(Math.exp(factor*i)*coll.size());
			if(cut == coll.size() || (!cuts.isEmpty() && cut==cuts.get(cuts.size()-1)))
				cut -= 1;
			cuts.add(cut);
		}
		return divideList(coll, cuts.toArray(new Integer[1]));
	}
	
	private static <T> List<List<T>> checkPartitionConditions(List<T> coll, int number){
		if(coll==null || coll.isEmpty() || number<1 || number>coll.size())
			throw new IllegalArgumentException();
		if(number==1){
			List<List<T>> result = new ArrayList<List<T>>();
			result.add(coll);
			return result;
		}
		if(number == coll.size())
			return divideAsList(coll);
		return null;
	}
	
	/**
	 * Returns a list of lists containing exactly one element of the given list each.
	 * @param coll The list to split
	 * @return A list of lists containing exactly one element of the given list each.
	 */
	public static <T> List<List<T>> divideAsList(List<T> coll){
		List<List<T>> result = new ArrayList<List<T>>();
		for(T t: coll){
			ArrayList<T> list = new ArrayList<T>();
			list.add(t);
			result.add(list);
		}
		return result;
	}
	
	/**
	 * Returns a new list containing all elements of the original list with an index in [from;to]
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param from Start-index (inclusive) for copy operation
	 * @param to End-index (inclusive) for copy operation
	 * @return The sublist of {@link list} starting at index {@link from} and ending at index {@link to}
	 */
	public static <T> List<T> copyOfRange(List<T> list, int from, int to) {
		if(from<0 || from >=list.size() || to<0 || to>=list.size() || from>to)
			throw new IllegalArgumentException("Illegal extraction bounds");
		ArrayList<T> result = new ArrayList<T>(to-from+1);
		for(int i=from; i<=to; i++)
			result.add(list.get(i));
		return result;
	}
	
	/**
	 * Returns a copy of the given list.
	 * @param <T> Type of list elements
	 * @param list List to copy
	 * @return Copy of {@link list} containing the same elements
	 */
	public static <T> List<T> copyOf(List<T> list) {
		ArrayList<T> result = new ArrayList<T>(list.size());
		result.addAll(list);
		return result;
	}
	
	/**
	 * Returns a string list representation of the given list.
	 * @param coll List to convert
	 * @return A list containing string representations for all elements of the input list.
	 */
	public static List<String> asStringList(List<?> coll){
		List<String> result = new ArrayList<String>();
		for(Object t: coll){
			result.add(t.toString());
		}
		return result;
	}
	
	/**
	 * Returns an Iterator for all possible permutations of the given list.
	 * @param <T> Type of list elements
	 * @param list Basic list for permutations
	 * @return Iterator holding all possible permutations
	 */
	public static <T> ListPermutations<T> getPermutations(List<T> list){
		return new ListPermutations<T>(list);
	}
	
	public static <T> List<Partition<T>> getPartitions(List<T> list, Integer... elementCounts){
		int totalCount = 0;
		for(Integer elements: elementCounts)
			totalCount += elements;
		if(totalCount!=list.size())
			return null;
		Integer[] cuts = new Integer[elementCounts.length-1];
		for(int i=0; i<elementCounts.length-1; i++)
			cuts[i] =  elementCounts[i] + (i==0 ? 0 : cuts[i-1]);
		List<Partition<T>> result = new ArrayList<Partition<T>>();
		Iterator<List<T>> permutations = getPermutations(list);
		while(permutations.hasNext()){
			Partition<T> part = new Partition<T>(list);
			List<List<T>> dividedList = divideList(permutations.next(), cuts);
			for(List<T> l: dividedList)
				part.addSubset(l);
			result.add(part);
		}
		return result;
	}
	
	/**
	 * Generates a random sublist of {@link list}, that contains at most {@link maxCount} elements.
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param maxCount Maximum number of items
	 * @return A sublist with at most {@link maxCount} elements
	 */
	public static <T> List<T> getRandomSublistMax(List<T> list, int maxCount){
		int count = rand.nextInt(maxCount)+1;
		return getRandomSublist(list, count);	
	}
	
	/**
	 * Generates a random sublist of {@link list}, that contains at least {@link maxCount} elements.
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param minCount Minimum number of items
	 * @return A sublist with at least {@link minCount} elements
	 */
	public static <T> List<T> getRandomSublistMin(List<T> list, int minCount){
		int count = RandomUtils.randomIntBetween(minCount, list.size());
		return getRandomSublist(list, count);	
	}
	
	/**
	 * Generates a random sublist of {@link list}, that contains exactly {@link maxCount} elements.
	 * @param <T> Type of list elements
	 * @param list Basic list for operation
	 * @param count Number of items
	 * @return A sublist with exactly {@link count} elements
	 */
	public static <T> List<T> getRandomSublist(List<T> list, int count){
		List<T> result = new ArrayList<T>();
		ArrayList<T> opSet = new ArrayList<T>(list);
		while(result.size()<count){
			int next = rand.nextInt(opSet.size());
			result.add(opSet.get(next));
			opSet.remove(next);
		}
		return result;	
	}
	
	/**
	 * Generates all permutations of a given list.
	 * @param <T> Type of list elements.
	 */
	public static class ListPermutations<T> extends Permutations<List<T>> {

		private List<T> list;

		public ListPermutations(List<T> list) {
			super(list.size());
			this.list = list;
		}

		/**
		 * Returns a new list with permuted elements.
		 * @return A new list with permuted elements
		 */
		@Override
		public List<T> next() {
			Integer[] next = super.nextPermutation();
			List<T> newList = new ArrayList<T>();
			for (int i = 0; i < next.length; i++) {
				newList.add(i, list.get(next[i]));
			}
			return newList;
		}

	}
	
	public static class Partition<T> {
		private static final long serialVersionUID = 1L;
		
		private List<T> basicSet = null;
		private List<List<T>> subsets = new ArrayList<List<T>>();
		private int elements = 0;
		
		public Partition(List<T> basicSet){
			this.basicSet = basicSet;
		}
		
		public List<T> getSubset(int index){
			return subsets.get(index);
		}
		
		public void addSubset(List<T> subset){
			if(basicSet.containsAll(subset) && (elements+subset.size())<=basicSet.size()){
				subsets.add(subset);
				elements += subset.size();
			}
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			for(List<T> subset: subsets){
				builder.append(subset);
				builder.append(' ');
			}
			return builder.toString();
		}
	}
	
	public static <T> List<List<T>> getKElementaryLists(List<T> list, int k) {
		if(list.size()<0)
			throw new IllegalArgumentException("set size 0");
		List<List<T>> result = new ArrayList<List<T>>();
		for (int i = 0; i < Math.pow(2, list.size()); i++) {
			int setSize = Integer.bitCount(i);
			if(setSize == k){
				List<T> newList = new ArrayList<T>(setSize);
				result.add(newList);
				for (int j = 0; j < list.size(); j++) {
					if ((i & (1 << j)) != 0) {
						newList.add(list.get(j));
					}
				}
			}
		}
		return result;
	}

}
