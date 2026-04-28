package com.example.banking.config;

/**
 * Utilitário para rastrear o número de tentativas de processamento de uma mensagem.
 *
 * O ActiveMQ incrementa a propriedade JMSXDeliveryCount automaticamente a cada reentrega.
 * Esta classe oferece um helper estático para ler esse contador de forma legível.
 */
public final class RetryContext {

    private RetryContext() {}

    /**
     * Retorna o número de tentativas a partir do JMSXDeliveryCount.
     * O valor começa em 1 na primeira entrega.
     *
     * @param deliveryCount valor de JMSXDeliveryCount lido do cabeçalho JMS
     * @return número da tentativa atual (começa em 1)
     */
    public static int tentativaAtual(int deliveryCount) {
        return deliveryCount;
    }

    /**
     * Verifica se esta é a última tentativa antes da DLQ.
     *
     * @param deliveryCount  valor de JMSXDeliveryCount
     * @param maxReentregas  número máximo configurado (ex.: 10)
     */
    public static boolean isUltimaTentativa(int deliveryCount, int maxReentregas) {
        return deliveryCount >= maxReentregas;
    }
}
