package com.chenx;

import com.chenx.exception.DegradeException;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.chenx.State.*;
/**
 * 用于状态的转换和代码的执行
 */
public class CircuitBreaker {
    private State state;
    private Config config;
    private Counter counter;
    private long lastOpenedTime;

    public CircuitBreaker(Config config) {
        this.counter = new Counter(config.getFailureCount(), config.getFailureTimeInterval());
        this.state = CLOSED;
        this.config = config;
    }

    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            if (state == OPEN) { // 断路器开启
                // 判断half_open是否超时，超时就执行HALF_OPEN的处理逻辑
                if (halfOpenTimeout()) {
                    return halfOpenHandle(toRun, fallback);
                }
                // 没超时，直接熔断
                return fallback.apply(new DegradeException("degrade by circuit breaker"));
            } else if (state == CLOSED) { // 熔断器处于关闭状态，执行
                T result = toRun.get();
                closed();
                return result;
            } else { // 半开处理逻辑
                return halfOpenHandle(toRun, fallback);
            }
        } catch (Exception e) {
            counter.incrFailureCount();
            if (counter.failureThresholdReached()) { // 错误数量达到阈值，进入OPEN状态
                open();
            }
            return fallback.apply(e);
        }
    }

    private <T> T halfOpenHandle(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            halfOpen(); // CLOSED状态超时，进入HALF_OPEN状态
            T result = toRun.get();
            int halfOpenSuccessCount = counter.incrSuccessHalfOpenCount();
            if (halfOpenSuccessCount >= this.config.getHalfOpenSuccessCount()) { // 成功个数达到阈值，进入CLOSED状态
                closed();
            }
            return result;
        } catch (Exception e) {
            // HALF_OPEN状态中，只要出现一次错误，进入OPEN状态
            open();
            return fallback.apply(new DegradeException("degrade by circuit breaker"));
        }
    }

    private boolean halfOpenTimeout() {
        return System.currentTimeMillis() - lastOpenedTime > config.getHalfOpenTimeout();
    }

    private void closed() {
        counter.reset();
        state = CLOSED;
    }

    private void open() {
        state = OPEN;
        lastOpenedTime = System.currentTimeMillis();
    }

    private void halfOpen() {
        state = HALF_OPEN;
    }

}
