package de.invation.code.toval.graphic;

import java.awt.Window;

import javax.swing.JOptionPane;

/**
 * 
 * @author Thomas Stocker
 */
public abstract class ConditionalInputDialog {
	
	private Window parent = null;
	private String title = null;
	private String message = null;

	public ConditionalInputDialog(Window parent, String message,  String title) {
		this.parent = parent;
		this.title = title;
		this.message = message;
	}
	
	public String requestInput(){
		String input = null;
		while(((input = JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE)) != null) && !isValid(input)){
			JOptionPane.showMessageDialog(parent, getErrorMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
			input = null;
		}
		return input;
	}
	
	protected abstract String getErrorMessage();

	protected abstract boolean isValid(String input);

}
