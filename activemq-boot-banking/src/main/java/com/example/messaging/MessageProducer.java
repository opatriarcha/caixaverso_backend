package com.example.messaging;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Produtor genérico para filas e tópicos (exemplo original preservado).
 */
@Component
public class MessageProducer {

    private final JmsTemplate queueTemplate;
    private final JmsTemplate topicTemplate;

    public MessageProducer(
            @Qualifier("queueJmsTemplate") JmsTemplate queueTemplate,
            @Qualifier("topicJmsTemplate") JmsTemplate topicTemplate
    ) {
        this.queueTemplate = queueTemplate;
        this.topicTemplate = topicTemplate;
    }

    public void sendToQueue(String msg) {
        this.queueTemplate.convertAndSend("demo-queue", msg);
    }

    public void sendToTopic(String msg) {
        this.topicTemplate.convertAndSend("demo-topic", msg);
    }
}
