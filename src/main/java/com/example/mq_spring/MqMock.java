package com.example.mq_spring;

import com.ibm.msg.client.jakarta.jms.JmsMessage;
import jakarta.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MqMock {

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "DEV.QUEUE.3")
    public void receiveMessage(JmsMessage jmsMessage) {
        try {
            String correlationId = jmsMessage.getJMSCorrelationID();
            // TODO: set correlationId in mock response
            jmsTemplate.convertAndSend("DEV.QUEUE.2", "copy of " + jmsMessage.getBody(String.class));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
