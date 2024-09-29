package com.chenx;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间窗口得统计需要临时保存上一次调用失败的时间戳，该时间戳需要和当前时间比较，确认是否超过时间窗口
 * 当前失败次数也需要记录，该次数会与错误个数阈值比较，确认是否进入OPEN状态。
 */
public class Counter {
    // CLOSED状态进入OPEN状态的错误个数阈值
    private final int failureCount;
    // failureCount统计的时间窗口
    private final long failureTimeInterval;
    // 当前错误次数
    private final AtomicInteger currentCount;
    // 上一次调用失败的时间戳
    private long lastTime;

    //HALF_OPEN下成功的次数
    private final AtomicInteger halfOpenSuccessCount;

    public Counter(int failureCount, long failureTimeInterval) {
        this.failureCount = failureCount;
        this.failureTimeInterval = failureTimeInterval;
        this.currentCount = new AtomicInteger(0);
        this.halfOpenSuccessCount = new AtomicInteger(0);
        this.lastTime = System.currentTimeMillis();
    }

    public synchronized int incrFailureCount() {
        long current = System.currentTimeMillis();
        if (current - lastTime > failureTimeInterval) { // 超出时间窗口，当前失败次数清零
            this.currentCount.set(0);
            lastTime = current;
        }
        return this.currentCount.getAndIncrement();
    }

    public int incrSuccessHalfOpenCount() {
        return this.halfOpenSuccessCount.incrementAndGet();
    }

    public boolean failureThresholdReached() {
        return getCurCount() >= this.failureCount;
    }

    public int getCurCount() {
        return this.currentCount.get();
    }

    public synchronized void reset() {
        this.halfOpenSuccessCount.set(0);
        this.currentCount.set(0);
    }
}
