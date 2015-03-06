package de.invation.code.toval.misc.soabase;

public class SOABaseChangeReply {

	public SOABaseListener source;
	public boolean decision;
	public String changeObject;
	
	public SOABaseChangeReply(SOABaseListener source, boolean decision, String changeObject) {
		super();
		this.source = source;
		this.decision = decision;
		this.changeObject = changeObject;
	}

}
