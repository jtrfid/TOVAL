package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.invation.code.toval.types.HashList;
import de.invation.code.toval.validate.Validate;


public class SetUtils {
	
	private static java.util.Random rand = new Random(); 
	
	/**
	 * Generates a subset of <code>set</code>, that contains a random number of elements.
	 * @param <T> Type of set elements
	 * @param set Basic set for operation
	 * @return A subset with a random number of elements
	 */
	public static <T> Set<T> getRandomSubset(Set<T> set){
		return getRandomSubsetMax(set, set.size());
	}
	
	/**
	 * Generates a random subset of <code>set</code>, that contains at most <code>maxCount</code> elements.
	 * @param <T> Type of set elements
	 * @param set Basic set for operation
	 * @param maxCount Maximum number of items
	 * @return A subset with at most <code>maxCount</code> elements
	 */
	public static <T> Set<T> getRandomSubsetMax(Set<T> set, int maxCount){
		int count = rand.nextInt(maxCount)+1;
		return getRandomSubset(set, count);	
	}
	
	/**
	 * Generates a random subset of <code>set</code>, that contains at least <code>maxCount</code> elements.
	 * @param <T> Type of set elements
	 * @param set Basic set for operation
	 * @param minCount Minimum number of items
	 * @return A subset with at least <code>minCount</code> elements
	 */
	public static <T> Set<T> getRandomSubsetMin(Set<T> set, int minCount){
		int count = RandomUtils.randomIntBetween(minCount, set.size());
		return getRandomSubset(set, count);	
	}
	
	/**
	 * Generates a random subset of <code>set</code>, that contains exactly <code>maxCount</code> elements.
	 * @param <T> Type of set elements
	 * @param set Basic set for operation
	 * @param count Number of items
	 * @return A subset with exactly <code>count</code> elements
	 */
	public static <T> Set<T> getRandomSubset(Set<T> set, int count){
		Set<T> result = new HashSet<T>();
		HashList<T> opSet = new HashList<T>(set);
		while(result.size()<count){
			int next = rand.nextInt(opSet.size());
			if(!result.contains(opSet.get(next))){
				result.add(opSet.get(next));
			}
		}
		return result;	
	}
	
	/**
	 * Generates a new Powerset out of the given set.
	 * @param <T> Type of set elements
	 * @param hashSet Underlying set of elements
	 * @return Powerset of <code>set</code>
	 */
	public static <T> PowerSet<T> getPowerSet(Set<T> hashSet) {
		Validate.notNull(hashSet);
		if(hashSet.size() == 0)
			throw new IllegalArgumentException("set size 0");
		HashList<T> hashList = new HashList<T>(hashSet);
		PowerSet<T> result = new PowerSet<T>(hashList.size());
		for (int i = 0; i < Math.pow(2, hashList.size()); i++) {
			int setSize = Integer.bitCount(i);
			HashSet<T> newList = new HashSet<T>(setSize);
			result.get(setSize).add(newList);
			for (int j = 0; j < hashList.size(); j++) {
				if ((i & (1 << j)) != 0) {
					newList.add(hashList.get(j));
				}
			}
		}
		return result;
	}
	
	public static synchronized <T> List<List<T>> getKElementarySets(Set<T> set, int k) {
		return ListUtils.getKElementaryLists(new ArrayList<T>(set), k);
	}
	
	/**
	 * Basic class for representing a powerset (the set of all subsets of a set).<br>
	 * Subsets are kept in a map grouped by their size which acts as map-key.
	 * map-values are lists of {@link HashSet}s representing the subsets.
	 * @param <T> Type of set elements
	 * 
	 * @author Thomas Stocker
	 */
	@SuppressWarnings("serial")
	public static class PowerSet<T> extends HashMap<Integer, List<HashSet<T>>>{
		
		/**
		 * Creates a new PowerSet and prepares the subset-map.
		 * @param setSize The size (number of elements) of the underlying set
		 */
		public PowerSet(int setSize){
			for(int i=0; i<=setSize ; i++) {
				put(i, new ArrayList<HashSet<T>>());
			}
		}
		
		/**
		 * Returns a String representation of the powerset.
		 * @return String representation of the powerset
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			String format = "%s: %s\n";
			for(Integer i: keySet()) {
				builder.append(String.format(format, i, Arrays.toString(get(i).toArray())));
			}
			return builder.toString();
		}
	
	}
	
	/**
	 * Determines the intersection of a collection of sets.
	 * @param sets Basic collection of sets.
	 * @return The set of common elements of all given sets.
	 */
	public static <T> Set<T> intersection(Collection<Set<T>> sets){
		Set<T> result = new HashSet<T>();
		if(sets.isEmpty()){
			return result;
		}
		Iterator<Set<T>> iter = sets.iterator();
		result.addAll(iter.next());
		while(iter.hasNext()){
			result.retainAll(iter.next());
		}
		return result;
	}
	
	/**
	 * Determines the intersection of a collection of sets.
	 * @param sets Basic collection of sets.
	 * @return The set of common elements of all given sets.
	 */
	public static <T> Set<T> intersection(Set<T>... sets){
		return intersection(Arrays.asList(sets));
	}
	
	/**
	 * Determines the union of a collection of sets.
	 * @param sets Basic collection of sets.
	 * @return The set of distinct elements of all given sets.
	 */
	public static <T> Set<T> union(Collection<Set<T>> sets){
		Set<T> result = new HashSet<T>();
		if(sets.isEmpty()){
			return result;
		}
		Iterator<Set<T>> iter = sets.iterator();
		result.addAll(iter.next());
		
		if(sets.size() == 1){
			return result;
		}
		
		while(iter.hasNext()){
			result.addAll(iter.next());
		}
		return result;
	}
	
	public static <T> Set<T> union(Set<T>... sets){
		List<Set<T>> list = new ArrayList<Set<T>>(sets.length);
		for(Set<T> set: sets){
			list.add(set);
		}
		return union(list);
	}
	
	public static <T> boolean containSameElements(Collection<Set<T>> sets){
		if(sets.isEmpty() || sets.size()<2)
			return false;
		Iterator<Set<T>> iterator = sets.iterator();
		Set<T> basicSet = iterator.next();
		Set<T> actualSet;
		while(iterator.hasNext()){
			actualSet = iterator.next();
			if(actualSet.size() != basicSet.size())
				return false;
			if(!actualSet.containsAll(basicSet))
				return false;
		}
		return true;
	}
	
	public static <T> boolean containSameElements(Set<T>... sets){
		if(sets.length == 0 || sets.length<2)
			return false;
		Set<T> basicSet = sets[0];
		for(int i=1; i<sets.length; i++){
			if(sets[i].size() != basicSet.size())
				return false;
			if(!sets[i].containsAll(basicSet))
				return false;
		}
		return true;
	}
	
	public static <T> T getRandomElement(Set<T> set){
		ArrayList<T> list = new ArrayList<T>();
		list.addAll(set);
		return list.get(rand.nextInt(list.size()));
	}
	
	/**
	 * Checks if any two of the given sets intersect.
	 * @param sets Basic collection of sets.
	 * @return <code>true</code> if there is an intersection between at least two sets;<br>
	 * <code>false</code> otherswise.
	 */
	public static boolean existPairwiseIntersections(Collection<Set<String>> sets){
		//Determine all possible pairs of sets
		List<List<Set<String>>> setPairs = ListUtils.getKElementaryLists(new ArrayList<Set<String>>(sets), 2);
		for(Iterator<List<Set<String>>> iter = setPairs.iterator(); iter.hasNext();){
			Set<String> intersection = SetUtils.intersection(iter.next());
			if(!intersection.isEmpty()){
				return true;
			}
		}
		return false;
	}

}
