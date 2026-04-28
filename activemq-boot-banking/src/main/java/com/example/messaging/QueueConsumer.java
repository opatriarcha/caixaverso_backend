package com.example.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor genérico de fila (exemplo original preservado).
 */
@Component
public class QueueConsumer {

    @JmsListener(destination = "demo-queue")
    public void receive(String message) {
        System.out.println("MENSAGEM RECEBIDA NO QUEUE CONSUMER: " + message);
    }
}
