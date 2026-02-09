package org.winterfell.misc.timer;

import org.winterfell.misc.timer.cron.DateTimeUtil;
import org.winterfell.misc.timer.job.TimerJobs;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/2/9
 */
public class TimerManagerTest {


    private TimerManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new TimerManager(new TimerManager.TimerConfig(1, 24, TimeUnit.SECONDS)
                .setGuardCron("0 10 01 * * ?"));
        manager = new TimerManager(TimerManager.TimerConfig.DEFAULT);
        manager.start();
    }

    @Test
    public void addTimerJob() throws InterruptedException {
        manager.addTimerJob(TimerJobs.newDelayJob("delay-job", Duration.ofMinutes(1), timeout -> {
            System.out.printf(Thread.currentThread().getName());
            System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now()));
            System.out.println(timeout.isExpired());
        }));
        System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now()));
        Thread.sleep(300000);


    }

    @Test
    public void testCronJob() throws InterruptedException {
        manager.addTimerJob(TimerJobs.newCronJob("cron-job", "*/7 * * * * ?", timeout -> {
            boolean expired = timeout.isExpired();
            System.out.println("executed");
            System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now())+ expired);
        }));
        System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now()));
        Thread.sleep(40000);
        manager.removeTimerJob("test-job");
        Thread.sleep(500000);

    }

    @Test
    public void testFixedRateJob() throws InterruptedException {
        manager.addTimerJob(TimerJobs.newFixedRateJob("test-job", Duration.ofSeconds(8L), timeout -> {
            boolean expired = timeout.isExpired();
            System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now())+ expired);
        }));
        System.out.println(DateTimeUtil.formatDefault(LocalDateTime.now()));
        Thread.sleep(40000);
        manager.removeTimerJob("test-job");
        Thread.sleep(500000);

    }

    @Test
    public void removeTimerJob() {

        long seconds = TimeUnit.NANOSECONDS.toSeconds(48117803900L);
        System.out.println(seconds);
    }

    @Test
    public void close() {
    }

}