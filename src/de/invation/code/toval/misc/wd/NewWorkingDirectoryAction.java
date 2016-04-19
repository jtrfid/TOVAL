package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.SimpleDebugger;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author stocker
 */
public class NewWorkingDirectoryAction extends AbstractWorkingDirectoryAction {

    private static final long serialVersionUID = 3421975574956233676L;

    public NewWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties) {
        this(parentWindow, properties, null);
    }

    public NewWorkingDirectoryAction(Window parentWindow, AbstractWorkingDirectoryProperties properties, SimpleDebugger debugger) {
        super(parentWindow, "New Directory", properties, debugger);
    }

    @Override
    public void actionProcedure(ActionEvent e) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose location for new " + properties.getWorkingDirectoryDescriptor().toLowerCase());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String directoryLocation = file.getAbsolutePath();
            File dir = new File(directoryLocation + "/" + properties.getDefaultWorkingDirectoryName());
            if (dir.exists()) {
                int count = 1;
                while ((dir = new File(directoryLocation + "/" + properties.getDefaultWorkingDirectoryName() + count)).exists()) {
                    count++;
                }
            }
            dir.mkdir();
            String workingDirectory = dir.getAbsolutePath() + "/";
            
            if (properties.getKnownWorkingDirectories().size() != 0) {
            	JOptionPane.showMessageDialog(null, "Please restart SWAT to load the new Working Directory");
            }
            addKnownWorkingDirectory(workingDirectory, true);
        }
    }
}
