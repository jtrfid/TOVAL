package misc;
import java.util.Random;

/**
 * 
 * @author Thomas Stocker
 *
 */
public class RandomUtils {
	
	private static java.util.Random rand = new Random(); 
	
	/**
	 * Returns a random positive double value in the range [0;1).
	 * @return Random double value in the range [0;1)
	 */
	public static double randomPosDouble() {
		return Math.abs(rand.nextDouble());
	}
	
	/**
	 * Returns a random negative double value in the range (-1;0].
	 * @return Random double value in the range (-1;0]
	 */
	public static double randomNegDouble() {
		return -randomPosDouble();	
	}
	
	/**
	 * Returns a random <code>int</code> value out of a specified range
	 * @param lowerBound Lower bound of the target range (inclusive)
	 * @param upperBound Upper bound of the target range (exclusive)
	 * @return A random <code>int</code> value out of a specified range
	 */
	public static int randomIntBetween(int lowerBound, int upperBound) {
		if(upperBound<lowerBound)
			throw new IllegalArgumentException("lower bound higher than upper bound");
		return rand.nextInt(upperBound-lowerBound)+lowerBound;
	}

}
