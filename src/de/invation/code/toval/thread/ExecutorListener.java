package de.invation.code.toval.thread;

public interface ExecutorListener {

	public void executorStarted();
	
	public void executorStopped();
	
	public void executorFinished();
	
	public void progress(double progress);
	
}
