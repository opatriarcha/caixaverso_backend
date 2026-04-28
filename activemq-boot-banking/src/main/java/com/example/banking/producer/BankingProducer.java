package com.example.banking.producer;

import com.example.banking.model.AlertaFraude;
import com.example.banking.model.EventoConta;
import com.example.banking.model.TransacaoBancaria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Produtor responsável por enviar mensagens bancárias às filas e tópicos.
 *
 * Destinos configurados via application.yml:
 *   - transacao-bancaria.queue  : processamento de transações
 *   - alerta-fraude.queue       : alertas de fraude detectados
 *   - eventos-conta.topic       : eventos de ciclo de vida da conta (pub/sub)
 */
@Component
public class BankingProducer {

    private static final Logger log = LoggerFactory.getLogger(BankingProducer.class);

    private final JmsTemplate queueTemplate;
    private final JmsTemplate topicTemplate;

    @Value("${banking.transaction.queues.transaction}")
    private String transacaoQueue;

    @Value("${banking.transaction.queues.fraud-alert}")
    private String alertaFraudeQueue;

    @Value("${banking.transaction.topics.account-events}")
    private String eventosContaTopic;

    public BankingProducer(
            @Qualifier("queueJmsTemplate") JmsTemplate queueTemplate,
            @Qualifier("topicJmsTemplate") JmsTemplate topicTemplate) {
        this.queueTemplate = queueTemplate;
        this.topicTemplate = topicTemplate;
    }

    /**
     * Envia uma transação bancária para a fila de processamento.
     */
    public void enviarTransacao(TransacaoBancaria transacao) {
        log.info("[PRODUTOR] Enviando transação {} para {}", transacao.getId(), transacaoQueue);
        queueTemplate.convertAndSend(transacaoQueue, transacao);
    }

    /**
     * Envia um alerta de fraude para a fila dedicada.
     */
    public void enviarAlertaFraude(AlertaFraude alerta) {
        log.warn("[PRODUTOR] Enviando alerta de fraude para transação {}", alerta.getTransacaoId());
        queueTemplate.convertAndSend(alertaFraudeQueue, alerta);
    }

    /**
     * Publica um evento de conta no tópico (todos os subscribers recebem).
     */
    public void publicarEventoConta(EventoConta evento) {
        log.info("[PRODUTOR] Publicando evento {} da conta {} no tópico", evento.getTipo(), evento.getContaId());
        topicTemplate.convertAndSend(eventosContaTopic, evento);
    }
}
