package com.example;

import com.example.messaging.MessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessaqeProducerTest {

    @Autowired
    MessageProducer producer;

    @Test
    void testSendMessagesforAll(){
        for( int i = 0; i<= 100000; i++){
            this.producer.sendToQueue("SAMPLE MESSAGE TO QUEUE PROCESSING " + i);
        }


        //this.producer.sendToTopic("SAMPLE MESSAGE TO TOPIC PROCESSING -----------------");

    }
}
