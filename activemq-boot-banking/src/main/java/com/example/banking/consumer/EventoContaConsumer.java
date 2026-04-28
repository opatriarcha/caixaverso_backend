package com.example.banking.consumer;

import com.example.banking.config.RetryContext;
import com.example.banking.model.EventoConta;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de conta publicados via tópico (pub/sub).
 *
 * O tópico eventos-conta.topic permite múltiplos subscribers.
 * Em caso de falha, as mensagens são reprocessadas até 10x e,
 * após esgotar as tentativas, movidas para eventos-conta.DLQ.
 *
 * Nota: a DLQ de tópicos é criada como queue (configuração padrão
 * do IndividualDeadLetterStrategy com useQueueForTopicMessages=true).
 */
@Component
public class EventoContaConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventoContaConsumer.class);

    @Value("${banking.transaction.retry-max-attempts}")
    private int maxReentregas;

    // -------------------------------------------------------------------------
    // Subscriber do tópico — eventos-conta.topic
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.topics.account-events}",
            containerFactory = "topicListenerContainerFactory"
    )
    public void receberEvento(EventoConta evento, Message jmsMessage) throws Exception {
        int deliveryCount = jmsMessage.getIntProperty("JMSXDeliveryCount");
        int tentativa = RetryContext.tentativaAtual(deliveryCount);
        boolean ultimaTentativa = RetryContext.isUltimaTentativa(deliveryCount, maxReentregas);

        log.info("[EVENTO-CONTA] Tentativa {}/{} | Conta={} | Tipo={}",
                tentativa, maxReentregas, evento.getContaId(), evento.getTipo());

        if (ultimaTentativa) {
            log.error("[EVENTO-CONTA] Última tentativa para conta {}. DLQ será acionada.", evento.getContaId());
        }

        processarEvento(evento);

        log.info("[EVENTO-CONTA] Evento {} processado para conta {}", evento.getTipo(), evento.getContaId());
    }

    private void processarEvento(EventoConta evento) {
        // Em produção: atualizar cache, notificar serviços downstream, etc.
        log.debug("[EVENTO-CONTA] Aplicando evento {} na conta {}: {}",
                evento.getTipo(), evento.getContaId(), evento.getDescricao());
    }

    // -------------------------------------------------------------------------
    // Consumidor da DLQ — eventos-conta.DLQ (queue)
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.topics.account-events-dlq}",
            containerFactory = "dlqListenerContainerFactory"
    )
    public void processarDlq(EventoConta evento, Message jmsMessage) throws Exception {
        log.error("""
                ╔═══════════════════════════════════════════╗
                ║  [DLQ] EVENTO DE CONTA NÃO PROCESSADO    ║
                ╚═══════════════════════════════════════════╝
                  ContaID     : {}
                  Tipo        : {}
                  Descricao   : {}
                  Ocorrido em : {}
                """,
                evento.getContaId(),
                evento.getTipo(),
                evento.getDescricao(),
                evento.getOcorridoEm()
        );

        // Aqui: reprocessamento manual, alertas de monitoramento, etc.
    }
}
