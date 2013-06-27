package de.invation.code.toval.file;

import java.io.IOException;
import java.nio.charset.Charset;

import validate.ParameterException;

public class HMDFileParser extends LineBasedFileTransformer {
	
	public HMDFileParser(){
		super();
	}
	
	public HMDFileParser(Charset charset) throws ParameterException{
		super(charset);
	}

	@Override
	protected void writeOutputLine(String inputLine) throws IOException {
		super.writeOutputLine(inputLine.substring(1, inputLine.length()));
	}

	public static void main(String[] args) throws Exception{
		if(args.length < 1 || args.length > 2){
			System.out.println("Usage: FileParser [charset] filename");
			return;
		}
		LineBasedFileTransformer parser;
		if(args.length == 2){
			parser = new HMDFileParser(Charset.forName(args[0]));
			parser.parseFile(args[1]);
		} else {
			parser = new HMDFileParser();
			parser.parseFile(args[0]);
		}
	}
	
}
