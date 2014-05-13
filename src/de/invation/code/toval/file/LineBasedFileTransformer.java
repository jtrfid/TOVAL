package de.invation.code.toval.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class LineBasedFileTransformer {
	
	protected Charset inputCharset = Charset.forName("UTF-8");
	protected Charset outputCharset = Charset.forName("UTF-8");

	protected boolean omitFirstLine = false;

	protected FileReader input = null;
	protected FileWriter output = null;
	
	private String fileExtension = null;
	
	private int inputLines = 0;
	private int outputLines = 0;
	
	
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
	
	public String getFileExtension(){
		return fileExtension;
	}
	
	public void setFileExtension(String extension){
		this.fileExtension = extension;
	}
	
	protected String getHeaderLine() {
		return null;
	}
	
	public int getInputLines() {
		return inputLines;
	}

	public int getOutputLines() {
		return outputLines;
	}
	
	//------- Methods for setting up the parser ----------------------------------------------

	protected synchronized void initialize(String fileName) throws IOException, ParameterException {
		Validate.notNull(fileName, "File Name must not be null");
		input = new FileReader(fileName, inputCharset);
		String inputName = input.getFile().getAbsolutePath();
		String outputFileName = inputName.substring(0, inputName.indexOf('.'))+"_output";
		output = new FileWriter(outputFileName, outputCharset);
		if(fileExtension != null){
			output.setFileExtension(getFileExtension());
		}
	}
		
	
	public void parseFile(String fileName) throws IOException, ParameterException{
		initialize(fileName);
		
		String headerLine = getHeaderLine();
		if(headerLine != null)
			output.writeLine(headerLine);
		
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
			if(!continueParsing(lineCount))
				break;
		}
		inputLines = lineCount;
		input.closeFile();
		output.closeFile();
	}

	protected boolean continueParsing(int lineCount){
		return true;
	}
	
	private synchronized void writeOutputLine(String outputLine) throws IOException{
		Set<String> lines = transformLine(outputLine);
		if(lines == null || lines.isEmpty())
			return;
		for(String line: lines){
			output.writeLine(line);
			outputLines++;
		}
		
	}
	
	protected synchronized Set<String> transformLine(String line){
		return new HashSet<String>(Arrays.asList(line));
	}

}
