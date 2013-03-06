package types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Set implementation that allows random access by extending <code>ArrayList</code>.<br>
 * It extends <code>ArrayList</code> with the set feature, that it contains no two equal elements.
 * @param <E> Type of elements
 * 
 * @author Thomas Stocker
 */
public class HashList<E> extends ArrayList<E> implements Set<E> {

	private static final long serialVersionUID = 1L;
	
	public HashList() {}
	
	public HashList(Collection<? extends E> c) {
		super();
		addAll(c);
	}
	
    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws    IndexOutOfBoundsException if index out of range
     *		  <tt>(index &lt; 0 || index &gt;= size())</tt>.
     */
	@Override
    public E set(int index, E element) {
    	if(element!=null && !contains(element)){
    		return super.set(index, element);
    	}
    	return get(index);
    }
    
    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    public boolean add(E o) {
    	if(o!=null && !contains(o)){
    		return super.add(o);
    	}
    	return false;
    }
    
    public boolean add(E[] arr) {
    	if(arr != null && arr.length>0) {
    		boolean modified = false;
    		for(int i=0; i<arr.length; i++) {
    			if(arr[i]!=null && !contains(arr[i])) {
    				modified = add(arr[i]);
    			}
    		}
    		return modified;
    	}
    	return false;
    }
    
    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @throws    IndexOutOfBoundsException if index is out of range
     *		  <tt>(index &lt; 0 || index &gt; size())</tt>.
     */
    public void add(int index, E element) {
    	if(element!=null && !contains(element)){
    		super.add(index, element);
    	}
    }
    
    /**
     * Appends all of the elements in the specified Collection to the end of
     * this list, in the order that they are returned by the
     * specified Collection's Iterator.  The behavior of this operation is
     * undefined if the specified Collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified Collection is this list, and this
     * list is nonempty.)
     *
     * @param c the elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws    NullPointerException if the specified collection is null.
     */
    public boolean addAll(Collection<? extends E> c) {
    	boolean modified = false;
    	for(E object: c) {
    		if(object!=null && !contains(object)) {
    			modified = add(object);
    		}
    	}
    	return modified;
    }

    /**
     * Inserts all of the elements in the specified Collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified Collection's iterator.
     *
     * @param index index at which to insert first element
     *		    from the specified collection.
     * @param c elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws    IndexOutOfBoundsException if index out of range <tt>(index
     *		  &lt; 0 || index &gt; size())</tt>.
     * @throws    NullPointerException if the specified Collection is null.
     */
    public boolean addAll(int index, Collection<? extends E> c) {
    	removeNulls(c);
    	c.removeAll(this);
    	return super.addAll(index, c);
    }
    
    private void removeNulls(Collection<? extends E> c){
    	while(c.contains(null)){
    		c.remove(null);
    	}
    }
    
    @Override
    public void clear() {
        super.clear();
    }
    
    public HashList<E> clone(){
    	HashList<E> result = new HashList<E>();
    	for(E e: this)
    		result.add(e);
    	return result;
    }

}
