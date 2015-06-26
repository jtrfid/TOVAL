/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.misc.StringUtils;
import de.invation.code.toval.properties.AbstractProperties;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author stocker
 * @param <E> Enumeration type used for property names
 */
public abstract class AbstractWorkingDirectoryProperties<E> extends AbstractProperties {

    public static final String DEFAULT_WORKING_DIRECTORY = ".";
    public static final String DEFAULT_WORKING_DIRECTORY_NAME = "WorkingDirectory";
    public static final String DEFAULT_WORKING_DIRECTORY_DESCRIPTOR = "Working Directory";
    public static final String DEFAULT_PROPERTY_FILE_NAME = "Properties";
    public static final String PROPERTY_NAME_WORKING_DIRECTORY = "WORKING_DIRECTORY";
    public static final String PROPERTY_NAME_KNOWN_WORKING_DIRECTORIES = "KNOWN_WORKING_DIRECTORIES";

    private String applicationPath = null;

    private Set<WDPropertyChangeListener> listeners = new HashSet<WDPropertyChangeListener>();

    protected AbstractWorkingDirectoryProperties() throws IOException {
        
        try {
            load(getPropertyFileName());
        } catch (IOException e) {
            // Create new property file.
            loadDefaultProperties();
            store();
        }
        // get current dir
        applicationPath = new File(".").getCanonicalPath();
    }
    
    protected abstract AbstractProjectComponents getProjectComponents() throws Exception;

    public String getDefaultWorkingDirectory() {
        return DEFAULT_WORKING_DIRECTORY;
    }
    
    public String getWorkingDirectoryDescriptor() {
        return DEFAULT_WORKING_DIRECTORY_DESCRIPTOR;
    }

    public String getDefaultWorkingDirectoryName() {
        return DEFAULT_WORKING_DIRECTORY_NAME;
    }

    public String getPropertyFileName() {
        return DEFAULT_PROPERTY_FILE_NAME;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    //------- Property setting -------------------------------------------------------------
    protected void setProperty(E property, Object value) {
        props.setProperty(property.toString(), value.toString());
    }

    protected String getProperty(E property) {
        return props.getProperty(property.toString());
    }

    protected void removeProperty(E property) {
        props.remove(property.toString());
    }

    //------- Working Directory ------------------------------------------------------------
    public void setWorkingDirectory(String directory, boolean reloadComponents) throws ProjectComponentException {
        validateWorkingDirectory(directory, false);
        
        props.setProperty(PROPERTY_NAME_WORKING_DIRECTORY, directory);

        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            directoryFile.mkdir();
        }

        if (reloadComponents) {
            try{
                getProjectComponents().reloadComponents();
            } catch(Exception e){
                throw new ProjectComponentException("Cannot reload components", e);
            }
        }
    }

    public String getWorkingDirectory() throws PropertyException {
        String propertyValue = props.getProperty(PROPERTY_NAME_WORKING_DIRECTORY);
        if (propertyValue == null) {
            throw new PropertyException(PROPERTY_NAME_WORKING_DIRECTORY, propertyValue);
        }
        validatePath(propertyValue);
        return propertyValue;
    }

    public void removeWorkingDirectory() {
        props.remove(PROPERTY_NAME_WORKING_DIRECTORY);
    }

	//------- Known Working Directories ----------------------------------------------------
    public void addKnownWorkingDirectory(String workingDirectory, boolean createSubdirectories) {
        validateWorkingDirectory(workingDirectory, createSubdirectories);
        Set<String> currentDirectories = getKnownWorkingDirectories();
        currentDirectories.add(workingDirectory);
        props.setProperty(PROPERTY_NAME_KNOWN_WORKING_DIRECTORIES, ArrayUtils.toString(prepareWorkingDirectories(currentDirectories)));
    }

    public void removeKnownWorkingDirectory(String simulationDirectory) {
        validateStringValue(simulationDirectory);
        Set<String> currentDirectories = getKnownWorkingDirectories();
        currentDirectories.remove(simulationDirectory);
        props.setProperty(PROPERTY_NAME_KNOWN_WORKING_DIRECTORIES, ArrayUtils.toString(prepareWorkingDirectories(currentDirectories)));
    }

    private String[] prepareWorkingDirectories(Set<String> directories) {
        String[] result = new String[directories.size()];
        int count = 0;
        for (String directory : directories) {
            result[count++] = "'" + directory + "'";
        }
        return result;
    }

    public Set<String> getKnownWorkingDirectories() {
        Set<String> result = new HashSet<String>();
        String propertyValue = props.getProperty(PROPERTY_NAME_KNOWN_WORKING_DIRECTORIES);
        if (propertyValue == null) {
            return result;
        }
        return new HashSet<String>(StringUtils.splitArrayStringQuoted(propertyValue, '\''));
    }

    
    //------- Validation -------------------------------------------------------------------
    
    public void validateWorkingDirectory(String directory, boolean createSubdirectories) throws ParameterException {
        validatePath(directory);
        for(String subDirectory: getSubDirectoriesForValidation())
            checkSubDirectory(directory, subDirectory, createSubdirectories);
    }
    
    protected abstract Set<String> getSubDirectoriesForValidation();

    private void checkSubDirectory(String workingDirectory, String subDirectoryName, boolean ensureSubdirectory) {
        File dir = new File(workingDirectory + subDirectoryName);
        if (!dir.exists()) {
            if (!ensureSubdirectory) {
                throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Corrupt structure of working directory.\nMissing subdirectory: " + subDirectoryName);
            }
            dir.mkdir();
        }
    }

    public void store() throws IOException {
        try {
            store(getPropertyFileName());
        } catch (IOException e) {
            throw new IOException("Cannot create/store properties file on disk.");
        }
    }
}
