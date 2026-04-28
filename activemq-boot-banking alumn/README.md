# activemq-demo — Mensageria Bancária com ActiveMQ Embutido

Projeto Spring Boot 3.2 + Java 21 demonstrando mensageria com **ActiveMQ embutido (in-memory)**.
Nenhum Docker ou broker externo é necessário — basta executar `mvn spring-boot:run`.

---

## Estrutura do Projeto

```
src/main/java/com/example/
├── DemoApplication.java                     # Ponto de entrada
│
├── messaging/                               # Exemplo original (queue e topic genéricos)
│   ├── MessageProducer.java
│   ├── QueueConsumer.java
│   └── TopicConsumer.java
│
├── banking/                                 # Domínio bancário com DLQ e retry
│   ├── model/
│   │   ├── TransacaoBancaria.java           # Modelo de transação
│   │   ├── AlertaFraude.java                # Alerta de fraude
│   │   └── EventoConta.java                 # Evento de ciclo de vida da conta
│   ├── producer/
│   │   └── BankingProducer.java             # Produtor bancário
│   ├── consumer/
│   │   ├── TransacaoConsumer.java           # Consumidor + DLQ de transações
│   │   ├── AlertaFraudeConsumer.java        # Consumidor + DLQ de alertas de fraude
│   │   └── EventoContaConsumer.java         # Subscriber de tópico + DLQ
│   └── config/
│       ├── RetryContext.java                # Helper para leitura do delivery count
│       └── BankingDemoRunner.java           # Runner de demonstração na inicialização
│
└── com/example/config/                      # Configuração do broker e JMS
    ├── EmbeddedActiveMQConfig.java
    └── JmsConfig.java
```

---

## Filas e Tópicos

| Destino                    | Tipo   | Propósito                                  | DLQ correspondente           |
|----------------------------|--------|--------------------------------------------|------------------------------|
| `transacao-bancaria.queue` | Queue  | Processamento de transações bancárias      | `transacao-bancaria.DLQ`     |
| `alerta-fraude.queue`      | Queue  | Alertas de transações suspeitas            | `alerta-fraude.DLQ`          |
| `eventos-conta.topic`      | Topic  | Eventos de ciclo de vida da conta (pub/sub)| `eventos-conta.DLQ` (queue)  |
| `demo-queue`               | Queue  | Exemplo genérico original                  | `demo-queue.DLQ`             |

---

## Política de Retry e Dead Letter Queue

### Como funciona

1. Um consumidor recebe uma mensagem e lança uma `RuntimeException`
2. Como a sessão é transacionada (`sessionTransacted = true`), a mensagem é **revertida** para o broker
3. O ActiveMQ reentrega a mensagem automaticamente com **backoff exponencial**:
   - Tentativa 1: após 1 s
   - Tentativa 2: após 2 s
   - Tentativa 3: após 4 s … até 30 s de intervalo máximo
4. Após **10 tentativas** sem sucesso, o broker move a mensagem para a **DLQ** correspondente
5. O listener da DLQ registra a mensagem morta para auditoria e ação manual

### Configuração (application.yml)

```yaml
banking:
  transaction:
    retry-max-attempts: 10
```

### Configuração (EmbeddedActiveMQConfig.java)

```java
RedeliveryPolicy policy = new RedeliveryPolicy();
policy.setMaximumRedeliveries(10);
policy.setInitialRedeliveryDelay(1_000L);
policy.setBackOffMultiplier(2.0);
policy.setUseExponentialBackOff(true);
policy.setMaximumRedeliveryDelay(30_000L);
```

### DLQ individual por destino

```java
IndividualDeadLetterStrategy dlqStrategy = new IndividualDeadLetterStrategy();
dlqStrategy.setQueueSuffix(".DLQ");   // transacao-bancaria.queue → transacao-bancaria.DLQ
dlqStrategy.setTopicSuffix(".DLQ");   // eventos-conta.topic      → eventos-conta.DLQ
dlqStrategy.setUseQueueForTopicMessages(true);
```

---

## Como Executar

```bash
# Compilar e executar
mvn spring-boot:run

# Apenas testes
mvn test
```

Ao iniciar, o `BankingDemoRunner` envia automaticamente mensagens de demonstração.
Observe o log para ver as tentativas de retry e o encaminhamento para a DLQ.

---

## Observando o Retry nos Logs

Quando a transação de R$ 75.000 é enviada, você verá no console:

```
[TRANSACAO] Tentativa 1/10 | ID=... | Tipo=SAQUE | Valor=75000.00
[TRANSACAO] Valor suspeito detectado. Falha intencional para demonstrar retry.
[TRANSACAO] Tentativa 2/10 | ID=... | Tipo=SAQUE | Valor=75000.00
...
[TRANSACAO] Última tentativa (10) para ID=.... Próximo passo: DLQ.
[DLQ] TRANSAÇÃO MORTA — REQUER INTERVENÇÃO
  ID        : ...
  Valor     : 75000.00
```
