package de.invation.code.toval.graphic.dialog;

public interface DialogObject<O> {
	
	public O clone();
	
	public void takeoverValues(O other) throws Exception;

}
