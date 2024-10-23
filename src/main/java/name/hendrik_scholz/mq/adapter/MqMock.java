package name.hendrik_scholz.mq.adapter;

import com.ibm.msg.client.jakarta.jms.JmsMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

@Service
public class MqMock {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${mock.target.queue}")
    private String targetQueue;

    @JmsListener(destination = "${mock.source.queue}")
    public void receiveMessage(JmsMessage jmsMessage) {
        try {
            String messageId = jmsMessage.getJMSMessageID();
            String inputMessage = ((TextMessage)jmsMessage).getText();

            System.out.println("messageId: " + messageId);
            System.out.println("inputMessage: " + inputMessage);

            jmsTemplate.send(targetQueue, session -> {
                String mockResponse = "mock response";
                TextMessage message = session.createTextMessage(mockResponse);

                // Customize the message encoding via MessagePostProcessor
                MessagePostProcessor postProcessor = new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws JMSException {
                        message.setJMSCorrelationID(messageId);
                        return message;
                    }
                };

                // Apply post processor before sending
                return postProcessor.postProcessMessage(message);
            });
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
