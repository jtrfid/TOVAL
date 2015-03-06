package de.invation.code.toval.misc.soabase;

public class SOABaseChangeDecision {

	public boolean decision = true;
	public SOABaseListener vetoObject;
	public String changeObject;
	
	public SOABaseChangeDecision() {
		super();
	}
	
	public SOABaseChangeDecision(SOABaseListener vetoObject, String changeObject) {
		super();
		this.decision = false;
		this.vetoObject = vetoObject;
		this.changeObject = changeObject;
	}
	
	

}
