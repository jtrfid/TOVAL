package de.invation.code.toval.graphic;

import java.awt.Window;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public class FileNameChooser extends ConditionalInputDialog {
	
	private static final String errorMessageFormat = "Invalid file name:\n%s";
	private static final String spacesError = "File name contains spaces";
	protected String errorMessage = "";
	private boolean allowSpaces = false;

	public FileNameChooser(Window parent, String message, String title, boolean allowSpaces) {
		super(parent, message, title);
		this.allowSpaces = allowSpaces;
	}

	@Override
	protected String getErrorMessage() {
		return String.format(errorMessageFormat, errorMessage);
	}

	@Override
	protected boolean isValid(String input) {
		try {
			Validate.fileName(input);
		} catch (ParameterException e) {
			errorMessage = e.getMessage();
			return false;
		}
		if(!allowSpaces && input.contains(" ")){
			errorMessage = spacesError;
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		new FileNameChooser(null, "Please choose a name", "Testtitle", false).requestInput();
	}

}
