package de.invation.code.toval.thread;

public interface ExecutorListener {

	public void executorStarted() throws Exception;
	
	public void executorStopped() throws Exception;
	
	public void executorFinished() throws Exception;
	

}
