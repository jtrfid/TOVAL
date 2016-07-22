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
public class ThreadSampleTest<V> {
	private ExecutorService executorService = null;
	private Future<V> futureResult = null;
	private Callable<V> callable;

	public ThreadSampleTest(Callable<V> callable) {
		this.callable = callable;
	}

	/**
	 * 开启线程，执行任务
	 */
	public void setUpAndRun() {
		executorService = Executors.newSingleThreadExecutor();
		futureResult = executorService.submit(callable); // 执行callable中的call()方法
	}


	/**
	 * 获取异步任务执行结果，如果任务没有结束，等待计算结束。
	 * 
	 * @return
	 * @throws CancellationException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected V getCallableResult() throws CancellationException, InterruptedException, ExecutionException {
		return futureResult.get(); // Waits if necessary for the computation to complete, and then retrieves its result.
	}

	public static void main(String[] args) {
		class Mycallable<V> implements Callable<V> {
			private V a;
			
			public void setA(V a) { 
				this.a = a; 
			}
			
			@Override
			public V call() throws Exception {
				for (int i = 0; i < 10; i++) {
					System.out.println("call: " + i);
					Thread.sleep(1000);
				}
				return a;
			}

			
		}
		
		Mycallable<Integer> callable = new Mycallable<Integer>();

		ThreadSampleTest<Integer> service = new ThreadSampleTest<Integer>(callable);
		
		callable.setA(1);
		service.setUpAndRun();

		System.out.println("阻塞等待结果1...");
		try {
			System.out.println("计算结果1：" + service.getCallableResult());  // 1
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
		//callable.setA(2);
		service.setUpAndRun();

		//////////////////正确得到第二次结果。
		System.out.println("阻塞等待结果2...");
		try {
			System.out.println("计算结果2：" + service.getCallableResult());  // 2
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
