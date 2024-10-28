package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import name.hendrik_scholz.mq.adapter.enums.MessageType;
import name.hendrik_scholz.mq.adapter.exception.MessageTypeException;
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
    private String message;

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

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        Message messageToEnqueue;
        MessageType messageType = MessageType.valueOfLabel(messageTypeLabel);

        if (messageType == TEXT) {
            messageToEnqueue = session.createTextMessage(this.message);
        } else if (messageType == BINARY) {
            messageToEnqueue = session.createBytesMessage();
            ((BytesMessage)messageToEnqueue).writeBytes(this.message.getBytes(UTF_8));
        } else {
            throw new MessageTypeException(String.format("Invalid message type '%s'. Valid types are '%s' or '%s'.",
                messageTypeLabel, BINARY, TEXT));
        }

        MessagePostProcessor postProcessor = msg -> {
            msg.setJMSCorrelationID(correlationId);
            msg.setIntProperty(JMS_IBM_CHARACTER_SET, Integer.parseInt(ccsid));
            msg.setIntProperty(JMS_IBM_ENCODING, Integer.parseInt(encoding));
            return msg;
        };

        return postProcessor.postProcessMessage(messageToEnqueue);
    }
}
