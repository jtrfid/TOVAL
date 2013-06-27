package de.invation.code.toval.file;

import java.io.IOException;
import java.nio.charset.Charset;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class CharacterReplaceParser extends LineBasedFileTransformer{
	
	protected String replaceString = null;
	protected String replacementString = null;
	
	public CharacterReplaceParser(String replaceString, String replacementString) throws ParameterException {
		super();
		Validate.notNull(replaceString);
		Validate.notNull(replacementString);
		this.replaceString = replaceString;
		this.replacementString = replacementString;
	}
	
	public CharacterReplaceParser(Charset charset, String replaceString, String replacementString) throws ParameterException {
		this(charset, charset, replaceString, replacementString);
	}

	public CharacterReplaceParser(Charset inputCharset, Charset outputCharset, String replaceString, String replacementString) throws ParameterException {
		super(inputCharset, outputCharset);
		Validate.notNull(replaceString);
		Validate.notNull(replacementString);
		this.replaceString = replaceString;
		this.replacementString = replacementString;
	}

	@Override
	protected void writeOutputLine(String inputLine) throws IOException {
		if(replaceString != null && replacementString != null){
			output.writeLine(inputLine.replace(replaceString, replacementString));
		} else {
			output.writeLine(inputLine);
		}
	}

	public static void main(String[] args) throws Exception{
		if(args.length < 3 || args.length > 4){
			System.out.println("Usage: FileParser [charset] find replace filename");
			return;
		}
		CharacterReplaceParser parser;
		if(args.length == 3){
			parser = new CharacterReplaceParser(args[0], args[1]);
			parser.parseFile(args[2]);
		} else {
			parser = new CharacterReplaceParser(Charset.forName(args[0]), args[1], args[2]);
			parser.parseFile(args[3]);
		}
	}
	
}
