package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_ENCODING;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CCSID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CORRELATION_ID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.ENCODING;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.MESSAGE_TYPE;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.BINARY;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.TEXT;

@RestController
public class MqGet {

    private final JmsTemplate jmsTemplate;

    private final MessageConverter messageConverter;

    @Autowired
    public MqGet(JmsTemplate jmsTemplate,
                 MessageConverter messageConverter,
                 @Value("${mq.receive.timeoutInMs}") long timeoutInMs) {
        this.jmsTemplate = jmsTemplate;
        this.messageConverter = messageConverter;
        this.jmsTemplate.setReceiveTimeout(timeoutInMs);
    }

    @GetMapping("get/{queueName}")
    ResponseEntity<String> mqGet(@PathVariable String queueName) {
        try {
            Message message = this.jmsTemplate.receive(queueName);

            if (message == null) {
                return ResponseEntity.noContent().build();
            }

            String messageType = getMessageType(message);
            String payload = messageConverter.getMessage(message);

            return ResponseEntity.ok()
                .header(CORRELATION_ID.toString(), message.getJMSCorrelationID())
                .header(CCSID.toString(), String.valueOf(message.getStringProperty(JMS_IBM_CHARACTER_SET)))
                .header(ENCODING.toString(), String.valueOf(message.getIntProperty(JMS_IBM_ENCODING)))
                .header(MESSAGE_TYPE.toString(), messageType)
                .body(payload);
        } catch(JmsException | JMSException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getMessageType(Message message) {
        String messageType = "n/a";

        if (message instanceof BytesMessage) {
            messageType = BINARY.toString();
        }

        if (message instanceof TextMessage) {
            messageType = TEXT.toString();
        }

        return messageType;
    }
}
