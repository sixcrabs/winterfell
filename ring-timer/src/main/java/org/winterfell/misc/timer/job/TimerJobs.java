package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;

import java.time.Duration;

/**
 * <p>
 * static factory methods
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public class TimerJobs {

    private TimerJobs() {
    }

    public static TimerCronJob newCronJob(String name, String cron, TimerTask timerTask) {
        return new TimerCronJob(name, TimerJobType.CRON, timerTask, cron);
    }

    public static TimerFixedRateJob newFixedRateJob(String name, Duration fixedTime, TimerTask timerTask) {
        return new TimerFixedRateJob(name, TimerJobType.FIXED_RATE, timerTask, fixedTime);
    }

    public static TimerDelayedJob newDelayJob(String name, Duration delayTime, TimerTask timerTask) {
        return new TimerDelayedJob(name, TimerJobType.DELAY, timerTask, delayTime);
    }
}
