package de.invation.code.toval.misc.valuegeneration;

import de.invation.code.toval.validate.InconsistencyException;


/**
 * Interface for value generators.<br>
 * A value generator produces values of a specific type and returns it by the method {@link #getNextValue()}.
 * Value generation is only permitted if the generator is in a valid state.<br>
 * Once entered a valid state, the generator cannot enter an invalid state again.<br>
 * If value generations are requested in invalid state, 
 * Exceptions of type {@link ValueGenerationException} are thrown.
 *
 * @param <E>
 */
public interface ValueGenerator<E extends Object> {
	
	/** 获取下一值 */
	public E getNextValue() throws ValueGenerationException;
	/** 是否有效 */
	public boolean isValid();
	
	@SuppressWarnings("rawtypes")
	public Class getValueClass() throws InconsistencyException;
	
	public boolean isEmpty();
	
	public ValueGenerator<E> clone();
}
