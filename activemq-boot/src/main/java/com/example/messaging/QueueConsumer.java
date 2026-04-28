package com.example.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    @JmsListener(destination = "demo-queue")
    public void receive( String message ){
        this.executeBusinessLogic(message);
    }

    private void executeBusinessLogic(String message) {
        System.out.println("MENSAGEM RECEBIDA NO QUEUE CONSUMER: " + message);
    }
}
