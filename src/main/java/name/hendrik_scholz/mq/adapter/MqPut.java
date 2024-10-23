package name.hendrik_scholz.mq.adapter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqPut {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("put/{queueName}")
    String mqPut(@PathVariable String queueName,
                 @RequestHeader("message-type") String messageType,
                 @RequestHeader("encoding") String encoding,
                 @RequestHeader("ccsid") String ccsid,
                 @RequestHeader("correlation-id") String correlationId,
                 @RequestBody String msg){
        try {
            // TODO: create message depending on message type - text or binary
            jmsTemplate.send(queueName, session -> {
                TextMessage message = session.createTextMessage(msg);

                // Customize the message encoding via MessagePostProcessor
                MessagePostProcessor postProcessor = new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws JMSException {
                        message.setJMSCorrelationID(correlationId);
                        message.setIntProperty("JMS_IBM_Character_Set", Integer.parseInt(ccsid));
                        message.setIntProperty("JMS_IBM_Encoding", Integer.parseInt(encoding));
                        return message;
                    }
                };

                // Apply post processor before sending
                return postProcessor.postProcessMessage(message);
            });

            return "OK";
        } catch(JmsException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }
}
