package de.invation.code.toval.graphic.misc;

import de.invation.code.toval.validate.ExceptionDialog;

public abstract class AbstractWorkingDirectoryStartup extends AbstractStartup {
    
    protected boolean chooseWorkingDirectory() {
        String workingDirectory = null;
        try {
            workingDirectory = launchWorkingDirectoryDialog();
        } catch (Exception e) {
            ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot launch \""+getWorkingDirectoryDescriptor().toLowerCase()+"\" dialog", e), true);
            return false;
        }
        if (workingDirectory == null) {
            //User aborted 
//            ExceptionDialog.showException(null, "Invalid Directory", new Exception("Chosen \""+getWorkingDirectoryDescriptor().toLowerCase()+"\" is NULL"), true);
            return false;
        }
     
        try {
            setWorkingDirectory(workingDirectory);
            return true;
        } catch (Exception e1) {
            ExceptionDialog.showException(null, "Internal Exception", new Exception("Cannot set \""+getWorkingDirectoryDescriptor().toLowerCase()+"\"", e1), true);
            return false;
        }
    }
    
    protected abstract String getWorkingDirectoryDescriptor();
    
    protected abstract void setWorkingDirectory(String workingDirectory) throws Exception;
    
    protected abstract String launchWorkingDirectoryDialog() throws Exception;

}
