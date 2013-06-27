package de.invation.code.toval.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class LineBasedFileTransformer {
	
	protected Charset inputCharset = Charset.forName("UTF-8");
	protected Charset outputCharset = Charset.forName("UTF-8");
	protected File outputFile = null;
	protected boolean omitFirstLine = false;

	protected FileReader input = null;
	protected FileWriter output = null;
	
	
	//------- Constructors -------------------------------------------------------------------
	
	public LineBasedFileTransformer(){}
	
	public LineBasedFileTransformer(Charset charset) throws ParameterException{
		this(charset, charset);
	}
	
	public LineBasedFileTransformer(Charset inputCharset, Charset outputCharset) throws ParameterException{
		Validate.notNull(inputCharset);
		Validate.notNull(outputCharset);
		this.inputCharset = inputCharset;
		this.outputCharset = outputCharset;
	}
	
	
	//------- Getters and Setters ------------------------------------------------------------
	
	public String getInputCharset(){
		return inputCharset.toString();
	}
	
	public String getOutputCharset(){
		return inputCharset.toString();
	}
	
	public boolean omitsFirstLine(){
		return omitFirstLine;
	}
	
	public void setOmitFirstLine(boolean omitFirstLine){
		this.omitFirstLine = omitFirstLine;
	}
	
	
	//------- Methods for setting up the parser ----------------------------------------------
	
	protected void initialize(String fileName) throws IOException, ParameterException {
		Validate.notNull(fileName);
		input = new FileReader(fileName, inputCharset);
		String inputName = input.getFile().getAbsolutePath();
		String outputFileName = inputName.substring(0, inputName.lastIndexOf('.'))+"_output."+inputName.substring(inputName.lastIndexOf('.')+1, inputName.length());
		output = new FileWriter(outputFileName, outputCharset);
	}
		
	
	public void parseFile(String fileName) throws IOException, ParameterException{
		initialize(fileName);
		
		String line = null;
		int lineCount = 0;
		while ((line = input.readLine()) != null) {
			if(lineCount == 0 && omitFirstLine){
				lineCount++;
				continue;
			} else {
				writeOutputLine(line);
				lineCount++;
			}
		}
		input.closeFile();
		output.closeFile();
	}
	
	protected void writeOutputLine(String outputLine) throws IOException{
		output.writeLine(outputLine);
	}

}
