package de.invation.code.toval.graphic.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public abstract class AbstractModel{

    
    protected ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	listeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	listeners.remove(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
    	for(PropertyChangeListener l: listeners) {
    		l.propertyChange(evt);
    	}
    }

}
