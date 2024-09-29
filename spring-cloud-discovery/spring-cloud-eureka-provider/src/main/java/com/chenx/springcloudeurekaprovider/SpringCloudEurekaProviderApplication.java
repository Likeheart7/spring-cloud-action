package com.chenx.springcloudeurekaprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringCloudEurekaProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaProviderApplication.class, args);
    }

    @RestController
    class EchoController{
        @GetMapping("/echo")
        public String echo(@RequestParam(value = "name", required = false) String name) {
            return "echo from eureka: " + name;
        }
    }
}
