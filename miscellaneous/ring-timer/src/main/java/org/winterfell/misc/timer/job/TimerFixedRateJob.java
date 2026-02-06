package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 以固定频率执行job
 * 以加入队列的时间为计算开始时间
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public class TimerFixedRateJob extends TimerAbstractJob {

    private Duration fixedTime;

    public TimerFixedRateJob(String name, TimerJobType type, TimerTask task, Duration fixedTime) {
        super(name, type, task);
        this.fixedTime = fixedTime;
    }

    public Duration getFixedTime() {
        return fixedTime;
    }

    /**
     * 根据时间单位转为不同的long值
     * @param timeUnit 时间单位
     * @return
     */
    public long toUnitValue(@Nonnull TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return fixedTime.toMinutes() * 60L;
            case DAYS:
                return fixedTime.toDays();
            case HOURS:
                return fixedTime.toHours();
            case MILLISECONDS:
                return fixedTime.toMillis();
            default:
            case MINUTES:
                return fixedTime.toMinutes();
        }
    }


}
