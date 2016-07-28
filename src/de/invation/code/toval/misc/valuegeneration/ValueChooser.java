package de.invation.code.toval.misc.valuegeneration;

import java.util.List;

import de.invation.code.toval.validate.ParameterException;

/**
 * 集合中选一个元素
 *
 * @param <E> 集合元素类型
 */
public interface ValueChooser<E extends Object> {
	/**
	 * 参数集合中选一个值
	 * @param candidates
	 * @return
	 * @throws ParameterException
	 */
	public E chooseValue(List<E> candidates) throws ParameterException;
	
	/**
	 * 是否有效
	 * @return
	 */
	public boolean isValid();

}