package com.chenx.springmessaging.controller;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @MessageMapping("/subscribe")
    public void subscribe() {
        messageTemplate.convertAndSend("/topic/tom", "jerry");
        messageTemplate.convertAndSend("/topic/jerry", "tom");
    }
    @MessageMapping("/payload")
    public void payload(@Payload User user, @Header(value="content-type") String contentType) {
        System.out.println("payload: " + user);
        System.out.println("header ContentType: " + contentType);
    }


    @MessageMapping("/path/{var}")
    public void path(@DestinationVariable("var") String var, Message message) {
        System.out.println("receive: " + message);
    }

    @MessageMapping("/message")
    public void message(String msg) {
        if (msg.contains("input1")) {
            messageTemplate.convertAndSend("/topic/messages1" ,msg);
        } else if (msg.contains("input2")) {
            messageTemplate.convertAndSend("/topic/messages2", msg);
        } else if (msg.contains("input3")) {
            messageTemplate.convertAndSend("/topic/messages3", msg);
        } else {
            throw new IllegalStateException("unknown message");
        }
    }


    @MessageExceptionHandler
    @SendTo
    public String handleException(Throwable ex) {
        return ex.getMessage();
    }

    static class User {

        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public User() {
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
