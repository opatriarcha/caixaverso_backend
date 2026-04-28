package com.example.banking.config;

import com.example.banking.model.AlertaFraude;
import com.example.banking.model.EventoConta;
import com.example.banking.model.TransacaoBancaria;
import com.example.banking.producer.BankingProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Runner de demonstração do domínio bancário.
 *
 * Cenários executados na inicialização:
 *
 *   1. Transação normal (R$ 1.500)  → processada com sucesso
 *   2. Transferência suspeita (R$ 75.000) → falha em todas as 10 tentativas → DLQ
 *   3. Alerta de fraude             → processado com sucesso
 *   4. Evento de conta (tópico)     → entregue a todos os subscribers
 *
 * Aguarda 2 s entre cenários para separar os logs visualmente.
 */
@Component
public class BankingDemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BankingDemoRunner.class);

    private final BankingProducer producer;

    public BankingDemoRunner(BankingProducer producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("═══════════════════════════════════════════════");
        log.info("  DEMO BANCÁRIO — ACTIVEMQ EMBEDDED + DLQ");
        log.info("═══════════════════════════════════════════════");

        // ── Cenário 1: Transação normal ──────────────────────────────────────
        log.info("[DEMO] Cenário 1: transação dentro do limite (deve ser processada)");
        producer.enviarTransacao(
                TransacaoBancaria.nova("AG001-CC1234", "AG002-CC5678",
                        new BigDecimal("1500.00"), TransacaoBancaria.TipoTransacao.TRANSFERENCIA)
        );
        Thread.sleep(500);

        // ── Cenário 2: Transação que excede o limite → irá para DLQ ─────────
        log.info("[DEMO] Cenário 2: transação acima do limite — demonstra 10 retries antes da DLQ");
        producer.enviarTransacao(
                TransacaoBancaria.nova("AG001-CC9999", "AG003-CC0001",
                        new BigDecimal("75000.00"), TransacaoBancaria.TipoTransacao.SAQUE)
        );
        Thread.sleep(500);

        // ── Cenário 3: Alerta de fraude ──────────────────────────────────────
        log.info("[DEMO] Cenário 3: alerta de fraude para transação suspeita");
        producer.enviarAlertaFraude(
                AlertaFraude.de("TXN-SUSPEITA-42", "Múltiplas tentativas de saque em países diferentes")
        );
        Thread.sleep(500);

        // ── Cenário 4: Evento de conta via tópico ────────────────────────────
        log.info("[DEMO] Cenário 4: evento de conta publicado no tópico");
        producer.publicarEventoConta(
                EventoConta.de("CONTA-2024-001", EventoConta.TipoEvento.LIMITE_ALTERADO,
                        "Limite de crédito aumentado para R$ 10.000")
        );

        log.info("[DEMO] Mensagens enviadas. Aguarde o processamento nos logs acima.");
        log.info("Observe: a transação de R$ 75.000 será reprocessada {} vezes antes de ir para a DLQ.", 10);
    }
}
