package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.invation.code.toval.validate.Validate;

public abstract class SingleThreadExecutorService<V> implements CallableListener {
	
	private ExecutorService executorService = null;
	private Future<V> futureResult = null;
	private Set<ExecutorListener> listeners = new HashSet<ExecutorListener>();

	public SingleThreadExecutorService(){}
	
	public SingleThreadExecutorService(ExecutorListener listener){
		this();
		addExecutorListener(listener);
	}
	
	public void addExecutorListener(ExecutorListener listener){
		Validate.notNull(listener);
		listeners.add(listener);
	}
	
	public void removeExecutorListener(ExecutorListener listener){
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
		for(ExecutorListener listener: listeners)
			listener.executorStopped();
	}
	
	protected V getResult() throws CancellationException, InterruptedException, ExecutionException {
		return futureResult.get();
	}
	

	@Override
	public void executionFinished() {
		for(ExecutorListener listener: listeners)
			listener.executorFinished();
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
