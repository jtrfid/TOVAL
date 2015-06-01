package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.invation.code.toval.validate.Validate;

public abstract class SingleThreadExecutorService<V,Z,E extends Exception> implements CallableListener<V> {
	
	private ExecutorService executorService = null;
	private Future<V> futureResult = null;
	private Set<ExecutorListener<Z>> listeners = new HashSet<ExecutorListener<Z>>();
	private AbstractCallable<V> callable;

	public SingleThreadExecutorService(){}
	
	public SingleThreadExecutorService(ExecutorListener<Z> listener){
		this();
		addExecutorListener(listener);
	}
	
	public void addExecutorListener(ExecutorListener<Z> listener){
		Validate.notNull(listener);
		listeners.add(listener);
	}
	
	public void removeExecutorListener(ExecutorListener<Z> listener){
		listeners.remove(listener);
	}
	
	public void setUpAndRun(){
		executorService = Executors.newSingleThreadExecutor();
		AbstractCallable<V> callable = createCallable();
		callable.addCallableListener(this);
		futureResult = executorService.submit(callable);
	}
	
	public boolean isDone(){
		return futureResult.isDone();
	}
	
	public void stop() throws Exception {
		executorService.shutdownNow();
		for(ExecutorListener<Z> listener: listeners)
			listener.executorStopped();
	}
	
	protected V getCallableResult() throws CancellationException, InterruptedException, ExecutionException {
		if(getCallable().getCallableResult() == null){
			futureResult.get();
		}
		return getCallable().getCallableResult();
	}
	
	public final Z getResult() throws E {
		try {
			return getResultFromCallableResult(getCallableResult());
		} catch (CancellationException e) {
			throw createException("Callable cancelled.", e);
		} catch (InterruptedException e) {
			throw createException("Callable interrupted.", e);
		} catch (ExecutionException e) {
			throw executionException(e);
		} catch(Exception e){
			throw createException("Exception while running callable.\n" + e.getMessage(), e);
		}
	}
	
	protected abstract E createException(String message, Throwable cause);
	
	protected abstract E executionException(ExecutionException e);
	
	protected abstract Z getResultFromCallableResult(V callableResult) throws Exception;
	
	@Override
	public void callableFinished(V result) {
		try {
			for (ExecutorListener<Z> listener : listeners)
				listener.executorFinished(getResultFromCallableResult(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callableStarted() {
		for(ExecutorListener<Z> listener: listeners)
			listener.executorStarted();
	}

	@Override
	public void callableStopped() {
		for(ExecutorListener<Z> listener: listeners)
			listener.executorStopped();
	}

	@Override
	public void callableException(Exception e) {
		for(ExecutorListener<Z> listener: listeners)
			listener.executorException(e);
	}

	protected abstract AbstractCallable<V> createCallable();
	
	protected AbstractCallable<V> getCallable(){
		return callable;
	}

}
