package com.chenx.springcloudnacosconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false) // 这是消费者，不自动注册到注册中心
public class SpringCloudNacosConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudNacosConsumerApplication.class, args);
    }

    /**
     * 注册一个RestTemplate，用于访问HTTP服务
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    class HelloController {
        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        // Spring Cloud Commons模块提供的一个服务发现接口，
        // Spring Cloud Alibaba Nacos Discovery模块内部会初始化一个它的实现类—NacosDiscoveryClient，用于后续的服务发现操作。
        private DiscoveryClient discoveryClient;

        private String serviceName = "nacos-provider"; // 目标服务名称


        @GetMapping("/info")
        public String info() {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("All services: ").append(discoveryClient.getServices()).append("<br/>");
            stringBuilder.append("nacos-provider instance list: <br/>");
            instances.forEach(instance -> {
                stringBuilder.append("[ serviceId: ")
                        .append(instance.getServiceId())
                        .append(", host: ")
                        .append(instance.getHost())
                        .append(", port: ")
                        .append(instance.getPort())
                        .append(" ]")
                        .append("<br/>");
            });
            return stringBuilder.toString();
        }

        @GetMapping("/hello")
        public String hello() {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            ServiceInstance instance = instances.stream().findAny().orElseThrow(() ->
                    new IllegalStateException("no " + serviceName + " instance available")
            );
            return restTemplate.getForObject("http://" + instance.getHost() + ":" + instance.getPort() + "/echo?name=from-nacos-consumer", String.class );
        }
    }
}
