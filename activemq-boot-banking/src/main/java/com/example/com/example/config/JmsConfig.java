package com.example.com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.ConnectionFactory;

@Configuration
public class JmsConfig {

    @Bean
    public JmsTemplate queueJmsTemplate(ConnectionFactory cf, MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jt = new JmsTemplate(cf);
        jt.setPubSubDomain(false);
        jt.setMessageConverter(jacksonJmsMessageConverter);
        return jt;
    }

    @Bean
    public JmsTemplate topicJmsTemplate(ConnectionFactory cf, MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jt = new JmsTemplate(cf);
        jt.setPubSubDomain(true);
        jt.setMessageConverter(jacksonJmsMessageConverter);
        return jt;
    }
}
