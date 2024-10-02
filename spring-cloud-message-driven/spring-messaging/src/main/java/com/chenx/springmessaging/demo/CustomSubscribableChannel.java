package com.chenx.springmessaging.demo;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订阅方式消费消息
 */
public class CustomSubscribableChannel extends AbstractSubscribableChannel {
    private final Random random = new Random();

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        if (message == null || CollectionUtils.isEmpty(getSubscribers())) {
            return false;
        }
        Iterator<MessageHandler> iterator = getSubscribers().iterator();
        int index = 0, targetIndex = random.nextInt(getSubscribers().size());
        while (iterator.hasNext()) {
            MessageHandler handler = iterator.next();
            if (index == targetIndex) {
                handler.handleMessage(message);
                return true;
            }
            index++;
        }
        return false;
    }

    public static void main(String[] args) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CustomSubscribableChannel channel = new CustomSubscribableChannel();

        channel.addInterceptor(new ChannelInterceptor() {
            /**
             * 如果消息头含有ignoreKey且值为true，就忽略这条消息
             */
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                String ignoreKey = "ignore";
                if (message.getHeaders().containsKey(ignoreKey) &&
                        message.getHeaders().get(ignoreKey, Boolean.class)) {
                    return null;
                }
                return message;
            }

            /**
             * 发送后的统计
             */
            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                if (sent) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }
        });
        // 此时还没有订阅，这条消息会被忽略，并且计入failCount
        channel.send(MessageBuilder
                .withPayload("custom payload1")
                .setHeader("k1", "v1")
                .build()
        );
        // 三个匿名MessageHandler订阅该channel
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler1 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler2 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler3 receive: " + msg);
        });

        // 该消息会被正常消费
        channel.send(MessageBuilder
                .withPayload("customer payload2")
                .setHeader("k2", "v2")
                .build()
        );
        // 该消息Header存在ignoreKey且为true，会被过滤掉
        channel.send(MessageBuilder
                .createMessage("custom payload3", new MessageHeaders(Collections.singletonMap("ignore", true))));
        /*
        [main] handler3 receive: GenericMessage [payload=customer payload2, headers={k2=v2, id=8de51b81-577c-f9ac-8eeb-d572303a9bac, timestamp=1727860885683}]
        successCount:1, failCount: 1
         */
        System.out.println("successCount:" + successCount.get() + ", failCount: " + failCount.get());
    }
}
