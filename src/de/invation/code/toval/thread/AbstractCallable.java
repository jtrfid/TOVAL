package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AbstractCallable<V> implements Callable<V>{

	private Set<CallableListener> listeners = new HashSet<CallableListener>();
	
	public void addCallableListener(CallableListener listener){
		listeners.add(listener);
	}

	public void removeCallableListener(CallableListener listener){
		listeners.remove(listener);
	}

	@Override
	public V call() throws Exception {
		V result = callRoutine();
		for(CallableListener listener: listeners)
			listener.executionFinished();
		return result;
	}
	
	protected void notifyExecutionStarted(){
		for(CallableListener listener: listeners)
			listener.executionStarted();
	}
	
	protected void notifyExecutionStopped(){
		for(CallableListener listener: listeners)
			listener.executionStopped();
	}

	protected abstract V callRoutine() throws Exception;

}
