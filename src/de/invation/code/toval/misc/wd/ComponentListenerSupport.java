package de.invation.code.toval.misc.wd;

import de.invation.code.toval.event.AbstractListenerSupport;
import de.invation.code.toval.misc.soabase.SOABase;

@SuppressWarnings("rawtypes")
public class ComponentListenerSupport<O> extends AbstractListenerSupport<ComponentListener> {

    private static final long serialVersionUID = -5976107306359206805L;

    public void notifyComponentAdded(O component) throws ProjectComponentException{
        for (ComponentListener listener : listeners) {
            listener.componentAdded(component);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentRemoved(O component) throws ProjectComponentException{
        for (ComponentListener listener : listeners) {
            listener.componentRemoved(component);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentRenamed(O component, String oldName, String newName) throws ProjectComponentException{
        for (ComponentListener listener : listeners) {
            listener.componentRenamed(component, oldName, newName);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentsChanged() throws ProjectComponentException{
        for (ComponentListener listener : listeners) {
            listener.componentsChanged();
        }
    }

}
