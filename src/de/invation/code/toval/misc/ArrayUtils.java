package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.invation.code.toval.math.Permutations;

import reflect.GenericReflection;


public class ArrayUtils {
	
	/**
	 * Random number generator.
	 */
	private static java.util.Random rand = new Random(); 
	/**
	 * String for value separation.<br>
	 * Used for generating String representations of arrays.
	 */
	public static final char VALUE_SEPARATION = ' ';
	/**
	 * String representation for empty arrays.
	 */
	public static final String EMPTY_ARRAY = "[]";
	/**
	 * Default precision used for String representations of array elements
	 * having type <code>Float</code> or <code>Double</code>.
	 */
	public static final int DEFAULT_PRECISION = 2;
	
	/**
	 * Returns a new array with the given size containing the default value at each index.
	 * @param size The desired size of the array.
	 * @param defaultValue The default value to use
	 * @return The created array
	 */
	public static <T> T[] createArray(int size, T defaultValue){
		T[] result = (T[]) GenericReflection.newArray(defaultValue.getClass(), size);
		for(int i=0; i<result.length; i++){
			result[i] = defaultValue;
		}
		return result;
	}
	
	/**
	 * Returns a random element of the given array.
	 * @param <T> Type of array elements
	 * @param arr Array
	 * @return Random element of {@link arr}
	 */
	public static <T> T getRandomItem(T[] arr){
		return arr[rand.nextInt(arr.length)];
	}
	
	/**
	 * Reverses the entries of a given array.
	 * @param <T> Type of array elements
	 * @param arr Array to reverse
	 * @return The same array in reverse order
	 */
	public static <T> T[] reverseArray(T[] arr) {
		for (int left=0, right=arr.length-1; left<right; left++, right--) {
		    T temp = arr[left]; 
		    arr[left] = arr[right]; 
		    arr[right] = temp;
		}
		return arr;
	}
	
	public static <T> List<T> toList(T[] arr){
		return Arrays.asList(arr);
	}
	
	public static <T> List<String> toStringList(T[] arr){
		List<String> result = new ArrayList<String>();
		for(T t: arr){
			result.add(t.toString());
		}
		return result;
	}
	
	/**
	 * Swaps the elements at position {@link a} and {@link b} in the array {@link arr}. 
	 * @param <T> Type of array elements
	 * @param arr Array that contains the elements to be swapped
	 * @param a First position
	 * @param b Second position
	 */
	public static <T> void swap(T[] arr, int a, int b) {
		if(a<0 || a>arr.length || b<0 || b>arr.length)
			throw new IllegalArgumentException("swap position out of bounds.");
		if(a != b) {
			T t = arr[a]; 
			arr[a] = arr[b]; 
			arr[b] = t;
		}
	}
	
	/**
	 * Permutes the elements of the given array.
	 * @param <T> Array-type
	 * @param arr Array to shuffle
	 */
	public static <T> void shuffleArray(T[] arr) {
		for(int i = arr.length; i > 1; i--)
			swap(arr, i-1, rand.nextInt(i));
	}

	/**
	 * Checks if the given array contains the specified value.<br>
	 * @param <T> Type of array elements and {@link value}
	 * @param array Array to examine
	 * @param value Value to search
	 * @return <code>true</code> if {@link array} contains {@link value}, <code>false</code> otherwise
	 */
	public static <T> boolean arrayContains(T[] array, T value) {
		for(int i=0; i<array.length; i++) {
			if(array[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Divides the given array using the boundaries in {@link cuts}.<br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut at position i
	 * divides the given array in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start and end-indexes<br>
	 * @param <T> Type of array elements
	 * @param arr The array to divide
	 * @param cuts Cut positions for divide operations
	 * @return A list of subarrays of {@link arr} according to the given cut positions
	 */
	public static <T> List<T[]> divideArray(T[] arr, Integer... cuts) {
		Arrays.sort(cuts);
		int c = cuts.length;
		if(cuts[0]<0 || cuts[c-1]>arr.length-1)
			throw new IllegalArgumentException("cut position out of bounds.");
		int startIndex = cuts[0]==0 ? 1 : 0;
		if(cuts[c-1]!=arr.length-1) {
			cuts = Arrays.copyOf(cuts, cuts.length+1);
			cuts[cuts.length-1] = arr.length-1;
			c++;
		}
		List<T[]> result = new ArrayList<T[]>(c-startIndex);
		int lastEnd = 0;
		for(int i=startIndex; i<=c-1; i++) {
			int c2 = i<c-1 ? 0 : 1;
			result.add(Arrays.copyOfRange(arr, lastEnd, cuts[i]+c2));
			lastEnd = cuts[i];
		}
		return result;
	}
	
	/**
	 * Divides the given object-array using the boundaries in {@link cuts}.<br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut at position i
	 * divides the given array in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start and end-indexes<br>
	 * @param arr Array to divide
	 * @param cuts Cut positions for divide operations
	 * @return A list of subarrays of {@link arr} according to the given cut positions
	 * @see #divideArray(Object[], Integer[])
	 */
	public static <T> List<T[]> divideObjectArray(T[] arr, Integer... cuts) {
		return divideArray(arr, cuts);
	}
	
	/**
	 * Returns an Iterator for all possible permutations of the given array.
	 * @param <T> Type of list array
	 * @param arr Basic array for permutations
	 * @return Iterator holding all possible permutations
	 */
	public static <T> Iterator<T[]> getPermutations(T[] arr){
		return new ArrayPermutations<T>(arr);
	}
	
	/**
	 * Generates all permutations of a given array.
	 * @param <T> Type of array elements.
	 */
	private static class ArrayPermutations<T> extends Permutations<T[]> {

		private T[] array;

		public ArrayPermutations(T[] array) {
			super(array.length);
			this.array = array;
		}

		/**
		 * Returns a new array with permuted elements.
		 * @return A new array with permuted elements
		 */
		@Override
		public T[] next() {
			Integer[] next = super.nextPermutation();
			T[] newArr = array.clone();
			for (int i = 0; i < next.length; i++) {
				newArr[i] = array[next[i]];
			}
			return newArr;
		}
	}
	
	/**
	 * Returns a String representation of an object-array.<br>
	 * @param arr Object-array for String representation
	 * @return String representation of {@link arr}
	 * @see ArrayUtils#getFormat(Object[])
	 */
	public static <T> String toString(T[] arr, char valueSeparation) {
		return toString(arr, DEFAULT_PRECISION, valueSeparation);
	}
	
	public static <T> String toString(T[] arr) {
		return toString(arr, DEFAULT_PRECISION, VALUE_SEPARATION);
	}
	
	/**
	 * Returns a String representation of an object-array.<br>
	 * The specified precision is only applicable for <code>Float</code> and <code>Double</code> elements.
	 * @param arr Object-array for String representation
	 * @return String representation of {@link arr}
	 * @see ArrayUtils#getFormat(Object[], int)
	 */
	public static <T> String toString(T[] arr, int precision, char valueSeparation) {
		if(arr.length>0)
			return String.format(getFormat(arr, precision, valueSeparation), arr);
		else return EMPTY_ARRAY;
	}
	
	/**
	 * Returns a format-String that can be used to generate a String representation of an array
	 * using the String.format method.
	 * @param arr Array for which a String representation is desired
	 * @param precision Desired precision for <code>Float</code> and <code>Double</code> elements
	 * @return Format-String for {@link arr}
	 * @see Formatter
	 * @see String#format(String, Object...)
	 */
	private static <T> String getFormat(T[] arr, int precision, char valueSeparation) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for(int i=0; i<arr.length-1; i++) {
			builder.append(FormatUtils.getFormat(arr[i], precision));
			builder.append(valueSeparation);
		}
		builder.append(FormatUtils.getFormat(arr[arr.length-1], precision));
		builder.append(']');
		return builder.toString();
	}
	
	

}
