package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import name.hendrik_scholz.mq.adapter.enums.MessageType;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_ENCODING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.BINARY;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.TEXT;

@Service
public class CustomMessageCreator implements MessageCreator {

    private String ccsid;
    private String correlationId;
    private String encoding;
    private String messageTypeLabel;
    private String msg;

    public void setCcsid(String ccsid) {
        this.ccsid = ccsid;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setMessageTypeLabel(String messageTypeLabel) {
        this.messageTypeLabel = messageTypeLabel;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
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

        MessagePostProcessor postProcessor = msg -> {
            msg.setJMSCorrelationID(correlationId);
            msg.setIntProperty(JMS_IBM_CHARACTER_SET, Integer.parseInt(ccsid));
            msg.setIntProperty(JMS_IBM_ENCODING, Integer.parseInt(encoding));
            return msg;
        };

        return postProcessor.postProcessMessage(message);
    }
}
