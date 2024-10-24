package name.hendrik_scholz.mq.adapter;

import com.ibm.mq.constants.MQConstants;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

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
            jmsTemplate.send(queueName, session -> {
                Message message;

                if (messageType.equals(MQConstants.MQFMT_STRING.trim())) {
                    message = session.createTextMessage(msg);
                } else if (messageType.equals(MQConstants.MQFMT_NONE.trim())) {
                    message = session.createBytesMessage();
                    ((BytesMessage)message).writeBytes(msg.getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new RuntimeException("Unknown format");
                }

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
