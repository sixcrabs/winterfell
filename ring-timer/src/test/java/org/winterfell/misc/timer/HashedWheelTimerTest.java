package org.winterfell.misc.timer;

import org.winterfell.misc.timer.cron.DateTimeUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/2/9
 */
public class HashedWheelTimerTest {



    HashedWheelTimer heartbeatTimer;

    long baseDelay = 5;

    @Before
    public void setUp() throws Exception {
        // 定义一个 tick 间隔是 1s 的共 12个槽位的时间轮
        heartbeatTimer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat("heartbeat-timer").setDaemon(true).build(),
                1, TimeUnit.SECONDS,
                12);
    }

    @Test
    public void start() {

        Timeout target = null;
        for (int i = 1; i < 11; i++) {
            Timeout timeout = this.heartbeatTimer.newTimeout(new MyTimerTask("alex-" + i), i * baseDelay, TimeUnit.SECONDS);
            if (i == 9) {
                target = timeout;
            }
        }
        // 该任务会被cancel
        target.cancel();

        System.out.println("---------start-------");
        System.out.println(DateTimeUtil.formatNow("HH:mm:ss"));
        try {
            Thread.sleep(50000);
            System.out.println("---------end-------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        if (this.heartbeatTimer != null) {
            this.heartbeatTimer.stop();
        }
    }

    private class MyTimerTask implements TimerTask {

        private String name;

        public MyTimerTask(String name) {
            this.name = name;
        }

        /**
         * Executed after the delay specified with
         * Timer#newTimeout(TimerTask, long, TimeUnit).
         *
         * @param timeout a handle which is associated with this task
         */
        @Override
        public void run(Timeout timeout) throws Exception {
            System.out.println(String.format("%s: [%s]-[%s] is on the stage now, pending num %d", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")),
                    this.name, Thread.currentThread().getName(), heartbeatTimer.pendingTimeouts()));
        }
    }

}