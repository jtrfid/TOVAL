package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
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
		addListener(listener);
	}
	
	public void addListener(ExecutorListener listener){
		Validate.notNull(listener);
		listeners.add(listener);
	}
	
	public void setUpAndRun() throws Exception{
		executorService = Executors.newSingleThreadExecutor();
		AbstractCallable<V> callable = getCallable();
		callable.addCallableListener(this);
		futureResult = executorService.submit(callable);
		for(ExecutorListener listener: listeners)
			listener.executorStarted();
	}
	
	public boolean isDone(){
		return futureResult.isDone();
	}
	
	public void stop() throws Exception {
		executorService.shutdownNow();
		for(ExecutorListener listener: listeners)
			listener.executorStopped();
	}
	
	public V getResult() throws InterruptedException, ExecutionException {
		return futureResult.get();
	}
	

	@Override
	public void executionFinished() throws Exception {
		for(ExecutorListener listener: listeners)
			listener.executorFinished();
	}

	protected abstract AbstractCallable<V> getCallable();

}
