package edu.xidian;

import de.invation.code.toval.thread.AbstractCallable;
import de.invation.code.toval.thread.CallableListener;


/**
 * 同一线程内，执行一个长时间的任务，AbstractCallable抽象类的使用：
   (1) 构造类对象
   AbstractCallable<String> callable = new AbstractCallable<String>(){
		@Override
		protected String callRoutine() throws Exception {
		   // 执行任务
		}
   }
   (2) 可选，监听执行情况
   callable.addCallableListener(new CallableListener<String>(){
			@Override
			public void callableStarted() {
				System.out.println("callableStarted...");
			}
			@Override
			public void callableStopped() {
				System.out.println("callableStopped");
			}
			@Override
			public void callableFinished(String result) {
				System.out.println("callableFinished, result = " + result);
			}
			@Override
			public void callableException(Exception e) {
				System.out.println("callableException: " + e.toString());
			}
		});
		
	(3) 执行任务：
	callable.call();
	
	(4) 线程阻塞至此，等待以上语句执行完毕，获取执行结果。
	callable.getCallableResult();
	
 * 
 * @author Administrator
 *
 */
public class CallableTest {

	////////////////////////// 产生抽象类AbstractCallable对象的两种等效方式
	// 1. 定义一个继承自AbstractCallable的新类，在该类中实现抽象方法。
	private AbstractCallable<String> callable1 = new TaskCallable();
	// 2. 直接生成AbstractCallable对象，实现抽象方法。
	private AbstractCallable<String> callable = new AbstractCallable<String>(){
        // 执行任务
		@Override
		protected String callRoutine() throws Exception {
			for(int i = 0; i < 100; i++) {
				if(i == 10) throw new Exception("test exception"); // 测试抛出异常
				System.out.println("i=" + i);
				//Thread.sleep(1000); // 延时1s
			}
			return "ok";
		}
		
	};
	private class TaskCallable extends AbstractCallable<String> {
        // 执行任务
		@Override
		protected String callRoutine() throws Exception {
			for(int i = 0; i < 100; i++) {
				if(i == 10) throw new Exception("test exception");
				System.out.println("i=" + i);
				//Thread.sleep(1000); // 延时1s
			}
			return "ok";
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		CallableTest test = new CallableTest();
		
		// 监听执行情况
        test.callable.addCallableListener(new CallableListener<String>(){

			@Override
			public void callableStarted() {
				System.out.println("callableStarted...");
			}

			@Override
			public void callableStopped() {
				System.out.println("callableStopped");
			}

			@Override
			public void callableFinished(String result) {
				System.out.println("callableFinished, result = " + result);
			}

			@Override
			public void callableException(Exception e) {
				System.out.println("callableException: " + e.toString());
			}
			
		});
		
        System.out.println("result = " + test.callable.getCallableResult()); // null
        
		try {
			test.callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 线程阻塞至此，等待以上语句执行完毕，获取执行结果。
		System.out.println("ssssssssss");
		
		System.out.println("result = " + test.callable.getCallableResult());
	}
	
	
}
