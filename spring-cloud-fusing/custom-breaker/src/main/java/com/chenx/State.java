package com.chenx;

/**
 * <pre>
 * 断路器拥有三种状态。
 * CLOSED:表示程序正常响应。当指定时间窗口内，出现的错误个数满足阈值，则进入OPEN状态。
 * OPEN：表示断路器开启，服务不可用。经过一段时间后，OPEN状态转入HALF_OPEN
 * HALF_OPEN：断路器半开，经过指定成功的个数后，切换到CLOSED状态。
 * </pre>
 */
public enum State {
    CLOSED,
    HALF_OPEN,
    OPEN;
}
