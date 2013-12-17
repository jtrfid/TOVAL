package de.invation.code.toval.file;

import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * This class can be used to request a file/directory with the help of a JFileChooserDialog.<br>
 * It allows to customize the accepted files/directories by overriding the method {@link #isValid(File)}.<br>
 * The user will be shown a JFileChooserDialog until he hits cancel or selects a valid file.
 * 
 * @author Thomas Stocker
 */
public abstract class ConditionalFileChooser {
	
	private JFileChooser fileChooser = null;
	private Window parent = null;

	public ConditionalFileChooser(Window parent, FileChooserType type, String title) {
		this.parent = parent;
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		switch(type){
		case DIRECTORY:
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			break;
		case FILE:
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			break;
		}	
	}
	
	public String chooseFile(){
		String fileName = null;
		while(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION && !isValid(fileChooser.getSelectedFile())){
			JOptionPane.showMessageDialog(parent, getErrorMessage(), "Invalid or corrupted file/directory", JOptionPane.ERROR_MESSAGE);
			fileName = null;
		}
		return fileName;
	}
	
	protected abstract String getErrorMessage();

	protected abstract boolean isValid(File file);
	
	public enum FileChooserType {
		DIRECTORY, FILE;
	}

}
