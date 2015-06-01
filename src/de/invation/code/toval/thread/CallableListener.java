package de.invation.code.toval.thread;

public interface CallableListener<V> {
	
	public void callableStarted();
	
	public void callableStopped();
	
	public void callableFinished(V result);
	
	public void callableException(Exception e);

}
