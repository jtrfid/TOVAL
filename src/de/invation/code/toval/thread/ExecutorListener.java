package de.invation.code.toval.thread;

/**
 * 任务执行情况回调（监听）接口：任务开始，停止，结束（V 结果），异常，进度情况
 *
 * @param <Z> 任务执行结果类型
 */
public interface ExecutorListener<Z> {
	/** 任务开始 */
	public void executorStarted();
	/** 任务停止 */
	public void executorStopped();
	/** 任务结束 */
	public void executorFinished(Z result);
	/** 异常 */
	public void executorException(Exception exception);
	/** 任务执行进度 */
	public void progress(double progress);
	
}
