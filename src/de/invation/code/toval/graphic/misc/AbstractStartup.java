package de.invation.code.toval.graphic.misc;

import de.invation.code.toval.validate.ExceptionDialog;

public abstract class AbstractStartup {

    protected AbstractStartup() {
        String osType = System.getProperty("os.name");
        if (osType.equals("Mac OS") || osType.equals("Mac OS X")) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getToolName());
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getToolName());
        }

        try {
            startApplication();
        } catch (Exception e) {
            ExceptionDialog.showException(null, "Internal Exception", e, true);
//			JOptionPane.showMessageDialog(null, "Cannot launch application.\nReason: " + e.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
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

    protected abstract String getToolName();

    protected abstract void startApplication() throws Exception;

}
