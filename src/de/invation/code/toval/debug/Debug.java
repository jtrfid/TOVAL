package de.invation.code.toval.debug;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.invation.code.toval.file.FileWriter;
import de.invation.code.toval.validate.ParameterException;

/**
 * Debugging class.
 * It provides methods for debugging messages.
 * Printing of debug-messages depends on the actual {@link DebugMode}.
 * @author stocker
 *
 */
public class Debug {
	/**
	 * Date format for debug-outputs
	 */
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	/**
	 * Debug mode
	 */
	private static DebugMode debugMode = DebugMode.SILENT;
	
	private static PrintStream printStream = System.out;
	
	private static final String messageOffset = "                    ";
	
	private static FileWriter fileWriter = null;
	
	private static OutputMode outputMode = OutputMode.SYSOUT;
	
	private static boolean includeHeader = true;
	
	private static Map<Integer, StringBuilder> storedDebugInfo = new HashMap<Integer, StringBuilder>();
	
	public static synchronized void setOutputMode(OutputMode mode){
		outputMode = mode;
		if(outputMode == OutputMode.FILE && fileWriter == null){
			try {
				fileWriter = new FileWriter("debug");
			} catch (ParameterException e) {
				printStream.println("Cannot prepare file writer for output-mode FILE: Parameter Exception");
			}
		}
	}
	
	public static int newStoredDebuggingInfo(){
		int i=0;
		while(storedDebugInfo.containsKey(i)){
			i++;
		}
		storedDebugInfo.put(i, new StringBuilder());
		return i;
	}
	
	public static void addStoredDebuggingInfo(int key){
		if(debugMode == DebugMode.SILENT)
			return;
		println(storedDebugInfo.get(key).toString());
		storedDebugInfo.remove(key);
	}
	
	public static void setIncludeHeader(boolean includeHeader) {
		Debug.includeHeader = includeHeader;
	}

	/**
	 * Sets the debug mode
	 * @param mode New debug mode
	 * @see DebugMode
	 */
	public static void setDebugMode(DebugMode mode){
		debugMode = mode;
	} 
	
	public static DebugMode getDebugMode(){
		return debugMode;
	}
	
	public static boolean isActive(){
		return debugMode != DebugMode.SILENT;
	}
	
	public static void deactivate(){
		setDebugMode(DebugMode.SILENT);
	}
	
	public static void closeFile(){
		try {
			fileWriter.closeFile();
		} catch (IOException e) {
			printStream.println("Cannot close file: I/O Exception");
		}
	}
	
	/**
	 * Prints a debug message.
	 * This method is used for all low-priority messages.
	 * They are only printed, when {@link Debug#debugMode} is {@link DebugMode#EXTENDED}.
	 * @param message Debug message
	 */
	public static void message(String message){
		message(message, DebugMode.EXTENDED);
	}
	
	public static void message(Integer key, String message){
		message(key, message, DebugMode.EXTENDED);
	}
	
	public static void message(String message, DebugMode mode){
		message(message, mode, true);
	}
	
	public static void message(Integer key, String message, DebugMode mode){
		message(key, message, mode, true);
	}
	
	public static void message(String message, DebugMode mode, boolean withHeader){
		message(null, message, mode, withHeader);
	}
	
	public static void message(Integer key, String message, DebugMode mode, boolean withHeader){
		if(debugMode == DebugMode.SILENT)
			return;
		if(mode.ordinal() >= debugMode.ordinal()){
			if(withHeader && includeHeader){
				if(key != null){
					storedDebugInfo.get(key).append(getTime()+" Message:   "+prepareMessage(message));
					storedDebugInfo.get(key).append('\n');
				} else {
					println(getTime()+" Message:   "+prepareMessage(message));
				}
			} else {
				if(key != null){
					storedDebugInfo.get(key).append(prepareMessage(message));
					storedDebugInfo.get(key).append('\n');
				} else {
					println(prepareMessage(message));
				}
			}
		}
	}
	
	public static void messageN(String message){
		messageN(message, DebugMode.EXTENDED);
	}
	
	public static void messageN(String message, DebugMode mode){
		if(debugMode == DebugMode.SILENT)
			return;
		if(mode.ordinal() >= debugMode.ordinal()){
			print(getTime()+" Message:   "+prepareMessage(message));
		}
	}
	
	private static String prepareText(String text, String offset){
		String output = new String();
		for(int i=0; i<text.length(); i++){
			output += text.charAt(i);
			if(text.charAt(i) == '\n'){
				output += offset;
			}
		}
		return output;
	}
	
	private static String prepareMessage(String message){
		return prepareText(message, messageOffset);
	}
	
	/**
	 * Prints an error message.
	 * This method is used for encountered errors (catched exceptions)
	 * Error messages are printed, regardless of {@link Debug#debugMode}.
	 * @param message Debug message
	 */
	public static void error(String message){
		if(debugMode == DebugMode.SILENT)
			return;
		println(getTime()+" Error:   "+message);
	}
	
	public static void newLine(){
		newLine(DebugMode.EXTENDED);
	}
	
	/**
	 * Prints a new line
	 */
	public static void newLine(DebugMode mode){
		if(debugMode == DebugMode.SILENT)
			return;
		if(mode.ordinal() >= debugMode.ordinal()){
			if(outputMode == OutputMode.SYSOUT){
				printStream.println();
			} else if(outputMode == OutputMode.FILE){
				try {
					fileWriter.newLine();
				} catch (IOException e) {
					printStream.println("Cannot write output to file: I/O Exception");
				}
			}
		}
	}
	
	private static void print(String string){
		if(outputMode == OutputMode.SYSOUT){
			printStream.print(string);
		} else if(outputMode == OutputMode.FILE){
			try {
				fileWriter.write(string);
			} catch (IOException e) {
				printStream.println("Cannot write output to file: I/O Exception");
			}
		}
	}
	
	private static void println(String string){
		if(outputMode == OutputMode.SYSOUT){
			printStream.println(string);
		} else if(outputMode == OutputMode.FILE){
			try {
				fileWriter.writeLine(string);
			} catch (IOException e) {
				printStream.println("Cannot write output to file: I/O Exception");
			}
		}
	}
	
	public static void newMethod(){
//		newLine();
	}
	
	/**
	 * Returns the actual time in the format specified by {@link Debug#sdf}.
	 * @return The actual date
	 */
	private static synchronized String getTime(){
		return sdf.format(new Date());
	}
	
}
