package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 抽象类，执行任务，监听执行情况：任务开始，停止，结束（V 结果），异常<br>
 * 子类实现方法V callRoutine()，执行任务。<br>
 * void addCallableListener(CallableListener<V> listener)，添加任务执行情况监听<br>
 * V getCallableResult()，获取执行结果<br>
 * 
 * 类SingleThreadExecutorService<V,Z,E>中，以此类对象为成员，封装，在独立线程中执行任务。
 * 
 * Type Parameters:
 * @param <V> 任务执行结果类型
 */
public abstract class AbstractCallable<V> implements Callable<V> {

	    /**
	     * 监听接口：任务开始，停止，结束（V 结果），异常
	     */
        private final Set<CallableListener<V>> listeners = new HashSet<>();
        /**
         * 任务执行结果
         */
        private V callableResult;
        
        /**
         * 添加任务执行情况回调（监听）
         * @param listener 监听接口：任务开始，停止，结束（V 结果），异常
         */
        public void addCallableListener(CallableListener<V> listener) {
                listeners.add(listener);
        }

        /**
         * 去除任务执行情况回调（监听）
         * @param listener 监听接口：任务开始，停止，结束（V 结果），异常
         */
        public void removeCallableListener(CallableListener<V> listener) {
                listeners.remove(listener);
        }

        /**
         * 执行任务，执行情况回调，获得执行结果，或抛出异常<br>
         * 实现接口Callable<V>中的方法
         */
        @Override
        public V call() throws Exception {
                notifyExecutionStarted();
                try {
                        callableResult = callRoutine();
                } catch (Exception e) {
                        notifyExecutionStopped();
                        notifyException(e);
                        throw e;
                }
                notifyExecutionFinished(callableResult);
                return callableResult;
        }

        /**
         * 通知任务执行开始
         */
        protected void notifyExecutionStarted() {
                for (CallableListener<V> listener : listeners) {
                        listener.callableStarted();
                }
        }

        /**
         * 通知停止任务执行
         */
        protected void notifyExecutionStopped() {
                for (CallableListener<V> listener : listeners) {
                        listener.callableStopped();
                }
        }

        /**
         * 通知任务执行结束
         * @param result 任务执行结果
         */
        protected void notifyExecutionFinished(V result) {
                for (CallableListener<V> listener : listeners) {
                        listener.callableFinished(result);
                }
        }
        
        /**
         * 通知任务执行异常
         * @param exception
         */
        protected void notifyException(Exception exception) {
                for (CallableListener<V> listener : listeners) {
                        listener.callableException(exception);
                }
        }

        /**
         * 获取任务执行结果
         * @return 如果任务没有结束，返回null。
         *         使用ExecutorService机制，Future等待结束，获取执行结果，futureResult = executorService.submit(callable);
         */
        public V getCallableResult() {
                return callableResult;
        }

        /**
         * 子类实现此方法，执行任务
         * @return
         * @throws Exception
         */
        protected abstract V callRoutine() throws Exception;

}
