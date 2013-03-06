package types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Matrix representation where any type can be used for indexing cols and rows.<br>
 * Internally values are put in a 2-dimensional list, represented by an ArrayList of ArrayLists.
 * Indexing values are mapped onto integers that serve as indexes for retrieving the appropriate values out of the list.
 * @param <E> Indexing type for rows and cols
 * @param <T> Value type for matrix entries
 * 
 * @author Thomas Stocker
 */
@SuppressWarnings("serial")
public class DynamicMatrix<E, T> extends ArrayList<ArrayList<T>> {
	/**
	 * Key-mapping for matrix rows.<br>
	 * Each row-value of indexing type <code>E</code> is mapped to an integer.
	 */
	private HashMap<E, Integer> rowKeys = new HashMap<E, Integer>();
	/**
	 * Key-mapping for matrix cols.<br>
	 * Each col-value of indexing type <code>E</code> is mapped to an integer.
	 */
	private HashMap<E, Integer> colKeys = new HashMap<E, Integer>();
	/**
	 * String format used for String representations of matrix entries.
	 */
	private static final String valueFormat = "[%s %s]: %s";

	/**
	 * Creates a new DynamicMatrix.
	 */
	public DynamicMatrix() {}

	/**
	 * Sets the matrix entry specified by the given row and col keys.<br>
	 * This method sets the following content: Matrix[row,col]=value
	 * @param row Row-value of indexing type
	 * @param col Col-value of indexing type
	 * @param value Value that has to be inserted
	 */
	public void putValue(E row, E col, T value) {
		ensureRowKey(row);
		ensureColKey(col);
		get(rowKeys.get(row)).set(colKeys.get(col), value);
	}
	
	/**
	 * Returns the matrix entry specified by the given row and col keys (Matrix[row,col]).
	 * @param row Row-value of indexing type
	 * @param col Col-value of indexing type
	 * @return Value at matrix position [row,col]
	 */
	public T getValue(E row, E col) {
		if(!rowKeys.containsKey(row))
			return null;
		if(!colKeys.containsKey(col))
			return null;
		try{
			get(rowKeys.get(row)).get(colKeys.get(col));
		} catch(IndexOutOfBoundsException e){
			return null;
		}
		return get(rowKeys.get(row)).get(colKeys.get(col));
	}
	
	/**
	 * Returns a String representation of the matrix entry specified by the given row and col keys (Matrix[row,col]).
	 * @param row Row-value of indexing type
	 * @param col Col-value of indexing type
	 * @return String representation of the value at matrix position [row,col]
	 * @see #valueFormat
	 * @see #getValue(Object, Object)
	 */
	public String getValueAsString(E row, E col) {
		return String.format(valueFormat, row, col, getValue(row, col));
	}
	
	/**
	 * Returns a String representation of all matrix entries.
	 * @return String representation of all matrix entries
	 * @see #getValueAsString(Object, Object)
	 */
	public String getValuesAsString() {
		StringBuilder builder = new StringBuilder();
		for(E row: rowKeys.keySet())
			for(E col: colKeys.keySet()) {
				builder.append(getValueAsString(row, col));
				builder.append('\n');
			}
		return builder.toString();
	}
	
	public List<T> getValues(){
		List<T> result = new ArrayList<T>();
		for(List<T> l1: this)
			for(T val: l1)
				result.add(val);
		return result;
	}
	
	public void setAllValuesTo(T value){
		for(E row: rowKeys()){
			for(E col: colKeys()){
				putValue(row, col, value);
			}
		}
	}
	
	public void replaceNullsWith(T value){
		for(E row: rowKeys()){
			for(E col: colKeys()){
				if(getValue(row, col) == null){
					putValue(row, col, value);
				}
			}
		}
	}

	/**
	 * Checks if the given col-key is already known and optionally generates a new key mapping.<br>
	 * @param key Col-value of indexing type
	 */
	protected void ensureColKey(E key) {
		if (!colKeys.containsKey(key)) {
			colKeys.put(key, colKeys.size());
			addCol();
		}
	}
	
	/**
	 * Checks if the given row-key is already known and optionally generates a new key mapping.<br>
	 * @param key Row-value of indexing type
	 */
	protected void ensureRowKey(E key) {
		if (!rowKeys.containsKey(key)) {
			rowKeys.put(key, rowKeys.size());
			addRow();
		}
	}

	/**
	 * Adds a new row to the matrix.
	 */
	protected void addRow() {
		add(createInitializedList(colKeys.size()));
	}

	/**
	 * Adds a new col to the matrix.
	 */
	protected void addCol() {
		for (ArrayList<T> list : this)
			list.add(null);
	}

	/**
	 * Creates a new list of specified size initialized with <code>null</code>-values.
	 * @param size 
	 * @return A new list of size {@link size} initialized with <code>null</code>-values
	 */
	protected ArrayList<T> createInitializedList(int size) {
		ArrayList<T> list = new ArrayList<T>(size);
		for (int i = 0; i < size; i++)
			list.add(null);
		return list;
	}
	
	public Set<E> rowKeys(){
		return rowKeys.keySet();
	}
	
	public Set<E> colKeys(){
		return colKeys.keySet();
	}

	/**
	 * Returns a String representation of the matrix.
	 * @return String representation of the matrix
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		builder.append(Arrays.toString(colKeys.keySet().toArray()));
		builder.append('\n');
		Iterator<E> iter = rowKeys.keySet().iterator();
		for (ArrayList<T> list : this) {
			builder.append(iter.next());
			builder.append(" ");
			builder.append(Arrays.toString(list.toArray()));
			builder.append('\n');
		}
		return builder.toString();
	}

}
