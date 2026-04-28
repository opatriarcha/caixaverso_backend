package com.example.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumer {

    @JmsListener(destination = "demo.topic")
    public void receive(String message){
        System.out.println("TOPIC RECEIVED: " + message);
    }
}
