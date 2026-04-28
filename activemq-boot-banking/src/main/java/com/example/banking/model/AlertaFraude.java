package com.example.banking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Alerta de fraude gerado para transações suspeitas.
 */
public class AlertaFraude {

    private final String transacaoId;
    private final String motivo;
    private final LocalDateTime geradoEm;

    @JsonCreator
    public AlertaFraude(
            @JsonProperty("transacaoId") String transacaoId,
            @JsonProperty("motivo") String motivo,
            @JsonProperty("geradoEm") LocalDateTime geradoEm) {
        this.transacaoId = transacaoId;
        this.motivo = motivo;
        this.geradoEm = geradoEm;
    }

    public static AlertaFraude de(String transacaoId, String motivo) {
        return new AlertaFraude(transacaoId, motivo, LocalDateTime.now());
    }

    public String getTransacaoId() { return transacaoId; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getGeradoEm() { return geradoEm; }

    @Override
    public String toString() {
        return "AlertaFraude{transacaoId='%s', motivo='%s'}".formatted(transacaoId, motivo);
    }
}
