package de.invation.code.toval.thread;

public interface CallableListener<V> {
	
	public void executionStarted();
	
	public void executionStopped();
	
	public void executionFinished(V result);

}
