package de.invation.code.toval.misc.wd;

@SuppressWarnings("rawtypes")
public class ComponentListenerAdapter<O> implements ComponentListener<O> {

	@Override
	public void componentAdded(O component) {}

	@Override
	public void componentRemoved(O component) {}
	
	@Override
	public void componentRenamed(O component) {}

	@Override
	public void componentsChanged() {}

}
