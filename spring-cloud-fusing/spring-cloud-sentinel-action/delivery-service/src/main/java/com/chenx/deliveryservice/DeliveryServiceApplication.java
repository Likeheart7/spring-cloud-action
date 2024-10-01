package com.chenx.deliveryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient(autoRegister = false)
public class DeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

    @RestController
    public class DeliveryController {
        @Autowired
        private SMSService smsService;

        @GetMapping("/delivery")
        public String delivery(String orderId) {
            String smsResult = smsService.send(orderId, 5);
            if (smsResult.equalsIgnoreCase("sms service has some problems")) {
                return "delivery " + orderId + " failed: " + smsResult;
            } else {
                return "delivery " + orderId + " success";
            }
        }
    }

    @Bean
    public FallbackFactory fallbackFactory() {
        return new FallbackFactory();
    }
    @FeignClient(name = "sms-service", fallbackFactory = FallbackFactory.class)
    public interface SMSService {

        @GetMapping("/send")
        String send(@RequestParam("orderId") String orderId, @RequestParam("delaySecs") int delaySecs);

    }

    class FallbackInventoryService implements SMSService {

        @Override
        public String send(String orderId, int delaySecs) {
            return "sms service has some problems";
        }
    }

    class FallbackFactory implements feign.hystrix.FallbackFactory {

        private FallbackInventoryService fallbackService = new FallbackInventoryService();

        @Override
        public Object create(Throwable cause) {
            return fallbackService;
        }
    }


}

