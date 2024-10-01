package com.chenx.springcloudsentinelfusing.controller;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DegradeController {

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/degrade")
    public String testSentinel() {
        /*
        exception occurs with url：https://localhost:8080/exception
        exception occurs with url：https://localhost:8080/exception
        exception occurs with url：https://localhost:8080/exception
        exception occurs with url：https://localhost:8080/exception
        exception occurs with url：https://localhost:8080/exception
        degrade by sentinel
        degrade by sentinel
        degrade by sentinel
        degrade by sentinel
        degrade by sentinel
         */
        return exp();
    }

    public String exp() {
        StringBuilder sb = new StringBuilder();
        CircuitBreaker cb = circuitBreakerFactory.create("temp");
        String url = "https://localhost:8080/exception";
        for (int i = 0; i < 10; i++) {
            String httpResult = cb.run(() -> {
                return restTemplate.getForObject(url, String.class);
            }, throwable -> {
                if (throwable instanceof DegradeException) {
                    return "degrade by sentinel";
                }
                return "exception occurs with url：" + url;
            });
            sb.append(httpResult).append("<br/>");
        }
        return sb.toString();
    }

    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException();
    }

}
