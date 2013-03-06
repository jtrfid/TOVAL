package debug;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	private static DebugMode debugMode = DebugMode.EXTENDED;
	
	private static PrintStream printStream = System.out;
	
	private static final String messageOffset = "                          ";
	/**
	 * Sets the debug mode
	 * @param mode New debug mode
	 * @see DebugMode
	 */
	public static void setDebugMode(DebugMode mode){
		debugMode = mode;
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
	
	public static void message(String message, DebugMode mode){
		if(debugMode == DebugMode.SILENT)
			return;
		if(mode.ordinal() >= debugMode.ordinal()){
			printStream.println("DEBUG "+getTime()+" Message:   "+prepareMessage(message));
		}
	}
	
	public static void messageN(String message){
		messageN(message, DebugMode.EXTENDED);
	}
	
	public static void messageN(String message, DebugMode mode){
		if(debugMode == DebugMode.SILENT)
			return;
		if(mode.ordinal() >= debugMode.ordinal()){
			printStream.print("DEBUG "+getTime()+" Message:   "+prepareMessage(message));
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
		System.out.println("DEBUG "+getTime()+" Error:   "+message);
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
			System.out.println();
		}
	}
	
	public static void newMethod(){
//		newLine();
	}
	
	/**
	 * Returns the actual time in the format specified by {@link Debug#sdf}.
	 * @return The actual date
	 */
	private static String getTime(){
		return sdf.format(new Date());
	}
	
}
