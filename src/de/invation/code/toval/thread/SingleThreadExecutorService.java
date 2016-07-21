package de.invation.code.toval.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.invation.code.toval.validate.Validate;

/**
 * 抽象类，在独立线程中执行任务，监听执行情况：任务开始，停止，结束（V 结果），异常<br>
 *
 * @param <V> 任务执行结果类型（for Callable），V,Z类型是一致的
 * @param <Z> 任务执行结果类型（for ExecutorListener）
 * @param <E> 异常类型
 */
public abstract class SingleThreadExecutorService<V, Z, E extends Exception> implements CallableListener<V> {

        private ExecutorService executorService = null;
        private Future<V> futureResult = null;
        private final Set<ExecutorListener<Z>> listeners = new HashSet<>();
        private AbstractCallable<V> callable;

        public SingleThreadExecutorService() {
        }

        public SingleThreadExecutorService(ExecutorListener<Z> listener) {
                this();
                addExecutorListener(listener);
        }

        public final void addExecutorListener(ExecutorListener<Z> listener) {
                Validate.notNull(listener);
                listeners.add(listener);
        }

        public void removeExecutorListener(ExecutorListener<Z> listener) {
                listeners.remove(listener);
        }

        /**
         * 开启线程，执行任务
         */
        public void setUpAndRun() {
                executorService = Executors.newSingleThreadExecutor();
                callable = createCallable();
                callable.addCallableListener(this);
                futureResult = executorService.submit(callable); // 执行callable中的call()方法
        }

        /**
         * Returns true if this task completed. 
         * Completion may be due to normal termination, an exception, or cancellation
         * -- in all of these cases, this method will return true.
         * @return true if this task completed
         */
        public boolean isDone() {
                return futureResult.isDone();
        }

        /**
         * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, 
         * and returns a list of the tasks that were awaiting execution. 
         * @throws Exception
         */
        public void stop() throws Exception {
                executorService.shutdownNow();
                for (ExecutorListener<Z> listener : listeners) {
                        listener.executorStopped();
                }
        }

        /**
         * 获取异步任务执行结果，如果任务没有结束，等待计算结束。
         * @return
         * @throws CancellationException
         * @throws InterruptedException
         * @throws ExecutionException
         */
        protected V getCallableResult() throws CancellationException, InterruptedException, ExecutionException {
                if (getCallable().getCallableResult() == null) {
                        futureResult.get();  // Waits if necessary for the computation to complete, and then retrieves its result.
                }
                return getCallable().getCallableResult();
        }

        /**
         * 获取异步任务执行结果，如果任务没有结束，阻塞在此，等待计算结束。
         * 因此，可以不用此函数，在监听中获取结果，避免阻塞。
         * @return
         * @throws E
         */
        public final Z getResult() throws E {
                try {
                        return getResultFromCallableResult(getCallableResult());
                } catch (CancellationException e) {
                        throw createException("Callable cancelled.", e);
                } catch (InterruptedException e) {
                        throw createException("Callable interrupted.", e);
                } catch (ExecutionException e) {
                        throw executionException(e);
                } catch (Exception e) {
                        throw createException("Exception while running callable.\n" + e.getMessage(), e);
                }
        }

        /** 返回异常对象 */
        protected abstract E createException(String message, Throwable cause);
        /** 返回异常对象 */
        protected abstract E executionException(ExecutionException e);
        /** 监听获取执行结果时，调用此函数，可以在此函数中对执行结果（callableResult）进行处理,或直接返回即可。 */
        protected abstract Z getResultFromCallableResult(V callableResult) throws Exception;

        @Override
        public void callableFinished(V result) {
                try {
                        for (ExecutorListener<Z> listener : listeners) {
                                listener.executorFinished(getResultFromCallableResult(result));
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                executorService.shutdownNow();
        }

        @Override
        public void callableStarted() {
                for (ExecutorListener<Z> listener : listeners) {
                        listener.executorStarted();
                }
        }

        @Override
        public void callableStopped() {
                for (ExecutorListener<Z> listener : listeners) {
                        listener.executorStopped();
                }
                executorService.shutdownNow();
        }

        @Override
        public void callableException(Exception e) {
                for (ExecutorListener<Z> listener : listeners) {
                        listener.executorException(e);
                }
                executorService.shutdownNow();
        }

        /**
         * 返回执行任务的AbstractCallable<V>对象，实现该对象的 V callRoutine()方法执行任务，该任务将在单独线程中完成。
         * @return
         */
        protected abstract AbstractCallable<V> createCallable();

        /** 获取执行任务的对象 */
        protected AbstractCallable<V> getCallable() {
                return callable;
        }

}
