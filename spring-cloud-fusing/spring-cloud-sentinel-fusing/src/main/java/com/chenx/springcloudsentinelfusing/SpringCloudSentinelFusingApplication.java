package com.chenx.springcloudsentinelfusing;

import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelConfigBuilder;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
public class SpringCloudSentinelFusingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSentinelFusingApplication.class, args);
    }

    @Bean
    public Customizer<SentinelCircuitBreakerFactory> customizer() {
        return factory -> {
            factory.configureDefault(id -> new SentinelConfigBuilder()
                            .resourceName(id)
                            .rules(Collections.singletonList(new DegradeRule(id)
                                    .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT)
                                    .setCount(3)
                                    .setTimeWindow(10)))
                            .build());
            factory.configure(builder -> {
                builder.rules(Collections.singletonList(new DegradeRule("slow")
                        .setGrade(RuleConstant.DEGRADE_GRADE_RT)
                        .setCount(100)
                        .setTimeWindow(5)));
            }, "rt");
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
