package edu.xidian;

import java.util.concurrent.ExecutionException;

import de.invation.code.toval.thread.AbstractCallable;
import de.invation.code.toval.thread.CallableListener;
import de.invation.code.toval.thread.ExecutorListener;
import de.invation.code.toval.thread.SingleThreadExecutorService;


/**
 * 在独立线程内，执行一个长时间的任务，SingleThreadExecutorService的使用：AbstractCallable抽象类的使用：
   (1) 构造AbstractCallable抽象类对象，任务封装在callRoutinue()方法中。
   AbstractCallable<String> callable = new AbstractCallable<String>(){
		@Override
		protected String callRoutine() throws Exception {
		   // 执行任务
		}
   }
   (2) 构造SingleThreadExecutorService对象，前两个参数类型是任务执行结果类型，后一个是异常类型
   SingleThreadExecutorService<String, String, Exception> service = new SingleThreadExecutorService<String,String,Exception>(){
		@Override
		protected Exception createException(String message, Throwable cause) {
			return new Exception(message,cause);
		}

		@Override
		protected Exception executionException(ExecutionException e) {
			return new Exception(e);
		}

		@Override
		protected String getResultFromCallableResult(String callableResult) throws Exception {
			// 这里的callableResult与executorFinished(result)相同
			System.out.println("getResultFromCallableResult: " + callableResult);
			return callableResult;
		}
		@Override
		protected AbstractCallable<String> createCallable() {
			return callable;  // AbstractCallable抽象类对象,任务封装在callRoutinue()方法中。
		}		
	};
	
   (3) 监听执行情况，必须，否则无法获取执行结果
   service.addExecutorListener(new ExecutorListener<String>(){

			@Override
			public void executorStarted() {
				System.out.println("executorStarted()");
			}

			@Override
			public void executorStopped() {
				System.out.println("executorStopped()");
			}

			@Override
			public void executorFinished(String result) {
				// 这里的result与getResultFromCallableResult(callableResult)相同
				System.out.println("executorFinished(),result = " + result);
			}

			@Override
			public void executorException(Exception exception) {
				System.out.println("executorException()");	
			}

			@Override
			public void progress(double progress) {
				System.out.println("progress()");
			}
			
		});
		     
	(4) 独立线程执行任务
	   service.setUpAndRun(); // 此后的语句继续执行，结果在监听中获取
		
	(5) 如果需要,阻塞等待执行结果。
	   service.getResult());
 * 
 * @author Administrator
 *
 */
public class SingleThreadExecutorServiceTest {

	AbstractCallable<String> callable = new AbstractCallable<String>(){

		@Override
		protected String callRoutine() throws Exception {
			for(int i = 0; i < 100; i++) {
				//if(i == 10) throw new Exception("test exception"); // 测试抛出异常
				System.out.println("i=" + i);
				//Thread.sleep(1000); // 延时1s
			}
			return "ok";
		}
		
	};
	
	SingleThreadExecutorService<String, String, Exception> service = new SingleThreadExecutorService<String,String,Exception>(){

		@Override
		protected Exception createException(String message, Throwable cause) {
			return new Exception(message,cause);
		}

		@Override
		protected Exception executionException(ExecutionException e) {
			return new Exception(e);
		}

		@Override
		protected String getResultFromCallableResult(String callableResult) throws Exception {
			// 这里的callableResult与executorFinished(result)相同
			System.out.println("getResultFromCallableResult: " + callableResult);
			return callableResult;
		}

		// 任务在返回对象中封装
		@Override
		protected AbstractCallable<String> createCallable() {
			return callable; // AbstractCallable抽象类对象,任务封装在callRoutinue()方法中。
		}
		
	};

	public static void main(String[] args) {
		System.out.println("start");
		SingleThreadExecutorServiceTest test = new SingleThreadExecutorServiceTest();
		
		// 监听执行情况
		test.service.addExecutorListener(new ExecutorListener<String>(){

			@Override
			public void executorStarted() {
				System.out.println("executorStarted()");
			}

			@Override
			public void executorStopped() {
				System.out.println("executorStopped()");
			}

			@Override
			public void executorFinished(String result) {
				// 这里的result与getResultFromCallableResult(callableResult)相同
				System.out.println("executorFinished(),result = " + result);
			}

			@Override
			public void executorException(Exception exception) {
				System.out.println("executorException()");	
			}

			@Override
			public void progress(double progress) {
				System.out.println("progress()");
			}
			
		});
		    
		// 独立线程执行任务
		test.service.setUpAndRun(); // 此后的语句继续执行，结果在监听中获取
		
		// 不会阻塞
		System.out.println("不会阻塞");	
		
		System.out.println("isDone = " + test.service.isDone()); // false
		
		try {
			//test.service.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("isDone = " + test.service.isDone()); // 经过stop后的延时，true
		
		// 阻塞等待执行结果
		try {
			System.out.println("阻塞等待执行结果：" + test.service.getResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
