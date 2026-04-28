package com.example.banking.consumer;

import com.example.banking.config.RetryContext;
import com.example.banking.model.AlertaFraude;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de alertas de fraude.
 *
 * Demonstra retry + DLQ em uma segunda fila bancária.
 * Simula falha na integração com o sistema antifraude externo.
 */
@Component
public class AlertaFraudeConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertaFraudeConsumer.class);

    @Value("${banking.transaction.retry-max-attempts}")
    private int maxReentregas;

    // -------------------------------------------------------------------------
    // Consumidor principal — alerta-fraude.queue
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.queues.fraud-alert}",
            containerFactory = "jmsListenerContainerFactory"
    )
    public void processarAlerta(AlertaFraude alerta, Message jmsMessage) throws Exception {
        int deliveryCount = jmsMessage.getIntProperty("JMSXDeliveryCount");
        int tentativa = RetryContext.tentativaAtual(deliveryCount);
        boolean ultimaTentativa = RetryContext.isUltimaTentativa(deliveryCount, maxReentregas);

        log.warn("[FRAUDE] Tentativa {}/{} | TransacaoID={} | Motivo={}",
                tentativa, maxReentregas, alerta.getTransacaoId(), alerta.getMotivo());

        if (ultimaTentativa) {
            log.error("[FRAUDE] Última tentativa para alerta da transação {}. Enviando para DLQ.",
                    alerta.getTransacaoId());
        }

        notificarSistemaAntifraude(alerta);

        log.info("[FRAUDE] Alerta processado com sucesso para transação {}", alerta.getTransacaoId());
    }

    /**
     * Simula integração com sistema antifraude externo.
     * Lança exceção na 1ª e 2ª tentativas para demonstrar o retry.
     */
    private void notificarSistemaAntifraude(AlertaFraude alerta) {
        // Em produção: chamada HTTP/gRPC ao serviço de antifraude
        log.info("[FRAUDE] Notificando sistema antifraude: {}", alerta);
    }

    // -------------------------------------------------------------------------
    // Consumidor da DLQ — alerta-fraude.DLQ
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.queues.fraud-alert-dlq}",
            containerFactory = "dlqListenerContainerFactory"
    )
    public void processarDlq(AlertaFraude alerta, Message jmsMessage) throws Exception {
        log.error("""
                ╔════════════════════════════════════════╗
                ║  [DLQ] ALERTA DE FRAUDE NÃO ENTREGUE  ║
                ╚════════════════════════════════════════╝
                  TransacaoID : {}
                  Motivo      : {}
                  Gerado em   : {}
                """,
                alerta.getTransacaoId(),
                alerta.getMotivo(),
                alerta.getGeradoEm()
        );

        // Aqui: acionar canal alternativo (e-mail, SMS, dashboard de operações)
    }
}
