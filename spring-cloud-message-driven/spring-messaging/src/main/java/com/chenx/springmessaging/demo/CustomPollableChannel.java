package com.chenx.springmessaging.demo;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CustomPollableChannel implements PollableChannel {
    private final BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1000);
    @Override
    public Message<?> receive() {
        return queue.poll();
    }

    @Override
    public Message<?> receive(long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean send(Message<?> message, long timeout) {
        return queue.add(message);
    }

    public static void main(String[] args) {
        CustomPollableChannel channel = new CustomPollableChannel();
        channel.send(MessageBuilder.withPayload("custom payload1").setHeader("k1", "v1").build());
        channel.send(MessageBuilder.withPayload("custom payload2").setHeader("k2", "v2").build());
        channel.send(MessageBuilder.createMessage("custom payload3", new MessageHeaders(Collections.singletonMap("k3","v3"))));
        /*
        GenericMessage [payload=custom payload1, headers={k1=v1, id=73239f76-e7a8-e68b-f5ec-c63ef6f49a0c, timestamp=1727861389404}]
        GenericMessage [payload=custom payload2, headers={k2=v2, id=c54cdc5e-79ef-4b22-fa40-d4527b85939e, timestamp=1727861389405}]
        GenericMessage [payload=custom payload3, headers={k3=v3, id=56189e9f-3ad7-1804-725d-5eac17654a83, timestamp=1727861389405}]
         */
        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());  // null
    }
}
