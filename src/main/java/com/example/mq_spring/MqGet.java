package com.example.mq_spring;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqGet {

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("get")
    ResponseEntity<String> mqGet(@RequestHeader("queue-name") String queueName){
        try {
            Message message = jmsTemplate.receive(queueName);

            try {
                return ResponseEntity.ok()
                    .header("correlation-id", message.getJMSCorrelationID())
                    .header("ccsid", String.valueOf(message.getStringProperty("JMS_IBM_Character_Set")))
                    .header("encoding", String.valueOf(message.getIntProperty("JMS_IBM_Encoding")))
                    .body(message.getBody(String.class));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } catch(JmsException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("FAIL");
        }
    }
}
