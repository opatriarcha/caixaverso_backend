package com.example.com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

@Configuration
public class JmsConfig {

    @Bean
    public JmsTemplate queueJmsTemplate(ConnectionFactory cf) {
        JmsTemplate jt = new JmsTemplate(cf);
        jt.setPubSubDomain(false);
        return jt;
    }

    @Bean
    public JmsTemplate topicJmsTemplate(ConnectionFactory cf) {
        JmsTemplate jt = new JmsTemplate(cf);
        jt.setPubSubDomain(true);
        return jt;
    }
}
