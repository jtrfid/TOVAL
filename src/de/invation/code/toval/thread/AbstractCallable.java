package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AbstractCallable<V> implements Callable<V>{

	private Set<CallableListener<V>> listeners = new HashSet<CallableListener<V>>();
	private V callableResult;
	
	public void addCallableListener(CallableListener<V> listener){
		listeners.add(listener);
	}

	public void removeCallableListener(CallableListener<V> listener){
		listeners.remove(listener);
	}

	@Override
	public V call() throws Exception {
		notifyExecutionStarted();
		try{
			callableResult = callRoutine();
		}catch(Exception e){
			notifyExecutionStopped();
			throw e;
		}
		notifyExecutionFinished(callableResult);
		return callableResult;
	}
	
	protected void notifyExecutionStarted(){
		for(CallableListener<V> listener: listeners)
			listener.callableStarted();
	}
	
	protected void notifyExecutionStopped(){
		for(CallableListener<V> listener: listeners)
			listener.callableStopped();
	}
	
	protected void notifyExecutionFinished(V result){
		for(CallableListener<V> listener: listeners)
			listener.callableStopped();
	}
	
	protected void notifyException(Exception exception){
		for(CallableListener<V> listener: listeners)
			listener.callableException(exception);
	}
	
	public V getCallableResult(){
		return callableResult;
	}

	protected abstract V callRoutine() throws Exception;

}
