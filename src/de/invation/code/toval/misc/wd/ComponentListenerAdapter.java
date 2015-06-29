package de.invation.code.toval.misc.wd;

@SuppressWarnings("rawtypes")
public class ComponentListenerAdapter<O> implements ComponentListener<O> {

	@Override
	public void componentAdded(O component) throws ProjectComponentException{}

	@Override
	public void componentRemoved(O component) throws ProjectComponentException{}
	
	@Override
	public void componentRenamed(O component, String oldName, String newName) throws ProjectComponentException{}

	@Override
	public void componentsChanged() throws ProjectComponentException{}

}
