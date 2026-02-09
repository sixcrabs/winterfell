package org.winterfell.misc.timer;

/**
 * A task which is executed after the delay specified with
 * Timer#newTimeout(TimerTask, long, TimeUnit).
 *
 *
 * @author Alex
 * @since v1.0, 2021/1/26
 */
public interface TimerTask {

    /**
     * Executed after the delay specified with
     * Timer#newTimeout(TimerTask, long, TimeUnit).
     *
     * @param timeout a handle which is associated with this task
     * @exception Exception
     */
    void run(final Timeout timeout) throws Exception;
}
