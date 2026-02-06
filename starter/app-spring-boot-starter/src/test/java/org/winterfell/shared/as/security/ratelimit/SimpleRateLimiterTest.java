package org.winterfell.shared.as.security.ratelimit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/11/11
 */
public class SimpleRateLimiterTest {

    @Test
    void tryAcquire() throws InterruptedException {

        // 创建一个容量为100，每秒生成10个令牌的限流器
        LocalRateLimiter limiter = new LocalRateLimiter(100, 10);

        System.out.println("=== 令牌桶限流器测试 ===");
        System.out.println("容量: " + limiter.getCapacity());
        System.out.println("速率: " + limiter.getTokensPerSecond() + " 令牌/秒");
        System.out.println();

        // 测试1：单线程快速请求
        System.out.println("测试1：单线程快速请求");
        for (int i = 0; i < 15; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + (i + 1) + ": " +
                    (acquired ? "成功" : "失败") +
                    " (剩余令牌: " + limiter.getAvailableTokens() + ")");
        }

        System.out.println("\n等待2秒补充令牌...");
        Thread.sleep(2000);
        System.out.println("当前令牌数: " + limiter.getAvailableTokens());

        // 测试2：多线程并发请求
        System.out.println("\n测试2：多线程并发请求（10个线程）");
        final int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    boolean acquired = limiter.tryAcquire();
                    System.out.println("线程" + threadId + "-请求" + j + ": " +
                            (acquired ? "成功" : "失败"));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n最终令牌数: " + limiter.getAvailableTokens());
    }

    @Test
    void acquire() {
    }

    @Test
    void getAvailableTokens() {
    }

    @Test
    void getCapacity() {
    }
}