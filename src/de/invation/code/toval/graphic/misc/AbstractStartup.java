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

    protected abstract String getToolName();

    protected abstract void startApplication() throws Exception;

}
