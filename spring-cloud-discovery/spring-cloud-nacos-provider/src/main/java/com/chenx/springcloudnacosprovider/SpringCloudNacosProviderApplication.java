package com.chenx.springcloudnacosprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class SpringCloudNacosProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudNacosProviderApplication.class, args);
    }

    @RestController
    class EchoController{
        @GetMapping("/echo")
        public String echo(@RequestParam(value = "name", required = false) String name) {
            return "echo: " + name;
        }
    }
}
