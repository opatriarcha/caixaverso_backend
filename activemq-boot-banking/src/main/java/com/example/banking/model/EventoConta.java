package com.example.banking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Evento de conta publicado em tópico (pub/sub).
 * Exemplos: CONTA_ABERTA, LIMITE_ALTERADO, CONTA_BLOQUEADA.
 */
public class EventoConta {

    public enum TipoEvento {
        CONTA_ABERTA,
        LIMITE_ALTERADO,
        CONTA_BLOQUEADA,
        CONTA_ENCERRADA
    }

    private final String contaId;
    private final TipoEvento tipo;
    private final String descricao;
    private final LocalDateTime ocorridoEm;

    @JsonCreator
    public EventoConta(
            @JsonProperty("contaId") String contaId,
            @JsonProperty("tipo") TipoEvento tipo,
            @JsonProperty("descricao") String descricao,
            @JsonProperty("ocorridoEm") LocalDateTime ocorridoEm) {
        this.contaId = contaId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.ocorridoEm = ocorridoEm;
    }

    public static EventoConta de(String contaId, TipoEvento tipo, String descricao) {
        return new EventoConta(contaId, tipo, descricao, LocalDateTime.now());
    }

    public String getContaId() { return contaId; }
    public TipoEvento getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }

    @Override
    public String toString() {
        return "EventoConta{contaId='%s', tipo=%s, descricao='%s'}".formatted(contaId, tipo, descricao);
    }
}
