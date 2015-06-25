/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.soabase;

import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.misc.wd.AbstractComponentContainer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author stocker
 * @param <C>
 */
public abstract class AbstractSOABaseContainer<C extends SOABase, P extends SOABaseProperties> extends AbstractComponentContainer<C>{
    
    public static final boolean DEFAULT_IGNORE_INCOMPATIBLE_FILES = true;
    protected boolean ignoreIncompatibleFiles = DEFAULT_IGNORE_INCOMPATIBLE_FILES;

    protected AbstractSOABaseContainer(String serializationPath) {
        super(serializationPath);
    }

    protected AbstractSOABaseContainer(String serializationPath, SimpleDebugger debugger) {
        super(serializationPath, debugger);
    }

    public void setIgnoreIncompatibleFiles(boolean ignoreIncompatibleFiles){
        this.ignoreIncompatibleFiles = ignoreIncompatibleFiles;
    }
    
    @Override
    protected void serializeComponent(C component, String serializationPath, String fileName) throws Exception {
        File pathToStore = new File(serializationPath, fileName);
	component.getProperties().store(pathToStore.getAbsolutePath());
    }

    @Override
    protected C loadComponentFromFile(String file) throws Exception {
        P properties = crearteNewProperties();
        try {
            properties.load(file);
        } catch (IOException e) {
            throw new IOException("Cannot load properties file: " + FileUtils.separateFileNameFromEnding(file) + ".");
        }
        Class<?> baseClass = null;
        try{
            baseClass = properties.getBaseClass();
        } catch(Exception e){
            throw new Exception("Cannot extract property class information from properties file", e);
        }
        if(!properties.getClass().equals(baseClass)){
            if(ignoreIncompatibleFiles){
                return null;
            }
            throw new Exception("Unexpected SOABase type, expected " + properties.getBaseClass().getSimpleName() + " but got " + baseClass.getSimpleName());
        }
        
        C result = createSOABaseFromProperties(properties);
        result.setName(properties.getName());
        Set<String> activities = properties.getActivities();
        if (activities != null && !activities.isEmpty()) {
            result.addActivities(activities);
        }
        Set<String> subjects = properties.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            result.addSubjects(subjects);
        }
        Set<String> attributes = properties.getObjects();
        if (attributes != null && !attributes.isEmpty()) {
            result.addObjects(attributes);
        }
        
        loadCustomContent(result, properties);
        
        return result;
    }
    
    protected abstract P crearteNewProperties() throws Exception;
    
    protected abstract C createSOABaseFromProperties(P properties) throws Exception;
    
    protected abstract void loadCustomContent(C SOABase, P properties) throws Exception;

    @Override
    public Set<String> getAcceptedFileEndings() {
        return new HashSet<>(Arrays.asList(""));
    }

}
