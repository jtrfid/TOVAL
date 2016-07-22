package edu.xidian;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.invation.code.toval.thread.AbstractCallable;
import de.invation.code.toval.thread.CallableListener;
import de.invation.code.toval.thread.ExecutorListener;
import de.invation.code.toval.validate.Validate;

/**
 * 独立线程执行任务，基本ExecutorService的使用方法。
 * 
 * @author Administrator
 *
 */
public class ThreadTest<V> implements CallableListener<V> {
	private ExecutorService executorService = null;
	private Future<V> futureResult = null;
	private final Set<ExecutorListener<V>> listeners = new HashSet<>();
	private AbstractCallable<V> callable;

	public ThreadTest(AbstractCallable<V> callable) {
		this.callable = callable;
	}

	public ThreadTest(AbstractCallable<V> callable, ExecutorListener<V> listener) {
		this(callable);
		addExecutorListener(listener);
	}

	public final void addExecutorListener(ExecutorListener<V> listener) {
		Validate.notNull(listener);
		listeners.add(listener);
	}

	public void removeExecutorListener(ExecutorListener<V> listener) {
		listeners.remove(listener);
	}

	/**
	 * 开启线程，执行任务
	 */
	public void setUpAndRun() {
		executorService = Executors.newSingleThreadExecutor();
		callable.addCallableListener(this);
		futureResult = executorService.submit(callable); // 执行callable中的call()方法
	}

	/**
	 * Returns true if this task completed. Completion may be due to normal
	 * termination, an exception, or cancellation -- in all of these cases, this
	 * method will return true.
	 * 
	 * @return true if this task completed
	 */
	public boolean isDone() {
		return futureResult.isDone();
	}

	/**
	 * Attempts to stop all actively executing tasks, halts the processing of
	 * waiting tasks, and returns a list of the tasks that were awaiting
	 * execution.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		executorService.shutdownNow();
		for (ExecutorListener<V> listener : listeners) {
			listener.executorStopped();
		}
	}
	
	 /**
     * 获取异步任务执行结果，如果任务没有结束，等待计算结束。
     * 注意，如果是第二次运行setUpAndRun(),此函数将返回上一次的结果
     * 事实上，本函数仅含一条语句即可：return futureResult.get();
     * @return
     * @throws CancellationException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected V getCallableResult() throws CancellationException, InterruptedException, ExecutionException {
    	/**
            if (callable.getCallableResult() == null) {  // 注意，如果是第二次运行setUpAndRun(),此时将是上一次的结果
                    futureResult.get();  // Waits if necessary for the computation to complete, and then retrieves its result.
            }
            return callable.getCallableResult();
         **/   
            return futureResult.get();
    }

	@Override
	public void callableFinished(V result) {
		System.out.println("callableFinished()");
		try {
			for (ExecutorListener<V> listener : listeners) {
				listener.executorFinished(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		executorService.shutdownNow();
	}

	@Override
	public void callableStarted() {
		System.out.println("callableStarted()");
		for (ExecutorListener<V> listener : listeners) {
			listener.executorStarted();
		}
	}

	@Override
	public void callableStopped() {
		System.out.println("callableStopped()");
		for (ExecutorListener<V> listener : listeners) {
			listener.executorStopped();
		}
		executorService.shutdownNow();
	}

	@Override
	public void callableException(Exception e) {
		System.out.println("callableException()");
		for (ExecutorListener<V> listener : listeners) {
			listener.executorException(e);
		}
		executorService.shutdownNow();
	}

	public static void main(String[] args) {
		AbstractCallable<Integer> callable = new AbstractCallable<Integer>() {
			@Override
			protected Integer callRoutine() throws Exception {
				int i;
				for (i = 0; i < 10; i++) {
					System.out.println("callRoutine: " + i);
					Thread.sleep(1000);
				}
				return i;
			}

		};

		ThreadTest<Integer> service = new ThreadTest<Integer>(callable);
		service.setUpAndRun();

		System.out.println("阻塞等待结果...");
		try {
			System.out.println("计算结果：" + service.getCallableResult());
		} catch (CancellationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("再运行一次");
		service.setUpAndRun();

		//////////////////依然是上一次的结果，并且不等待。
		System.out.println("阻塞等待结果1...");
		try {
			System.out.println("计算结果1：" + service.getCallableResult());
		} catch (CancellationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
