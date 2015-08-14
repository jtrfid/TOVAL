package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.validate.ExceptionListener;
import de.invation.code.toval.validate.Validate;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 *
 * @author stocker
 */
public abstract class AbstractWorkingDirectoryAction extends AbstractAction {

    private static final long serialVersionUID = 6658565129248580915L;

    public static final String PROPERTY_NAME_SUCCESS = "success";

    protected Window parent;
    protected AbstractWorkingDirectoryProperties properties;
    protected SimpleDebugger debugger;

    private final Set<ExceptionListener> exceptionListeners = new HashSet<>();
    
    public AbstractWorkingDirectoryAction(Window parentWindow, String name, AbstractWorkingDirectoryProperties properties) {
        this(parentWindow, name, properties, null);
    }
    
    public AbstractWorkingDirectoryAction(Window parentWindow, String name, AbstractWorkingDirectoryProperties properties, SimpleDebugger debugger) {
        super(name);
        this.parent = parentWindow;
        Validate.notNull(properties);
        this.properties = properties;
    }
    
    public boolean addExceptionListener(ExceptionListener listener){
        return exceptionListeners.add(listener);
    }
    
    public boolean removeExceptionListener(ExceptionListener listener){
        return exceptionListeners.remove(listener);
    }

    protected void addKnownWorkingDirectory(String workingDirectory, boolean createSubfolders) throws Exception {
        properties.addKnownWorkingDirectory(workingDirectory, createSubfolders);
        properties.setWorkingDirectory(workingDirectory, true);
        properties.store();
        putValue(AbstractWorkingDirectoryProperties.PROPERTY_NAME_WORKING_DIRECTORY, workingDirectory);
        putValue(PROPERTY_NAME_SUCCESS, true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            actionProcedure(e);
        } catch (Exception ex) {
            reportException(ex);
        }
    }
    
    protected void reportException(Exception e){
        if(debugger != null) debugger.message("Exception in action \"" + AbstractWorkingDirectoryAction.this.getClass().getSimpleName() + "\": " + e.getMessage());
        for(ExceptionListener listener: exceptionListeners)
            listener.exceptionOccurred(this, e);
    }
    
    protected abstract void actionProcedure(ActionEvent e) throws Exception;

}
