package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.invation.code.toval.validate.Validate;

public abstract class SingleThreadExecutorService<V> implements CallableListener<V> {
	
	private ExecutorService executorService = null;
	private Future<V> futureResult = null;
	private Set<ExecutorListener<V>> listeners = new HashSet<ExecutorListener<V>>();

	public SingleThreadExecutorService(){}
	
	public SingleThreadExecutorService(ExecutorListener<V> listener){
		this();
		addExecutorListener(listener);
	}
	
	public void addExecutorListener(ExecutorListener<V> listener){
		Validate.notNull(listener);
		listeners.add(listener);
	}
	
	public void removeExecutorListener(ExecutorListener<V> listener){
		listeners.remove(listener);
	}
	
	public void setUpAndRun(){
		executorService = Executors.newSingleThreadExecutor();
		AbstractCallable<V> callable = getCallable();
		callable.addCallableListener(this);
		futureResult = executorService.submit(callable);
	}
	
	public boolean isDone(){
		return futureResult.isDone();
	}
	
	public void stop() throws Exception {
		executorService.shutdownNow();
		for(ExecutorListener<V> listener: listeners)
			listener.executorStopped();
	}
	
	protected V getResult() throws CancellationException, InterruptedException, ExecutionException {
		return futureResult.get();
	}
	

	@Override
	public void executionFinished(V result) {
		for(ExecutorListener<V> listener: listeners)
			listener.executorFinished(result);
	}

	@Override
	public void executionStarted() {
		for(ExecutorListener listener: listeners)
			listener.executorStarted();
	}

	@Override
	public void executionStopped() {
		for(ExecutorListener listener: listeners)
			listener.executorStopped();
	}

	protected abstract AbstractCallable<V> getCallable();

}
