package com.chenx.smsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SMSController {
    private AtomicInteger count = new AtomicInteger();

    @GetMapping("/send")
    public String send(String orderId, int delaySecs) {
        int num = count.addAndGet(1);
        if (num > 1000) {
            if (delaySecs > 0) {
                try {
                    TimeUnit.SECONDS.sleep(1000L * delaySecs);
                } catch (InterruptedException e) {
                    return "interrupted: " + e.getMessage();
                }
            }
        }
        System.out.println(orderId + " send successfully");
        return "success";
    }
}
