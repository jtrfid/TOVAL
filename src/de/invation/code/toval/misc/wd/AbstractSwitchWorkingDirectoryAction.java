package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import java.awt.event.ActionEvent;
import java.awt.Window;

public abstract class AbstractSwitchWorkingDirectoryAction extends AbstractWorkingDirectoryAction {

    private static final long serialVersionUID = 4540373111307405160L;

    public AbstractSwitchWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties) {
        this(parentWindow, properties, null);
    }
    
    public AbstractSwitchWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties, SimpleDebugger debugger) {
        super(parentWindow, "Switch " + properties.getWorkingDirectoryDescriptor(), properties, debugger);
    }

    @Override
    public void actionProcedure(ActionEvent e) throws Exception {
        String workingDirectory = launchWorkingDirectoryDialog(parent);
        if (workingDirectory == null) {
            return;
        }

        if (!properties.getWorkingDirectory().equals(workingDirectory)) {
            properties.setWorkingDirectory(workingDirectory, true);
        }
    }
    
    protected abstract String launchWorkingDirectoryDialog(Window parent) throws Exception;

}
