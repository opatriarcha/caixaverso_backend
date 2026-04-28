package com.example;

import com.example.banking.model.AlertaFraude;
import com.example.banking.model.EventoConta;
import com.example.banking.model.TransacaoBancaria;
import com.example.banking.producer.BankingProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * Testes de integração do fluxo bancário.
 *
 * Executa com broker embutido — nenhuma infraestrutura externa necessária.
 */
@SpringBootTest
class BankingIntegrationTest {

    @Autowired
    private BankingProducer producer;

    @Test
    void deveEnviarTransacaoNormal() {
        producer.enviarTransacao(
                TransacaoBancaria.nova("AG001-CC0001", "AG001-CC0002",
                        new BigDecimal("500.00"), TransacaoBancaria.TipoTransacao.TRANSFERENCIA)
        );
    }

    @Test
    void deveEnviarTransacaoQueIraFalhar() {
        // Transação acima do limite → demonstra ciclo de 10 retries antes da DLQ
        producer.enviarTransacao(
                TransacaoBancaria.nova("AG001-CC9999", "AG002-CC0001",
                        new BigDecimal("100000.00"), TransacaoBancaria.TipoTransacao.SAQUE)
        );
    }

    @Test
    void deveEnviarAlertaDeFraude() {
        producer.enviarAlertaFraude(
                AlertaFraude.de("TXN-TEST-001", "Transação em localização suspeita")
        );
    }

    @Test
    void devePublicarEventoNaTopicoDeContas() throws InterruptedException {
        producer.publicarEventoConta(
                EventoConta.de("CONTA-TEST-001", EventoConta.TipoEvento.CONTA_ABERTA,
                        "Nova conta corrente aberta via teste")
        );
        Thread.sleep(200); // aguarda processamento assíncrono do tópico
    }
}
