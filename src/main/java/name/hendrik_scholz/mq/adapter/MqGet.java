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
import static java.nio.charset.StandardCharsets.UTF_8;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CCSID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CORRELATION_ID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.ENCODING;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.MESSAGE_TYPE;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.BINARY;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.TEXT;

@RestController
public class MqGet {

    private JmsTemplate jmsTemplate;

    @Autowired
    public MqGet(JmsTemplate jmsTemplate, @Value("${mq.receive.timeoutInMs}") long timeoutInMs) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setReceiveTimeout(timeoutInMs);
    }

    @GetMapping("get/{queueName}")
    ResponseEntity<String> mqGet(@PathVariable String queueName){
        try {
            Message message = this.jmsTemplate.receive(queueName);

            if (message == null) {
                return ResponseEntity.noContent().build();
            }

            String messageType = "n/a";
            String payload = "";

            if (message instanceof BytesMessage) {
                messageType = BINARY.toString();
                int length = (int)((BytesMessage) message).getBodyLength();
                byte [] content = new byte[length];
                ((BytesMessage) message).readBytes(content, length);
                payload = new String(content, UTF_8);
            }

            if (message instanceof TextMessage) {
                messageType = TEXT.toString();
                payload = message.getBody(String.class);
            }

            try {
                return ResponseEntity.ok()
                    .header(CORRELATION_ID.toString(), message.getJMSCorrelationID())
                    .header(CCSID.toString(), String.valueOf(message.getStringProperty(JMS_IBM_CHARACTER_SET)))
                    .header(ENCODING.toString(), String.valueOf(message.getIntProperty(JMS_IBM_ENCODING)))
                    .header(MESSAGE_TYPE.toString(), messageType)
                    .body(payload);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } catch(JmsException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
