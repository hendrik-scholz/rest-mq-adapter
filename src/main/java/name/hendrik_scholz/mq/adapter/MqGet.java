package name.hendrik_scholz.mq.adapter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqGet {

    private JmsTemplate jmsTemplate;

    @Autowired
    public MqGet(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setReceiveTimeout(5000);
    }

    @GetMapping("get/{queueName}")
    ResponseEntity<String> mqGet(@PathVariable String queueName){
        try {
            Message message = this.jmsTemplate.receive(queueName);

            if (message == null) {
                return ResponseEntity.noContent().build();
            }

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
