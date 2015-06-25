/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.misc.NamedComponent;
import de.invation.code.toval.validate.Validate;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author stocker
 * @param <O>
 */
public abstract class AbstractComponentContainer<O extends NamedComponent> {

    private final Map<String, O> components = new HashMap<>();
    private String serializationPath = null;

    private SimpleDebugger debugger = null;

    private final ComponentListenerSupport<O> listenerSupport = new ComponentListenerSupport<>();

    public AbstractComponentContainer(String serializationPath) {
        this(serializationPath, null);
    }

    public AbstractComponentContainer(String serializationPath, SimpleDebugger debugger) {
        if(mandatoryDirectory()){
            Validate.directory(serializationPath);
        }
        this.serializationPath = serializationPath;
        this.debugger = debugger;
    }
    
    public abstract String getComponentDescriptor();

    public boolean addComponentListener(ComponentListener<O> listener) {
        return listenerSupport.addListener(listener);
    }

    public boolean removeComponentListener(ComponentListener<O> listener) {
        return listenerSupport.removeListener(listener);
    }

    public Set<String> getComponentNames() {
        return new HashSet<>(components.keySet());
    }

    public Collection<O> getComponents() {
        return Collections.unmodifiableCollection(components.values());
    }

    public Map<String, O> getComponentMap() {
        return Collections.unmodifiableMap(components);
    }

    public O getComponent(String componentName) throws ProjectComponentException {
        validateComponent(componentName);
        return components.get(componentName);
    }
    
    protected boolean mandatoryDirectory(){
        return true;
    }

    public void loadComponents() throws ProjectComponentException {
        if(!mandatoryDirectory() && !new File(serializationPath).exists())
            return;
        try {
            loadComponentsFromDirectory(serializationPath);
        } catch (Exception e) {
            throw new ProjectComponentException("Cannot load components.", e);
        }
    }

    public final void loadComponentsFromDirectory(String directory) throws Exception {
        Collection<String> fileNames = null;
        try {
            boolean allFiles = getAcceptedFileEndings().size() == 1 && getAcceptedFileEndings().iterator().next().isEmpty();
            if(allFiles){
                fileNames = FileUtils.getFileNamesInDirectory(directory, true);
            } else {
                fileNames = FileUtils.getFileNamesInDirectory(directory, true, getAcceptedFileEndings());
            }
        } catch (Exception e) {
            debugMessage("Exception: Cannot extract file names: " + e.getMessage());
            return;
        }

        for (String fileName : fileNames) {
            debugMessage("Trying to load "+getComponentDescriptor()+" from file \"" + FileUtils.getFileWithoutEnding(fileName) + "\"");
            O component = null;
            try {
                component = loadComponentFromFile(fileName);
            } catch (Exception e) {
                debugMessage("Exception: Error while loading "+getComponentDescriptor()+" from file: " + e.getMessage());
                return;
            }
            if (component != null) {
                debugMessage("Successfully loaded "+getComponentDescriptor()+" from file.");
                try {
                    addComponent(component, false);
                } catch (Exception e) {
                    debugMessage("Exception: Cannot add "+getComponentDescriptor()+" to container: " + e.getMessage());
                    return;
                }
                debugMessage("Successfully added "+getComponentDescriptor()+" to container.");
            } else {
                debugMessage("Exception: Cannot load "+getComponentDescriptor()+" from file");
            }
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

    protected abstract O loadComponentFromFile(String file) throws Exception;

    public abstract Set<String> getAcceptedFileEndings();

    public void removeComponents(boolean removeFilesFromDisk) throws ProjectComponentException {
        for (String componentName : getComponentNames()) {
            removeComponent(componentName, removeFilesFromDisk);
        }
    }

    public boolean removeComponent(String componentName, boolean removeFromDisk) throws ProjectComponentException {
        validateComponent(componentName);
        if (components.remove(componentName) != null) {
            if (removeFromDisk) {
                try {
                    FileUtils.deleteFile(serializationPath + getSerializationFileName(getComponent(componentName)));
                } catch (Exception e) {
                    throw new ProjectComponentException("Cannot delete component file from disk.", e);
                }
            }
            listenerSupport.notifyComponentRemoved(getComponent(componentName));
            return true;
        }
        return false;
    }

    public void storeComponents() throws ProjectComponentException {
        for (String componentName : getComponentNames()) {
            storeComponent(componentName);
        }
    }

    public void storeComponent(String componentName) throws ProjectComponentException {
        validateComponent(componentName);
        try {
            serializeComponent(getComponent(componentName), serializationPath, getSerializationFileName(getComponent(componentName)));
        } catch (Exception e) {
            throw new ProjectComponentException("Cannot store component.", e);
        }
    }

    /**
     * Checks, if there are components.
     *
     * @return <code>true</code> if there is at least one component model;<br>
     * <code>false</code> otherwise.
     */
    public boolean containsComponents() {
        return !components.isEmpty();
    }

    /**
     * Checks, if there is a component with the given name.
     *
     * @param name Name of the component in question
     * @return <code>true</code> if there is at least one component model;<br>
     * <code>false</code> otherwise.
     */
    public boolean containsComponent(String name) {
        return components.containsKey(name);
    }

    /**
     * Adds a new component.<br>
     * The component is also stores as property-file in the working directory.
     *
     * @param component The component to add.
     * @throws de.invation.code.toval.misc.wd.ProjectComponentException
     */
    public void addComponent(O component) throws ProjectComponentException {
        addComponent(component, true);
    }

    /**
     * Adds a new component.<br>
     * Depending on the value of the store-parameter, the component is also
     * stored in the serialization directory.
     *
     * @param component The new component to add.
     * @param storeToFile Indicates if the net should be stored to disk.
     * @throws de.invation.code.toval.misc.wd.ProjectComponentException
     */
    public void addComponent(O component, boolean storeToFile) throws ProjectComponentException {
        Validate.notNull(component);
        Validate.notNull(storeToFile);
        components.put(component.getName(), component);
        if (storeToFile) {
            storeComponent(component.getName());
        }
    }

    protected abstract void serializeComponent(O component, String serializationPath, String fileName) throws Exception;

    protected String getSerializationFileName(O component) {
        return component.getName();
    }

    public void renameComponent(String oldName, String newName) throws ProjectComponentException {
        validateComponent(oldName);
        O component = getComponent(oldName);
        component.setName(newName);
        components.remove(oldName);
        components.put(newName, component);
        storeComponent(newName);
        listenerSupport.notifyComponentRenamed(component);
    }

    private void validateComponent(String componentName) throws ProjectComponentException {
        if (!components.containsKey(componentName)) {
            throw new ProjectComponentException("No " + getComponentDescriptor() + " with name \"" + componentName + "\"");
        }
    }
}
