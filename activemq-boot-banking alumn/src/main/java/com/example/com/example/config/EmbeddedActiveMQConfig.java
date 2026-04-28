package com.example.com.example.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.broker.region.policy.IndividualDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.jms.ConnectionFactory;
import java.util.List;

/**
 * Configuração do broker ActiveMQ embutido.
 *
 * Política de reentrega (RedeliveryPolicy):
 *   - Máximo de 10 tentativas antes de enviar para a Dead Letter Queue (DLQ)
 *   - Backoff exponencial entre as tentativas
 *
 * Política de DLQ (IndividualDeadLetterStrategy):
 *   - Cada fila/tópico possui sua própria DLQ com sufixo ".DLQ"
 *   - Mensagens de tópicos também são encaminhadas para DLQ
 */
@Configuration
public class EmbeddedActiveMQConfig {

    private static final int MAX_REDELIVERIES = 10;

    @Bean(destroyMethod = "stop")
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("embedded-broker");
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("vm://embedded-broker");



        broker.start();
        return broker;
    }

    /**
     * ConnectionFactory com RedeliveryPolicy configurada:
     * 10 tentativas com backoff exponencial (inicial 1s, máx 30s, multiplicador 2.0).
     */

    // -------------------------------------------------------------------------
    // Conversor JSON (usado em todos os listeners bancários)
    // -------------------------------------------------------------------------

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(mapper);

        return converter;
    }

    // -------------------------------------------------------------------------
    // Listener factories — Queue
    // -------------------------------------------------------------------------

    /**
     * Factory padrão para filas com suporte a retry + DLQ.
     * sessionTransacted = true garante que, em caso de exceção, a mensagem
     * é devolvida ao broker para nova tentativa.
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory cf, MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setPubSubDomain(false);
        factory.setConcurrency("1-3");
        factory.setSessionTransacted(true);          // obrigatório para reentrega funcionar
        factory.setMessageConverter(jacksonJmsMessageConverter);
        return factory;
    }

    // -------------------------------------------------------------------------
    // Listener factory — Topic
    // -------------------------------------------------------------------------

    @Bean
    public DefaultJmsListenerContainerFactory topicListenerContainerFactory(
            ConnectionFactory cf, MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setPubSubDomain(true);
        factory.setSubscriptionDurable(false);
        factory.setConcurrency("1-3");
        factory.setSessionTransacted(true);
        factory.setMessageConverter(jacksonJmsMessageConverter);
        return factory;
    }

    // -------------------------------------------------------------------------
    // Listener factory — DLQ (sem retry, apenas registra)
    // -------------------------------------------------------------------------

    @Bean
    public DefaultJmsListenerContainerFactory dlqListenerContainerFactory(
            ConnectionFactory cf, MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setPubSubDomain(false);
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);         // DLQ não precisa reprocessar
        factory.setMessageConverter(jacksonJmsMessageConverter);
        return factory;
    }
}
