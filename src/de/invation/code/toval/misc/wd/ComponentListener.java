package de.invation.code.toval.misc.wd;

@SuppressWarnings("rawtypes")
public interface ComponentListener<O> {
	
	public void componentAdded(O component);
	
	public void componentRemoved(O component);
	
	public void componentRenamed(O component);
	
	public void componentsChanged();
	
}
