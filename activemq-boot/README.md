
# ActiveMQ Advanced Example (Queues + Topics)

## Requirements
- Java 21
- Maven
- Apache ActiveMQ running locally (port 61616)

## Run ActiveMQ
Download and run:
```
bin/activemq start
```

## Run Application
```
mvn spring-boot:run
```

## Test Messaging
You can trigger messages via tests or extend with REST.

## Concepts Covered
- Queue (point-to-point)
- Topic (publish-subscribe)
- JMS with Spring Boot
