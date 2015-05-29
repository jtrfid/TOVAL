package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AbstractCallable<V> implements Callable<V>{

	private Set<CallableListener<V>> listeners = new HashSet<CallableListener<V>>();
	
	public void addCallableListener(CallableListener<V> listener){
		listeners.add(listener);
	}

	public void removeCallableListener(CallableListener<V> listener){
		listeners.remove(listener);
	}

	@Override
	public V call() throws Exception {
		V result = callRoutine();
		for(CallableListener<V> listener: listeners)
			listener.executionFinished(result);
		return result;
	}
	
	protected void notifyExecutionStarted(){
		for(CallableListener<V> listener: listeners)
			listener.executionStarted();
	}
	
	protected void notifyExecutionStopped(){
		for(CallableListener<V> listener: listeners)
			listener.executionStopped();
	}

	protected abstract V callRoutine() throws Exception;

}
