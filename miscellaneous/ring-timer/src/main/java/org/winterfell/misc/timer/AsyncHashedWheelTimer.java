package org.winterfell.misc.timer;

import java.util.concurrent.*;

/**
 * <p>
 * execute {@link TimerTask } async
 * </p>
 *
 * @author Alex
 * @version v1.0, 2020/5/15
 */
public class AsyncHashedWheelTimer extends HashedWheelTimer {

    protected final Executor executor;

    protected final TaskFailedCallback taskFailedCallback;

    /**
     * @param threadFactory
     * @param tickDuration
     * @param unit
     * @param ticksPerWheel
     * @param threadSize
     * @param queueSize
     * @param asyncThreadFactory
     */
    public AsyncHashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit,
                                 int ticksPerWheel, int threadSize, int queueSize,
                                 ThreadFactory asyncThreadFactory,
                                 TaskFailedCallback taskFailedCallback) {
        super(threadFactory, tickDuration, unit, ticksPerWheel);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize,
                300L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), asyncThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        this.executor = threadPoolExecutor;
        this.taskFailedCallback = taskFailedCallback;
    }

    /**
     * @param threadFactory
     * @param tickDuration
     * @param unit
     * @param ticksPerWheel
     * @param asyncExecutor
     */
    public AsyncHashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit,
                                 int ticksPerWheel, Executor asyncExecutor,
                                 TaskFailedCallback taskFailedCallback) {
        super(threadFactory, tickDuration, unit, ticksPerWheel);

        this.executor = asyncExecutor;
        this.taskFailedCallback = taskFailedCallback;
    }

    /**
     *
     */
    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        return super.newTimeout(new AsyncTimerTask(task), delay, unit);
    }

    /**
     * async timer task
     */
    class AsyncTimerTask implements TimerTask, Runnable {
        /**
         *
         */
        TimerTask timerTask;
        /**
         *
         */
        Timeout timeout;

        /**
         * @param timerTask
         */
        public AsyncTimerTask(TimerTask timerTask) {
            super();
            this.timerTask = timerTask;
        }

        /**
         *
         */
        @Override
        public void run(Timeout timeout) {
            this.timeout = timeout;
            try {
                AsyncHashedWheelTimer.this.executor.execute(this);
            } catch (RejectedExecutionException e) {
                taskFailedCallback.executionRejected(e);
            } catch (Throwable e) {
                taskFailedCallback.executionFailed(e);
            }
        }

        /**
         * @see Runnable#run()
         */
        @Override
        public void run() {
            try {
                this.timerTask.run(this.timeout);
            } catch (Throwable e) {
                taskFailedCallback.executionFailed(e);
            }
        }

    }

    public interface TaskFailedCallback {

        void executionRejected(Throwable e);

        void executionFailed(Throwable e);

    }
}
