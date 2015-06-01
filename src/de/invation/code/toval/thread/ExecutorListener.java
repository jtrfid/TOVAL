package de.invation.code.toval.thread;

public interface ExecutorListener<Z> {

	public void executorStarted();
	
	public void executorStopped();
	
	public void executorFinished(Z result);
	
	public void executorException(Exception exception);
	
	public void progress(double progress);
	
}
