package de.invation.code.toval.math;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.validate.Validate;

public class CombinationsCalculator<T> implements Iterator<T[]>{
	
	private Object[] originalArray = null;
	private int combinationLength = 0;
	private long numCombinations = 0;
	private long calculatedCombinations = 0;
	
	private int[] lastIndexCombination = null;
	
	@SuppressWarnings("unchecked")
	public CombinationsCalculator(List<T> originalList, int combinationLength){
		this((T[]) originalList.toArray(), combinationLength);
	}
	
	public CombinationsCalculator(T[] originalArray, int combinationLength){
		Validate.notNull(originalArray);
		Validate.bigger(originalArray.length, 0);
		Validate.bigger(combinationLength, 0);
		Validate.isTrue(combinationLength <= originalArray.length);
		
		this.originalArray = originalArray;
		this.combinationLength = combinationLength;
		numCombinations = MathUtils.binCoeff(originalArray.length, combinationLength);
		lastIndexCombination = ArrayUtils.createAndInitializeArray(combinationLength, 0);
	}

	@Override
	public boolean hasNext() {
		return calculatedCombinations < numCombinations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] next() {
		if (!hasNext()) {
            throw new NoSuchElementException();
        }
		
        Object[] result = new Object[combinationLength];
        for (int i = 0; i < combinationLength; i++) {
            result[i] = originalArray[lastIndexCombination[i]];
        }

        for (int i = combinationLength - 1; i >= 0; i--) {
            if (lastIndexCombination[i] < originalArray.length - combinationLength + i) {
                lastIndexCombination[i]++;
                for (int j = i + 1; j < combinationLength; j++) {
                    lastIndexCombination[j] = lastIndexCombination[i] - i + j;
                }
                break;
            }
        }
        
        calculatedCombinations++;
        return (T[]) result;
	}

	@Override
	public void remove() {}

//	public static void main(String[] args) {
//		List<Integer> arr = ListUtils.createAndInitializeList(100, new Integer(1));
//		System.out.println(arr);
//		CombinationsCalculator<Integer> calc = new CombinationsCalculator<Integer>(arr, 2);
//		long start = System.currentTimeMillis();
//		while(calc.hasNext()){
//			calc.next();
//		}
//		TimeValue time = new TimeValue(System.currentTimeMillis() - start, TimeScale.MILLISECONDS);
//		time.adjustScale();
//		System.out.println(time);
//	}

}
