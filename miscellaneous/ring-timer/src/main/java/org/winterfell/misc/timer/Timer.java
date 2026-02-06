package org.winterfell.misc.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Schedules {@link TimerTask}s for one-time future execution in a background
 * thread.
 *
 * @author Alex
 * @since v1.0, 2021/1/26
 */
public interface Timer {

    /**
     * Schedules the specified {@link TimerTask} for one-time execution after
     * the specified delay.
     *
     * @return a handle which is associated with the specified task
     * @throws IllegalStateException      if this timer has been {@linkplain #stop() stopped} already
     * @throws java.util.concurrent.RejectedExecutionException if the pending timeouts are too many and creating new timeout
     *                                    can cause instability in the system.
     */
    Timeout newTimeout(final TimerTask task, final long delay, final TimeUnit unit);

    /**
     * Releases all resources acquired by this {@link Timer} and cancels all
     * tasks which were scheduled but not executed yet.
     *
     * @return the handles associated with the tasks which were canceled by
     * this method
     */
    Set<Timeout> stop();
}
