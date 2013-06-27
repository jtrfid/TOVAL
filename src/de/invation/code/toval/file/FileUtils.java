package de.invation.code.toval.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import validate.ParameterException;
import validate.ParameterException.ErrorCode;

public class FileUtils {
	
	public static List<File> getFilesInDirectory(String directory) throws IOException, ParameterException{
		return getFilesInDirectory(directory, true, true);
	}
	
	public static List<File> getFilesInDirectory(String directory, boolean onlyFiles, boolean onlyVisibleFiles) throws IOException, ParameterException{
		File dir = new File(directory);
		if(!dir.exists())
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Invalid or non-existing file path.");
		if(!dir.isDirectory())
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "File is not a directory.");
		
		List<File> result = new ArrayList<File>();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(onlyFiles && !files[i].isFile())
				continue;
			if(onlyVisibleFiles && files[i].isHidden())
				continue;
			result.add(files[i]);
		}
		return result;
	}

	public static List<String> getFileNamesInDirectory(String directory) throws IOException, ParameterException{
		return getFileNamesInDirectory(directory, false);
	}
	
	public static List<String> getFileNamesInDirectory(String directory, boolean absolutePath) throws IOException, ParameterException{
		return getFileNamesInDirectory(directory, true, true, absolutePath);
	}
	
	public static List<String> getFileNamesInDirectory(String directory, boolean onlyFiles, boolean onlyVisibleFiles, boolean absolutePath) throws IOException, ParameterException{
		List<File> files = getFilesInDirectory(directory, onlyFiles, onlyVisibleFiles);
		List<String> result = new ArrayList<String>();
		for(File file: files){
			if(absolutePath){
				result.add(file.getAbsolutePath());
			} else {
				result.add(file.getName());
			}
		}
		return result;
	}
	
	public static void deleteFile(String fileName){
	    File file = new File(fileName);

	    if(!file.exists())
	    	throw new IllegalArgumentException("No such file or directory: " + fileName);

	    if(!file.canWrite())
	    	throw new IllegalArgumentException("Write protection: " + fileName);

	    if(file.isDirectory())
	    	throw new IllegalArgumentException("File is a directory: " + fileName);

	    boolean success = file.delete();

	    if(!success)
	    	throw new IllegalArgumentException("Unspecified deletion error: " + fileName);
	}
	
	public static void deleteDirectory(String dirName, boolean recursive){
		File file = new File(dirName);

	    if(!file.exists())
	    	throw new IllegalArgumentException("No such file or directory: " + dirName);

	    if(!file.canWrite())
	    	throw new IllegalArgumentException("Write protection: " + dirName);
	    
	    if(!file.isDirectory())
	    	throw new IllegalArgumentException("No directory: " + dirName);
	    
	    String[] files = file.list();
	    if(files.length > 0){
	    	if(!recursive)
	    		throw new IllegalArgumentException("Cannot delete non-empty directory in non-recursive mode: " + dirName);
	    		
	    	for(int i=0; i<files.length; i++){
	    		File childFile = new File(file.getPath()+"/"+files[i]);
	    		if(childFile.isDirectory()){
	    			deleteDirectory(childFile.getAbsolutePath(), recursive);
	    		} else {
	    			deleteFile(childFile.getAbsolutePath());
	    		}
	    	}
	    }
	    
	    boolean success = file.delete();

	    if(!success)
	    	throw new IllegalArgumentException("Unspecified deletion error: " + dirName);
	}
	
	public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
}
