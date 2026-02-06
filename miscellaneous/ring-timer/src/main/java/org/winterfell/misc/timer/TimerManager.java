package org.winterfell.misc.timer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.winterfell.misc.timer.cron.CronUtil;
import org.winterfell.misc.timer.cron.DateTimeUtil;
import org.winterfell.misc.timer.job.TimerCronJob;
import org.winterfell.misc.timer.job.TimerDelayedJob;
import org.winterfell.misc.timer.job.TimerFixedRateJob;
import org.winterfell.misc.timer.job.TimerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * .定时器管理类
 * - 初始化定时器
 * - 增加定时任务
 * - 移除定时任务
 * <p>
 * 注: 如果定时任务执行间隔与 时间轮的扫描间隔相同， 则表现出会延迟一个 tick(建议定时器的tick间隔时间单位精确于定时任务的时间间隔)
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/26
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TimerManager {

    public static final Logger logger = LoggerFactory.getLogger(TimerManager.class);

    /**
     * 用于调起所有 worker timer
     */
    private static final HashedWheelTimer GUARD_TIMER = new HashedWheelTimer(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("timer-guard-thread-%s")
            .build(), 10, TimeUnit.SECONDS, 60);

//    /**
//     * 工作线程池 用于处理各类工作任务
//     */
//    private static final ExecutorService taskPool = new ThreadPoolExecutor(64, 128,
//            30L, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(512), new ThreadFactoryBuilder()
//            .setNameFormat("timer-task-thread-%d").setDaemon(true).build(), new ThreadPoolExecutor.AbortPolicy());

    /**
     * worker 定时器
     */
    private final HashedWheelTimer workerTimer;

    private final TimerConfig config;

    /**
     * 定时任务 支持增加和删除
     */
    private final List<TimerJob> jobs = new CopyOnWriteArrayList();

    /**
     * 存放所有的job name -- timeouts
     */
    private static final Map<String, List<Timeout>> JOB_TIMEOUTS = new ConcurrentHashMap<>(1);


    public TimerManager(TimerConfig config) {
        this.config = config;
        this.workerTimer = new HashedWheelTimer(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("timer-thread-%s")
                .build(), config.getTickDuration(), config.getTimeUnit(), config.getTicksPerWheel());
    }

    public TimerManager() {
        this.config = TimerConfig.DEFAULT;
        this.workerTimer = new HashedWheelTimer(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("timer-thread-%s")
                .build(), config.getTickDuration(), config.getTimeUnit(), config.getTicksPerWheel());
    }

    /**
     * 开启守卫 use {@link #start()} instead
     */
    @Deprecated
    public void startGuard() {
        init();
    }

    /**
     * 开启一个 manager
     */
    public  void start() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (config.getGuardCron() != null) {
            initGuardTimer(null);
        }
    }

    /**
     * 添加定时器任务
     *
     * @param job
     */
    public boolean addTimerJob(TimerJob job) {
        jobs.add(job);
        try {
            parseAndAdd(job, nextGuardTime());
            return true;
        } catch (Exception e) {
            //
            logger.error("[timer] add job [{}] error: {}", job.getName(), e.getLocalizedMessage());
            jobs.remove(job);
        }
        return false;
    }

    /**
     * 移除某个定时任务（进入定时队列的不会取消）
     *
     * @param name
     */
    public void removeTimerJob(String name) {
        List<TimerJob> list = jobs.stream().filter(job -> name.equalsIgnoreCase(job.getName())).collect(Collectors.toList());
        list.forEach(job -> {
            List<Timeout> timeouts = JOB_TIMEOUTS.get(job.getName());
            if (timeouts != null) {
                timeouts.forEach(Timeout::cancel);
                JOB_TIMEOUTS.remove(job.getName());
            }
            jobs.remove(job);
        });
    }

    /**
     * 关闭定时器 慎用
     */
    private void shutdown() {
        workerTimer.stop();
        GUARD_TIMER.stop();
    }

    /**
     * 清除所有的jobs
     * cancel timeout
     *
     * @return
     */
    public boolean clearJobs() {
        jobs.forEach(job -> {
            // 取消所有的任务
            List<Timeout> timeouts = JOB_TIMEOUTS.get(job.getName());
            if (timeouts != null) {
                timeouts.forEach(Timeout::cancel);
                JOB_TIMEOUTS.remove(job.getName());
            }
        });
        jobs.clear();
        return true;
    }

    /**
     * 解析各类job 并加入到 timer中
     *
     * @param job
     * @param guardTime
     */
    private void parseAndAdd(TimerJob job, LocalDateTime guardTime) throws Exception {
        List<Timeout> timeouts = new ArrayList<>(1);
        switch (job.getType()) {
            case CRON:
                TimerCronJob cronJob = (TimerCronJob) job;
                try {
                    LocalDateTime now = LocalDateTime.now();
                    List<LocalDateTime> times = CronUtil.parse(cronJob.getCronExpression(), now, guardTime == null ? LocalDateTime.now().withHour(23)
                            .withMinute(59).withSecond(59) : guardTime);
                    if (times.size() > 0) {
                        StringBuilder buffer = new StringBuilder("\n--------<timer>------------\n [{}] trigger times in today(Show the next 5 times at most):");
                        for (int i = 0; i < Math.min(5, times.size()); i++) {
                            buffer.append("\n" + DateTimeUtil.formatDefault(times.get(i)));
                        }
                        buffer.append("\n------------------------------");
                        logger.info(buffer.toString(), job.getName());
                    }

                    times.forEach(dateTime -> {
                        long mills = DateTimeUtil.toTimestamp(dateTime) - DateTimeUtil.toTimestamp(now);
                        Timeout timeout = workerTimer.newTimeout(cronJob.getTask(), millsToUnitValue(mills, config.getTimeUnit()),
                                config.getTimeUnit());
                        timeouts.add(timeout);
                    });
                } catch (ParseException e) {
                    logger.error(e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
                break;
            case FIXED_RATE:
                TimerFixedRateJob fixedRateJob = (TimerFixedRateJob) job;
                try {
                    LocalDateTime dateTime = config.getGuardCron() != null ? CronUtil.nextValidTime(config.getGuardCron()) :
                            LocalDateTime.now().plusDays(1L).withHour(0).withMinute(0).withSecond(0);
                    // 计算出到下一次守卫时间来临前的所有执行时间
                    long nowMills = System.currentTimeMillis();
                    long diffMills = DateTimeUtil.toTimestamp(dateTime) - nowMills;
                    long fixedMills = fixedRateJob.toUnitValue(TimeUnit.MILLISECONDS);
                    long count = diffMills / fixedMills;
                    for (long i = 0; i < count; i++) {
                        Timeout timeout = workerTimer.newTimeout(fixedRateJob.getTask(),
                                millsToUnitValue(i * fixedMills, config.getTimeUnit()),
                                config.getTimeUnit());
                        timeouts.add(timeout);
                    }
                } catch (ParseException e) {
                    logger.error(e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
                break;
            case DELAY:
                TimerDelayedJob delayedJob = (TimerDelayedJob) job;
                long delayMills = delayedJob.getDelayTime().toMillis();
                Timeout timeout = workerTimer.newTimeout(delayedJob.getTask(),
                        millsToUnitValue(delayMills, config.getTimeUnit()),
                        config.getTimeUnit());
                timeouts.add(timeout);
                logger.info(timeout.toString());
                break;
            default:
                break;
        }
        List<Timeout> timeoutList = JOB_TIMEOUTS.get(job.getName());
        if (timeoutList == null) {
            JOB_TIMEOUTS.put(job.getName(), timeouts);
        } else {
            timeoutList.addAll(timeouts);
            JOB_TIMEOUTS.put(job.getName(), Lists.newArrayList(timeoutList));
        }
    }

    /**
     * mills to value based on {@linkplain TimeUnit}
     *
     * @param mills
     * @param timeUnit
     * @return
     */
    private long millsToUnitValue(long mills, TimeUnit timeUnit) {
        switch (timeUnit) {
            case HOURS:
                return mills / 1000 / 3600;
            case MINUTES:
                return mills / 1000 / 60;
            case SECONDS:
                return mills / 1000;
            case DAYS:
                return mills / 1000 / 3600 / 24;
            default:
            case MILLISECONDS:
                return mills;
        }
    }

    /**
     * 初始化守卫定时器
     *
     * @param timeout
     */
    private void initGuardTimer(Timeout timeout) {
        LocalDateTime nextGuardTime = null;
        try {
            nextGuardTime = CronUtil.nextValidTime(config.getGuardCron());
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
        }
        LocalDateTime finalNextGuardTime = Objects.requireNonNull(nextGuardTime);
        jobs.forEach(job -> {
            try {
                parseAndAdd(job, finalNextGuardTime);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        });
        GUARD_TIMER.newTimeout(this::initGuardTimer,
                nextGuardTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                TimeUnit.SECONDS);
    }

    /**
     * next guard time
     *
     * @return
     */
    private LocalDateTime nextGuardTime() {
        LocalDateTime nextGuardTime = null;
        if (config.getGuardCron() == null) {
            return null;
        }
        try {
            nextGuardTime = CronUtil.nextValidTime(config.getGuardCron());
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
        }
        return nextGuardTime;
    }


    /**
     * 定时器配置
     */
    public static class TimerConfig {

        private long tickDuration;

        private int ticksPerWheel;

        private TimeUnit timeUnit;

        private String guardCron;

        public TimerConfig(long tickDuration, int ticksPerWheel, TimeUnit timeUnit) {
            this.tickDuration = tickDuration;
            this.ticksPerWheel = ticksPerWheel;
            this.timeUnit = timeUnit;
        }

        public TimerConfig() {
        }

        /**
         * 默认配置
         */
        public static final TimerConfig DEFAULT = new TimerConfig().setTickDuration(1).setTimeUnit(TimeUnit.MINUTES).setTicksPerWheel(64)
                .setGuardCron("0 01 0 * * ?");

        public long getTickDuration() {
            return tickDuration;
        }

        public TimerConfig setTickDuration(long tickDuration) {
            this.tickDuration = tickDuration;
            return this;
        }

        public int getTicksPerWheel() {
            return ticksPerWheel;
        }

        public TimerConfig setTicksPerWheel(int ticksPerWheel) {
            this.ticksPerWheel = ticksPerWheel;
            return this;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public TimerConfig setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public String getGuardCron() {
            return guardCron;
        }

        public TimerConfig setGuardCron(String guardCron) {
            this.guardCron = guardCron;
            return this;
        }
    }


}
