package de.invation.code.toval.file;

public enum EOLType {
	
	LF, CR, CRLF;
	
	public String toString(){
		switch(this){
			case LF: return "\n";
			case CR: return "\r";
			case CRLF: return "\r\n";
		}
		return "\n";
	}

}
