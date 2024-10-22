package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import name.hendrik_scholz.mq.adapter.enums.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_ENCODING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.BINARY;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.TEXT;

@RestController
public class MqPut {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("put/{queueName}")
    ResponseEntity<Void> mqPut(@PathVariable String queueName,
                         @RequestHeader("message-type") String messageTypeLabel,
                         @RequestHeader("encoding") String encoding,
                         @RequestHeader("ccsid") String ccsid,
                         @RequestHeader("correlation-id") String correlationId,
                         @RequestBody String msg) {
        try {
            jmsTemplate.send(queueName, session -> {
                Message message;
                MessageType messageType = MessageType.valueOfLabel(messageTypeLabel);

                if (messageType == TEXT) {
                    message = session.createTextMessage(msg);
                } else if (messageType == BINARY) {
                    message = session.createBytesMessage();
                    ((BytesMessage)message).writeBytes(msg.getBytes(UTF_8));
                } else {
                    throw new RuntimeException("Unknown format");
                }

                // Customize the message encoding via MessagePostProcessor
                MessagePostProcessor postProcessor = new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws JMSException {
                        message.setJMSCorrelationID(correlationId);
                        message.setIntProperty(JMS_IBM_CHARACTER_SET, Integer.parseInt(ccsid));
                        message.setIntProperty(JMS_IBM_ENCODING, Integer.parseInt(encoding));
                        return message;
                    }
                };

                // Apply post processor before sending
                return postProcessor.postProcessMessage(message);
            });

            return ResponseEntity.accepted().build();
        } catch(JmsException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
