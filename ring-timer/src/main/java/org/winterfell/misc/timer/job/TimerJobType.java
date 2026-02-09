package org.winterfell.misc.timer.job;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public enum TimerJobType {
    // 固定频率执行
    FIXED_RATE,
    // cron 频率
    CRON,
    // 延迟多久执行（一次） ttl 场景
    DELAY;

}
