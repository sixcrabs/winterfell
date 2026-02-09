package io.github.sixcrabs.winterfell.timer.job;

import io.github.sixcrabs.winterfell.timer.TimerTask;

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