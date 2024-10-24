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

import java.nio.charset.StandardCharsets;

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
                messageType = "binary";
                int length = (int)((BytesMessage) message).getBodyLength();
                byte [] content = new byte[length];
                ((BytesMessage) message).readBytes(content, length);
                payload = new String(content, StandardCharsets.UTF_8);
            }

            if (message instanceof TextMessage) {
                messageType = "text";
                payload = message.getBody(String.class);
            }

            try {
                return ResponseEntity.ok()
                    .header("correlation-id", message.getJMSCorrelationID())
                    .header("ccsid", String.valueOf(message.getStringProperty("JMS_IBM_Character_Set")))
                    .header("encoding", String.valueOf(message.getIntProperty("JMS_IBM_Encoding")))
                    .header("message-type", messageType)
                    .body(payload);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } catch(JmsException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("FAIL");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
