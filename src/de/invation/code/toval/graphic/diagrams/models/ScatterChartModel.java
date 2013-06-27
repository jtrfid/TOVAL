package de.invation.code.toval.graphic.diagrams.models;

import java.util.HashMap;
import java.util.List;

import de.invation.code.toval.types.StatList;


public class ScatterChartModel<S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> implements ChartModel<S,T> {
	
	/**
	 * Keeps the values for the two coordinate axes.<br>
	 * The values are kept in form of <code>StatList</code>s and are indexable by their corresponding coordinate axes.
	 * @see StatList
	 */
	protected HashMap<ValueDimension, StatList<?>> values = new HashMap<ValueDimension, StatList<?>>(2);
	
	public ScatterChartModel() {}

	public ScatterChartModel(List<S> xValues, List<T> yValues, boolean equalSizes) {
		if(xValues == null || yValues == null)
			throw new NullPointerException();
		if(equalSizes)
			if(xValues.size() != yValues.size())
				throw new IllegalArgumentException("Collection sizes do not match!");
		values.put(ValueDimension.X, new StatList<S>(xValues, false));
		values.put(ValueDimension.Y, new StatList<T>(yValues, false));
	}
	
	/**
	 * Returns the number of values maintained for the given dimension.<br>
	 * @return The number of values maintained for the given dimension
	 */
	@Override
	public int getValueCount(ValueDimension dim) {
		return values.get(dim).size();
	}

	/**
	 * Returns the maintained values for the given dimension.
	 * @param dim Reference dimension
	 * @return Maintained values for the given dimension
	 */
	@Override
	public StatList<?> getValues(ValueDimension dim) {
		return values.get(dim);
	}
	
	
	public void setXValues(List<S> xValues){
		if(xValues == null)
			throw new NullPointerException();
		values.put(ValueDimension.X, new StatList<S>(xValues, false));
	}
	
	public void setYValues(List<T> yValues){
		if(yValues == null)
			throw new NullPointerException();
		values.put(ValueDimension.Y, new StatList<T>(yValues, false));
	}
	
	/**
	 * Returns the value with the given index of the given dimension.
	 * @param dim Reference dimension for value extraction
	 * @param index Index of the desired value
	 * @return Value with the given index of the given dimension
	 */
	@Override
	public Number getValue(ValueDimension dim, int index) {
		return values.get(dim).get(index);
	}

}
