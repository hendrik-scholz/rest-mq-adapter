package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import name.hendrik_scholz.mq.adapter.exception.UnsupportedMessageTypeException;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MessageConverter {

    public String getMessage(Message jmsMessage) throws JMSException {
        if (jmsMessage instanceof BytesMessage) {
            int length = (int) ((BytesMessage) jmsMessage).getBodyLength();
            byte[] content = new byte[length];
            ((BytesMessage) jmsMessage).readBytes(content, length);
            return new String(content, UTF_8);
        }

        if (jmsMessage instanceof TextMessage) {
            return jmsMessage.getBody(String.class);
        }

        throw new UnsupportedMessageTypeException("Message type is not supported by converter.");
    }
}
