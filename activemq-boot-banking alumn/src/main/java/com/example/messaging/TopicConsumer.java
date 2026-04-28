package com.example.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor genérico de tópico (exemplo original preservado).
 */
@Component
public class TopicConsumer {

    @JmsListener(destination = "demo.topic", containerFactory = "topicListenerContainerFactory")
    public void receive(String message) {
        System.out.println("TOPIC RECEIVED: " + message);
    }
}
