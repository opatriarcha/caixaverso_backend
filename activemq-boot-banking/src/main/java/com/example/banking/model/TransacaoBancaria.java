package com.example.banking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma transação bancária enviada via mensageria.
 */
public class TransacaoBancaria {

    public enum TipoTransacao {
        TRANSFERENCIA,
        PAGAMENTO,
        DEPOSITO,
        SAQUE
    }

    private final String id;
    private final String contaOrigem;
    private final String contaDestino;
    private final BigDecimal valor;
    private final TipoTransacao tipo;
    private final LocalDateTime dataHora;

    @JsonCreator
    public TransacaoBancaria(
            @JsonProperty("id") String id,
            @JsonProperty("contaOrigem") String contaOrigem,
            @JsonProperty("contaDestino") String contaDestino,
            @JsonProperty("valor") BigDecimal valor,
            @JsonProperty("tipo") TipoTransacao tipo,
            @JsonProperty("dataHora") LocalDateTime dataHora) {
        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    /** Construtor de conveniência para criação rápida. */
    public static TransacaoBancaria nova(
            String contaOrigem, String contaDestino,
            BigDecimal valor, TipoTransacao tipo) {
        return new TransacaoBancaria(
                UUID.randomUUID().toString(),
                contaOrigem,
                contaDestino,
                valor,
                tipo,
                LocalDateTime.now()
        );
    }

    public String getId() { return id; }
    public String getContaOrigem() { return contaOrigem; }
    public String getContaDestino() { return contaDestino; }
    public BigDecimal getValor() { return valor; }
    public TipoTransacao getTipo() { return tipo; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return "TransacaoBancaria{id='%s', origem='%s', destino='%s', valor=%s, tipo=%s}"
                .formatted(id, contaOrigem, contaDestino, valor, tipo);
    }
}
