package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;

import java.time.Duration;

/**
 * <p>
 * 延迟一定时间后执行一次（eg TTL）
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/28
 */
public class TimerDelayedJob extends TimerAbstractJob {

    private Duration delayTime;

    public TimerDelayedJob(String name, TimerJobType type, TimerTask task, Duration delayTime) {
        super(name, type, task);
        this.delayTime = delayTime;
    }

    public Duration getDelayTime() {
        return delayTime;
    }
}
