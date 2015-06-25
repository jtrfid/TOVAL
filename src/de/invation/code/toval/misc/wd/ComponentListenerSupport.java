package de.invation.code.toval.misc.wd;

import de.invation.code.toval.event.AbstractListenerSupport;
import de.invation.code.toval.misc.soabase.SOABase;

@SuppressWarnings("rawtypes")
public class ComponentListenerSupport<O> extends AbstractListenerSupport<ComponentListener> {

    private static final long serialVersionUID = -5976107306359206805L;

    public void notifyComponentAdded(O component) {
        for (ComponentListener listener : listeners) {
            listener.componentAdded(component);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentRemoved(O component) {
        for (ComponentListener listener : listeners) {
            listener.componentRemoved(component);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentRenamed(O component) {
        for (ComponentListener listener : listeners) {
            listener.componentRenamed(component);
        }
        notifyComponentsChanged();
    }

    public void notifyComponentsChanged() {
        for (ComponentListener listener : listeners) {
            listener.componentsChanged();
        }
    }

}
