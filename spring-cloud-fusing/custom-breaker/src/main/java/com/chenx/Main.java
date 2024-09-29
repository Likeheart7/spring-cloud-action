package com.chenx;

import com.chenx.exception.DegradeException;

public class Main {
    public static void main(String[] args) {
        testNormal();
        testException();
        testOpen();
    }

    /**
     * 结果是从第6次开始，后续输出全是degrade，也就意味着断路器进入OPEN状态
     * 不过这里有个问题，就是在多个错误执行之间如果有一个正确的执行，会将对错误执行的统计reset，
     * 也就是说需要连续5个错误才能触发熔断
     */
    private static void testOpen() {
        CircuitBreaker cb = new CircuitBreaker(new Config());
        // 默认Config错误阈值是5
        for (int i = 0; i < 10; i++) {
            System.out.println(cb.run(Main::generateException,
                    t -> {
                        if (t instanceof DegradeException) {
                            return "degrade";
                        }
                        return "boom";
                    }));
        }
//        cb.run(() -> "main", t -> "normal");
//        for (int i = 0; i < 5; i++) {
//            System.out.println(cb.run(Main::generateException,
//                    t -> {
//                        if (t instanceof DegradeException) {
//                            return "degrade";
//                        }
//                        return "boom";
//                    }));
//        }
    }

    /**
     * 测试CLOSED状态出现异常的情况
     */
    private static void testException() {
        CircuitBreaker cb = new CircuitBreaker(new Config());
        String result = cb.run(Main::generateException, t -> "boom");
        System.out.println(result);
    }

    /**
     * 测试CLOSED状态正常处理的情况
     */
    private static void testNormal() {
        CircuitBreaker cb = new CircuitBreaker(new Config());
        String bookName = cb.run(() -> "deep in spring cloud fusing", t -> "boom");
        System.out.println(bookName);
    }

    private static String generateException() {
        int i = 1 / 0;
        return "success";
    }
}
