/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.misc.NamedComponent;
import de.invation.code.toval.validate.ExceptionDialog;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author stocker
 * @param <O>
 */
public abstract class AbstractComponentContainer<O extends NamedComponent> {

    public static final boolean DEFAULT_IGNORE_INCOMPATIBLE_FILES = true;
    public static final boolean DEFAULT_NOTIFY_LISTENERS = true;
    public static final boolean DEFAULT_USE_SUBDIRECTORIES_FOR_COMPONENTS = false;
    public static final String DEFAULT_FILE_ENDING = "";
    
    private static final String componentDirectoryFormat = "%s%s/";
    private static final String componentFileFormat = "%s%s%s";

    private boolean ignoreIncompatibleFiles = DEFAULT_IGNORE_INCOMPATIBLE_FILES;
    private final Map<String, O> components = new HashMap<>();
    private final Map<String, File> componentFiles = new HashMap<>();
    private String basePath = null;
    private SimpleDebugger debugger = null;
    private boolean useSubdirectoriesForComponents = DEFAULT_USE_SUBDIRECTORIES_FOR_COMPONENTS;

    protected final ComponentListenerSupport<O> listenerSupport = new ComponentListenerSupport<>();

    public AbstractComponentContainer(String basePath) {
        this(basePath, null);
    }

    public AbstractComponentContainer(String basePath, SimpleDebugger debugger) {
        if (mandatoryDirectory()) {
            Validate.directory(basePath);
        }
        this.basePath = basePath;
        this.debugger = debugger;
    }
    
    protected SimpleDebugger getDebugger(){
        return debugger;
    }
    
    public String getBasePath(){
        return basePath;
    }
    
    public void setUseSubdirectoriesForComponents(boolean useSubdirectoriesForComponents){
        this.useSubdirectoriesForComponents = useSubdirectoriesForComponents;
    }

    public void setIgnoreIncompatibleFiles(boolean ignoreIncompatibleFiles) {
        this.ignoreIncompatibleFiles = ignoreIncompatibleFiles;
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

    public List<O> getComponentsSorted(Comparator<O> comparator) {
        List<O> netList = new ArrayList<O>(getComponents());
        Collections.sort(netList, comparator);
        return netList;
    }

    public Map<String, O> getComponentMap() {
        return Collections.unmodifiableMap(components);
    }

    public O getComponent(String componentName) throws ProjectComponentException {
        validateComponent(componentName);
        return components.get(componentName);
    }

    public final File getComponentFile(String componentName) throws ProjectComponentException {
        validateComponent(componentName);
        return componentFiles.get(componentName);
    }

    protected boolean mandatoryDirectory() {
        return true;
    }

    public void loadComponents() throws ProjectComponentException {
        if (!mandatoryDirectory() && !new File(basePath).exists()) {
            return;
        }
        if (useSubdirectoriesForComponents) {
            Collection<File> subDirectories = null;
            try {
                subDirectories = FileUtils.getSubdirectories(basePath);
            } catch (Exception e) {
                debugMessage("Exception: Cannot identify subdirectories: " + e.getMessage());
                return;
            }
            for (File subDirectory : subDirectories) {
                try {
                    loadComponentsFromDirectory(subDirectory.getAbsolutePath());
                } catch (Exception e) {
                    throw new ProjectComponentException("Cannot load components from directory " + FileUtils.getDirName(subDirectory), e);
                }
            }
        } else {
            try {
                loadComponentsFromDirectory(basePath);
            } catch (Exception e) {
                throw new ProjectComponentException("Cannot load components from directory " + FileUtils.getDirName(basePath), e);
            }
        }
    }

    public final void loadComponentsFromDirectory(String directory) throws Exception {
        Collection<String> fileNames = null;
        try {
            boolean allFiles = getAcceptedFileEndings().size() == 1 && getAcceptedFileEndings().iterator().next().isEmpty();
            if (allFiles) {
                fileNames = FileUtils.getFileNamesInDirectory(directory, true);
            } else {
                fileNames = FileUtils.getFileNamesInDirectory(directory, true, getAcceptedFileEndings());
            }
        } catch (Exception e) {
            debugMessage("Exception: Cannot extract file names: " + e.getMessage());
            return;
        }

        for (String fileName : fileNames) {
            debugMessage("Trying to load " + getComponentDescriptor() + " from file \"" + FileUtils.getFileWithoutEnding(fileName) + "\"");
            O component = null;
            try {
                component = loadComponentFromFile(fileName);
            } catch (ParameterException e) {
                if (ignoreIncompatibleFiles && e.getErrorCode() == ErrorCode.INCOMPATIBILITY) {
                    debugMessage(e.getMessage());
                    debugMessage("Ignoring file \"" + FileUtils.getFileWithoutEnding(fileName) + "\"");
                    continue;
                } else {
                    debugMessage("Exception: Error while loading " + getComponentDescriptor() + " from file: " + e.getMessage());
                    continue;
                }
            } catch (Exception e) {
                debugMessage("Exception: Error while loading " + getComponentDescriptor() + " from file: " + e.getMessage());
                continue;
            }
            if (component == null) {
                debugMessage("Exception: Cannot load " + getComponentDescriptor() + " from file");
                continue;
            }

            debugMessage("Successfully loaded " + getComponentDescriptor() + " from file.");
            try {
                addComponent(component, false);
            } catch (Exception e) {
                debugMessage("Exception: Cannot add " + getComponentDescriptor() + " to container: " + e.getMessage());
                continue;
            }
            debugMessage("Successfully added " + getComponentDescriptor() + " to container.");

        }
    }

    protected void debugMessage(String message) {
        if (debugger != null) {
            if (message == null) {
                debugger.newLine();
            } else {
                debugger.message(message);
            }
        }
    }

    protected abstract O loadComponentFromFile(String file) throws Exception;

    public Set<String> getAcceptedFileEndings() {
        return new HashSet<String>(Arrays.asList(""));
    }

    public void removeComponents(boolean removeFilesFromDisk) throws ProjectComponentException {
        removeComponents(removeFilesFromDisk, DEFAULT_NOTIFY_LISTENERS);
    }
    
    public void removeComponents(boolean removeFilesFromDisk, boolean notifyListeners) throws ProjectComponentException {
        for (String componentName : getComponentNames()) {
            removeComponent(componentName, removeFilesFromDisk, notifyListeners);
        }
    }

    public boolean removeComponent(String componentName, boolean removeFromDisk) throws ProjectComponentException {
        return removeComponent(componentName, removeFromDisk, DEFAULT_NOTIFY_LISTENERS);
    }

    public boolean removeComponent(String componentName, boolean removeFromDisk, boolean notifyListeners) throws ProjectComponentException {
        validateComponent(componentName);
        if (components.remove(componentName) != null) {
            if (removeFromDisk) {
                if (useSubdirectoriesForComponents) {
                    try {
                        FileUtils.deleteDirectory(FileUtils.getPath(getComponentFile(componentName)), true);
                    } catch (Exception e) {
                        throw new ProjectComponentException("Cannot delete "+getComponentDescriptor()+" directory from disk.", e);
                    }
                } else {
                    try {
                        FileUtils.deleteFile(basePath + getSerializationFileName(getComponent(componentName)));
                    } catch (Exception e) {
                        throw new ProjectComponentException("Cannot delete "+getComponentDescriptor()+" file from disk.", e);
                    }
                }
            }
            componentFiles.remove(componentName);
            if (notifyListeners) {
                listenerSupport.notifyComponentRemoved(getComponent(componentName));
            }
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
            serializeComponent(getComponent(componentName), getComponentDirectory(componentName).getCanonicalPath(), getSerializationFileName(getComponent(componentName)).concat(getFileEndingForComponent(getComponent(componentName))));
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
        addComponent(component, storeToFile, DEFAULT_NOTIFY_LISTENERS);
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
    public void addComponent(O component, boolean storeToFile, boolean notifyListeners) throws ProjectComponentException {
        Validate.notNull(component);
        Validate.notNull(storeToFile);
        String componentName = component.getName();
        Validate.notNull(componentName);
        if(containsComponent(componentName))
            throw new ProjectComponentException("Container already contains component with name \"" +componentName+ "\"");
        
        File componentFile = null;
        try {
            File pathFile = getComponentDirectory(componentName);
            pathFile.mkdir();
            componentFile = getComponentFile(pathFile, componentName);
        } catch (Exception e) {
            ExceptionDialog.showException(null, "", e, true);
            throw new ProjectComponentException("Cannot create component file.", e);
        }
        
        components.put(componentName, component);
        componentFiles.put(componentName, componentFile);
        
        if (storeToFile) {
            try{
                storeComponent(component.getName());
            } catch(Exception e){
                throw new ProjectComponentException("Cannot store created component to disk.", e);
            }
        }
        if(notifyListeners){
            listenerSupport.notifyComponentAdded(component);
        }
    }

    protected abstract void serializeComponent(O component, String basePath, String fileName) throws Exception;

    protected String getSerializationFileName(O component) {
        return component.getName();
    }

    public void renameComponent(String oldName, String newName) throws ProjectComponentException {
        renameComponent(oldName, newName, DEFAULT_NOTIFY_LISTENERS);
    }

    public void renameComponent(String oldName, String newName, boolean notifyListeners) throws ProjectComponentException {
        validateComponent(oldName);
        if(containsComponent(newName))
            throw new ProjectComponentException("Container already contains " + getComponentDescriptor() + " with name \"" + newName + "\"");
        O component = getComponent(oldName);
        component.setName(newName);
        removeComponent(oldName, true, false);
        addComponent(component, true, false);
        if (notifyListeners) {
            listenerSupport.notifyComponentRenamed(component, oldName, newName);
        }
    }

    public void validateComponent(String componentName) throws ProjectComponentException {
        if (!components.containsKey(componentName)) {
            throw new ProjectComponentException("No " + getComponentDescriptor() + " with name \"" + componentName + "\"");
        }
        if (!componentFiles.containsKey(componentName)) {
            throw new ProjectComponentException("No file for " + getComponentDescriptor() + " with name \"" + componentName + "\"");
        }
    }

    public final File getComponentDirectory(String componentName) throws ProjectComponentException {
        if(!useSubdirectoriesForComponents){
            File path = new File(basePath);
            Validate.directory(path);
            return path;
        }
        
        try {
            File path = new File(String.format(componentDirectoryFormat, basePath, componentName));
            Validate.directory(path);
            return path;
        } catch (Exception e) {
            throw new ProjectComponentException("Cannot compose component directory.", e);
        }
    }
    
    private File getComponentFile(File pathFile, String componentName) throws ProjectComponentException {
        try {
            return new File(String.format(componentFileFormat, pathFile.getCanonicalPath(), componentName, ".pnml"));
        } catch (IOException e) {
            throw new ProjectComponentException("Cannot compose component file.", e);
        }
    }
    
    protected String getFileEndingForComponent(O component){
        return DEFAULT_FILE_ENDING;
    }
}