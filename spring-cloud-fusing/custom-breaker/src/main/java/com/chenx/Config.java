package com.chenx;

/**
 * 需要维护4个参数来处理状态的转换
 */
public class Config {
    private int failureCount = 5; // CLOSED状态进入OPEN状态得阈值
    private long failureTimeInterval = 2 * 1000; // failureCount统计时间窗口
    private int halfOpenTimeout = 5 * 1000; // OPEN状态进入HALF_OPEN状态得超时时间

    public int getFailureCount() {
        return failureCount;
    }

    public long getFailureTimeInterval() {
        return failureTimeInterval;
    }

    public int getHalfOpenTimeout() {
        return halfOpenTimeout;
    }

    public int getHalfOpenSuccessCount() {
        return halfOpenSuccessCount;
    }

    private int halfOpenSuccessCount = 2; // HALF_OPEN切换到CLOSED得成功个数阈值
}
