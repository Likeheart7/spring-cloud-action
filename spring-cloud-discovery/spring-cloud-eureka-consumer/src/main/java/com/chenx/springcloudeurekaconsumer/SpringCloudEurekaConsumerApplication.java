package com.chenx.springcloudeurekaconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false) // consumer不用注册，它只需要访问注册的服务
public class SpringCloudEurekaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaConsumerApplication.class, args);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    class EurekaHelloController{
        private String serviceName = "eureka-provider";
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private DiscoveryClient discoveryClient;

        @GetMapping("info")
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
        @GetMapping("hello")
        public String hello() {
            ServiceInstance instance = discoveryClient.getInstances(serviceName).stream().findAny().orElseThrow(IllegalStateException::new);
            return restTemplate.getForObject("http://" + instance.getHost() + ":" + instance.getPort() + "/echo?name=from-eureka-consumer", String.class);
        }
    }
}
