package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MessageConverter {

    public String getMessage(Message jmsMessage) {
        try {
            String payload = "";

            if (jmsMessage instanceof BytesMessage) {
                int length = (int) ((BytesMessage) jmsMessage).getBodyLength();
                byte[] content = new byte[length];
                ((BytesMessage) jmsMessage).readBytes(content, length);
                payload = new String(content, UTF_8);
            }

            if (jmsMessage instanceof TextMessage) {
                payload = jmsMessage.getBody(String.class);
            }

            return payload;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
