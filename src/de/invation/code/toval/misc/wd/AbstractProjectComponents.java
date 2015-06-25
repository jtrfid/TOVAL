/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.validate.ExceptionDialog;
import de.invation.code.toval.validate.Validate;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author stocker
 */
public abstract class AbstractProjectComponents {
    
    private Set<AbstractComponentContainer> componentContainers = new HashSet<>();
    private SimpleDebugger debugger = null;
    
    protected AbstractProjectComponents() throws ProjectComponentException{
        this(null);
    }
    
    protected AbstractProjectComponents(SimpleDebugger debugger) throws ProjectComponentException{
        this.debugger = debugger;
        addComponentContainers();
        try {
            loadComponents();
        } catch (Exception e) {
            ExceptionDialog.showException(null, "Project component Exception", new Exception("Cannot load components", e), true);
        }
    }
    
    protected abstract void addComponentContainers() throws ProjectComponentException;
    
    protected final void addComponentContainer(AbstractComponentContainer container){
        Validate.notNull(container);
        this.componentContainers.add(container);
    }
    
    public final void reloadComponents() throws ProjectComponentException {
        clearContainers(false);
        loadComponents();
    }
    
    public final void writeFilesToDisk() throws ProjectComponentException{
        for(AbstractComponentContainer container: componentContainers){
            container.storeComponents();
        }
    }
    
    protected final void clearContainers(boolean removeFromDisk) throws ProjectComponentException{
        for(AbstractComponentContainer container: componentContainers){
            container.removeComponents(removeFromDisk);
        }
    }
    
    public final void loadComponents() throws ProjectComponentException{
        for(AbstractComponentContainer container: componentContainers){
            container.loadComponents();
        }
    }
    
    protected void debugMessage(String message){
        if(debugger != null){
            if(message == null){
                debugger.newLine();
            } else {
                debugger.message(message);
            }
        }
    }
    
}
