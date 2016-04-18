package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class OpenWorkingDirectoryAction extends AbstractWorkingDirectoryAction {

    private static final long serialVersionUID = 3421975574956233676L;

    public OpenWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties) {
        this(parentWindow, properties, null);
    }

    public OpenWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties, SimpleDebugger debugger) {
        super(parentWindow, "Open Directory", properties, debugger);
    }

    @Override
    public void actionProcedure(ActionEvent e) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose existing " + properties.getWorkingDirectoryDescriptor().toLowerCase());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String workingDirectory = file.getAbsolutePath() + System.getProperty("file.separator");
            addKnownWorkingDirectory(workingDirectory, false);
            JOptionPane.showMessageDialog(null, "Please restart SWAT to load the choosen Working Directory");
        }
    }

}
