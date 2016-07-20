package de.invation.code.toval.thread;

/**
 * 任务执行情况回调（监听）接口：任务开始，停止，结束（V 结果），异常
 *
 * @param <V> 任务执行结果类型
 */
public interface CallableListener<V> {
	/** 任务开始 */
	public void callableStarted();
	/** 任务停止 */
	public void callableStopped();
	/** 任务结束  */
	public void callableFinished(V result);
	/** 异常 */
	public void callableException(Exception e);

}
