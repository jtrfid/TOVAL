package de.invation.code.toval.math;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * Permutation Generator.
 * Generates all permutations of an array with a specified length.
 */
public abstract class Permutations<T> implements Iterator<T>{

	/**
	 * Array holding different permutations.
	 */
	private Integer[] position;
	/**
	 * Number of permutations left (not yet generated).
	 */
	private BigInteger permLeft;
	/**
	 * Total number of permutations.
	 */
	private BigInteger permTotal;

	/**
	 * Initializes the permutation generation mechanism using the specified length,
	 * which is interpreted as the number of elements that are permuted.
	 * @param elementNumber Number of elements to permute.
	 */
	public Permutations(int elementNumber) {
		if (elementNumber < 1)
			throw new IllegalArgumentException("Illegal length: "+elementNumber);
		position = new Integer[elementNumber];
		permTotal = getFactorial(elementNumber);
		reset();
	}

	/**
	 * Resets the permutation generator.
	 */
	public void reset() {
		for (int i = 0; i < position.length; i++) {
			position[i] = i;
		}
		permLeft = new BigInteger(permTotal.toString());
	}

	/**
	 * Returns the residual number of permutations.
	 * @return Residual number of permutations
	 */
	public BigInteger getNumLeft() {
		return permLeft;
	}

	/**
	 * Returns the total number of permutations.
	 * @return Total number of permutations
	 */
	public BigInteger getTotal() {
		return permTotal;
	}

	/**
	 * Checks if there are residual permutations.
	 * @return <code>true</code> if there are residual permutations;
	 * <code>false</code> otherwise
	 */
	public boolean hasNext() {
		return permLeft.compareTo(BigInteger.ZERO) == 1;
	}

	/**
	 * Computes the factorial of the given number
	 * @param number jBasic number for factorial computation
	 * @return The factorial of {@link number}
	 */
	private static BigInteger getFactorial(int number) {
		BigInteger factorial = BigInteger.ONE;
		for (int i = number; i > 1; i--) {
			factorial = factorial.multiply(new BigInteger(Integer.toString(i)));
		}
		return factorial;
	}

	/**
	 * Generates the next permutation (algorithm from Rosen p. 284)
	 * @return an array containing the next permutation
	 */
	public Integer[] nextPermutation() {
		if (permLeft.equals(permTotal)) {
			permLeft = permLeft.subtract(BigInteger.ONE);
			return position;
		}
		
		// Find largest index j with a[j] < a[j+1]
		int j = position.length - 2;
		while (position[j] > position[j + 1]) { j--;}

		// Find index k such that a[k] is smallest integer
		// greater than a[j] to the right of a[j]
		int k = position.length - 1;
		while (position[j] > position[k]) { k--;}

		// Interchange a[j] and a[k]
		int temp;
		temp = position[k];
		position[k] = position[j];
		position[j] = temp;

		// Put tail end of permutation after j-th position in increasing order
		int r = position.length - 1;
		int s = j + 1;
		while (r > s) {
			temp = position[s];
			position[s] = position[r];
			position[r] = temp;
			r--;
			s++;
		}
		
		permLeft = permLeft.subtract(BigInteger.ONE);
		return position;
	}

	/**
	 * This <code>Iterator</code>-implementation does not support the optional <code>remove</code>-operation.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
