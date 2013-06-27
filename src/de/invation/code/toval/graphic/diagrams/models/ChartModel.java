package de.invation.code.toval.graphic.diagrams.models;

import types.StatList;

public interface ChartModel<S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> {
	
	/**
	 * Returns the number of values maintained for the given dimension.<br>
	 * @return The number of values maintained for the given dimension
	 */
	public int getValueCount(ValueDimension dim);
	
	/**
	 * Returns the maintained values for the given dimension.
	 * @param dim Reference dimension
	 * @return Maintained values for the given dimension
	 */
	public StatList<?> getValues(ValueDimension dim);
	
	/**
	 * Returns the value with the given index of the given dimension.
	 * @param dim Reference dimension for value extraction
	 * @param index Index of the desired value
	 * @return Value with the given index of the given dimension
	 */
	public Number getValue(ValueDimension dim, int index);
	
	/**
	 * enumeration type for value dimensions used in diagram panels.
	 * @author Thomas Stocker
	 */
	public enum ValueDimension {X,Y}
}
