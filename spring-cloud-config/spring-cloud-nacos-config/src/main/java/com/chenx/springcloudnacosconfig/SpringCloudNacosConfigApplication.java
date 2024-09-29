package com.chenx.springcloudnacosconfig;

import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringCloudNacosConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudNacosConfigApplication.class, args);
    }


    @RestController
    class ConfigurationController {
        @Autowired
        ApplicationContext context;

        // 从 Spring Cloud 2020.0（代号 Ilford） 开始，@Value
        // 注解可以直接动态更新配置，而无需依赖 @RefreshScope 注解
        @Value("${book.author:unknownAuthor}")
        String author;

        @GetMapping("/config")
        public String config() {
            StringBuilder sb = new StringBuilder();
            sb.append("env.get('book.category') = ")
                    .append(context.getEnvironment().getProperty("book.category", "unknown"))
                    .append("\n")
                    .append("env.get('book.author') = ")
                    .append(context.getEnvironment().getProperty("book.author"));
            return sb.toString();
        }
    }
}
