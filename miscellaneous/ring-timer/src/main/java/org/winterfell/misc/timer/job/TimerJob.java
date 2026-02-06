package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public interface TimerJob {

    String getName();

    TimerJobType getType();

    TimerTask getTask();
}
