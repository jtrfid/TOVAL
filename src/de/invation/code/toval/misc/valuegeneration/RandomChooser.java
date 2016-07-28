package de.invation.code.toval.misc.valuegeneration;

import java.util.List;
import java.util.Random;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * 在集合中，随机选一个元素
 *
 * @param <E> 集合元素类型
 */
public class RandomChooser<E extends Object> implements ValueChooser<E> {
	
	Random rand = new Random();
    
	/**
	 * 在参数集合中，随机选一个元素
	 */
	@Override
	public E chooseValue(List<E> candidates) throws ParameterException {
		Validate.notNull(candidates);
		Validate.notEmpty(candidates);
		
		return candidates.get(rand.nextInt(candidates.size()));
	}

	/**
	 * 是否有效
	 */
	@Override
	public boolean isValid() {
		return true;
	}

}
