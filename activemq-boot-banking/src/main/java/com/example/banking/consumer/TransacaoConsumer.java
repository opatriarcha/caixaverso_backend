package com.example.banking.consumer;

import com.example.banking.config.RetryContext;
import com.example.banking.model.TransacaoBancaria;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Consumidor da fila de transações bancárias.
 *
 * Fluxo de retry:
 *   1. Mensagem chega → processamento é tentado
 *   2. Se lançar RuntimeException → sessão é revertida (sessionTransacted=true)
 *   3. ActiveMQ reentrega a mensagem automaticamente, até MAX_REDELIVERIES vezes
 *   4. Após esgotar as tentativas, o broker move a mensagem para a DLQ:
 *      transacao-bancaria.DLQ
 *
 * O listener da DLQ (inner class) registra a mensagem morta para auditoria.
 */
@Component
public class TransacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransacaoConsumer.class);

    @Value("${banking.transaction.retry-max-attempts}")
    private int maxReentregas;

    // -------------------------------------------------------------------------
    // Consumidor principal — transacao-bancaria.queue
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.queues.transaction}",
            containerFactory = "jmsListenerContainerFactory"
    )
    public void processarTransacao(TransacaoBancaria transacao, Message jmsMessage) throws Exception {
        int deliveryCount = jmsMessage.getIntProperty("JMSXDeliveryCount");
        int tentativa = RetryContext.tentativaAtual(deliveryCount);
        boolean ultimaTentativa = RetryContext.isUltimaTentativa(deliveryCount, maxReentregas);

        log.info("[TRANSACAO] Tentativa {}/{} | ID={} | Tipo={} | Valor={}",
                tentativa, maxReentregas,
                transacao.getId(), transacao.getTipo(), transacao.getValor());

        if (ultimaTentativa) {
            log.error("[TRANSACAO] Última tentativa ({}) para ID={}. Próximo passo: DLQ.",
                    tentativa, transacao.getId());
        }

        validarEProcessar(transacao);

        log.info("[TRANSACAO] Processada com sucesso | ID={}", transacao.getId());
    }

    /**
     * Lógica de negócio simulada.
     * Transações acima de R$50.000 lançam exceção para demonstrar o ciclo de retry.
     */
    private void validarEProcessar(TransacaoBancaria transacao) {
        if (transacao.getValor().compareTo(new BigDecimal("50000")) > 0) {
            log.warn("[TRANSACAO] Valor suspeito detectado (R$ {}). Falha intencional para demonstrar retry.",
                    transacao.getValor());
            throw new IllegalStateException(
                    "Transação bloqueada por valor acima do limite: " + transacao.getValor());
        }

        // Simula processamento normal
        log.debug("[TRANSACAO] Débito em '{}' e crédito em '{}' registrados.",
                transacao.getContaOrigem(), transacao.getContaDestino());
    }

    // -------------------------------------------------------------------------
    // Consumidor da DLQ — transacao-bancaria.DLQ
    // -------------------------------------------------------------------------

    @JmsListener(
            destination = "${banking.transaction.queues.transaction-dlq}",
            containerFactory = "dlqListenerContainerFactory"
    )
    public void processarDlq(TransacaoBancaria transacao, Message jmsMessage) throws Exception {
        log.error("""
                ╔══════════════════════════════════════════════╗
                ║  [DLQ] TRANSAÇÃO MORTA — REQUER INTERVENÇÃO  ║
                ╚══════════════════════════════════════════════╝
                  ID        : {}
                  Tipo      : {}
                  Origem    : {}
                  Destino   : {}
                  Valor     : {}
                  Data/Hora : {}
                """,
                transacao.getId(),
                transacao.getTipo(),
                transacao.getContaOrigem(),
                transacao.getContaDestino(),
                transacao.getValor(),
                transacao.getDataHora()
        );

        // Aqui: persistir em tabela de auditoria, enviar alerta, acionar time de suporte, etc.
    }
}
