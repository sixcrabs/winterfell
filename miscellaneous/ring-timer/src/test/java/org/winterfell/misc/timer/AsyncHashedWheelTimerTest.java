package org.winterfell.misc.timer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/2/9
 */
public class AsyncHashedWheelTimerTest {

    private long currentTime;
    private long executeTime;
    private Throwable executionRejectedThrowable;
    private Throwable executionFailedThrowable;

    @Test
    public void doTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setDaemon(true);

        final Timer timer = new AsyncHashedWheelTimer(threadFactoryBuilder.setNameFormat(
                "AsyncHashedWheelTimerTest").build(), 50, TimeUnit.MILLISECONDS, 10, 5, 10,
                threadFactoryBuilder.setNameFormat("Registry-DataNodeServiceImpl-WheelExecutor-%d")
                        .build(), new AsyncHashedWheelTimer.TaskFailedCallback() {
            @Override
            public void executionRejected(Throwable t) {
                System.out.println("---rejected: " + t.getLocalizedMessage());
                executionRejectedThrowable = t;
            }

            @Override
            public void executionFailed(Throwable t) {
                System.out.println("---failed: " + t.getLocalizedMessage());
                executionFailedThrowable = t;
                countDownLatch.countDown();
            }
        });

        currentTime = System.currentTimeMillis();
        executeTime = currentTime;

        timer.newTimeout((timeout) -> {
            executeTime = System.currentTimeMillis();
            countDownLatch.countDown();
            throw new Exception(String.format("[%s] execution failed.", Thread.currentThread().getName()));
        }, 1000, TimeUnit.MILLISECONDS);

        countDownLatch.await(3000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(executeTime >= currentTime + 1000);
        Assert.assertNull(executionRejectedThrowable);
        Assert.assertNotNull(executionFailedThrowable);
    }
}