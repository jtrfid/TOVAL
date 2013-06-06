package misc;

import java.util.StringTokenizer;

import validate.ParameterException;
import validate.Validate;

public class StringUtils {
	
	public static StringTokenizer splitArrayString(String array, String delimiter){
		array = array.replace("[", "");
		array = array.replace("]", "");
		return new StringTokenizer(array, delimiter);
	}
	
	public static int countOccurrences(String string, char character){
		int count = 0;
		for(char c: string.toCharArray()){
			if(c == character)
				count++;
		}
		return count;
	}
	
	public static String removeLeading(String string, char character){
		int index = 0;
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i) != character){
				return string.substring(index);
			}
			index++;
		}
		if(index > 0)
			return "";
		return string;
	}
	
	public static String removeEnding(String string, char character){
		int index = string.length()-1;
		for(int i=string.length()-1; i>=0; i--){
			if(string.charAt(i) != character){
				return string.substring(0, index+1);
			}
			index--;
		}
		if(index < string.length()-1)
			return "";
		return string;
	}	
	
	public static String removeSurrounding(String string, char character){
		String result = removeLeading(string, character);
		return removeEnding(result, character);
	}
	
	public static String convertToHTML(String string) throws ParameterException{
		Validate.notNull(string);
		String htmlString = "<html>"+string.replace("\n", "<br>")+"</html>";
		htmlString.replace(" ", "&nbsp;");
		return htmlString;
	}

}
