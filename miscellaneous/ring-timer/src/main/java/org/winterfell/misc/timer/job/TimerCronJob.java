package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;
import org.winterfell.misc.timer.cron.CronUtil;

import java.text.ParseException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public class TimerCronJob extends TimerAbstractJob {

    private String cronExpression;

    public TimerCronJob(String name, TimerJobType type, TimerTask task, String cronExpression) {
        super(name, type, task);
        this.cronExpression = cronExpression;
    }

    /**
     * cron 表达式
     * @return
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * 时间配置是否有效
     * @return
     */
    public boolean isValid() {
        try {
            CronUtil.nextValidTime(cronExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
