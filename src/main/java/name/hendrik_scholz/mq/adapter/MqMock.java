package name.hendrik_scholz.mq.adapter;

import com.ibm.msg.client.jakarta.jms.JmsMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MqMock {

    private static final Logger log = LoggerFactory.getLogger(MqMock.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${mock.target.queue}")
    private String targetQueue;

    @Value("${mock.response.file}")
    private String responseFile;

    @JmsListener(destination = "${mock.source.queue}")
    public void receiveMessage(JmsMessage jmsMessage) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(responseFile));

            String mockResponse = br.lines().collect(Collectors.joining());

            String messageId = jmsMessage.getJMSMessageID();
            String inputMessage = ((TextMessage)jmsMessage).getText();

            log.info("Mock input message: >>>{}<<<", inputMessage);

            jmsTemplate.send(targetQueue, session -> {
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
