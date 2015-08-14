package de.invation.code.toval.misc.wd;

public interface ComponentListener<O> {
	
	public void componentAdded(O component) throws ProjectComponentException;
	
	public void componentRemoved(O component) throws ProjectComponentException;
	
	public void componentRenamed(O component, String oldName, String newName) throws ProjectComponentException;
	
	public void componentsChanged() throws ProjectComponentException;
}
