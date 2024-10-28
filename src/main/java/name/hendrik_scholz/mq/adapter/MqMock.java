package name.hendrik_scholz.mq.adapter;

import com.ibm.msg.client.jakarta.jms.JmsMessage;
import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MqMock {

    private static final Logger log = LoggerFactory.getLogger(MqMock.class);

    private final CustomMessageCreator customMessageCreator;

    private final JmsTemplate jmsTemplate;

    private final MessageConverter messageConverter;

    private final String ccsid;

    private final String encoding;

    private final String messageType;

    private final String responseFile;

    private final String targetQueue;

    public MqMock(CustomMessageCreator customMessageCreator,
                  JmsTemplate jmsTemplate,
                  MessageConverter messageConverter,
                  @Value("${mock.response.ccsid}") String ccsid,
                  @Value("${mock.response.encoding}") String encoding,
                  @Value("${mock.response.file}") String responseFile,
                  @Value("${mock.response.messageType}") String messageType,
                  @Value("${mock.target.queue}") String targetQueue) {
        this.customMessageCreator = customMessageCreator;
        this.jmsTemplate = jmsTemplate;
        this.messageConverter = messageConverter;
        this.ccsid = ccsid;
        this.encoding = encoding;
        this.messageType = messageType;
        this.responseFile = responseFile;
        this.targetQueue = targetQueue;
    }

    @JmsListener(destination = "${mock.source.queue}")
    public void receiveMessage(JmsMessage jmsMessage) {
        try (BufferedReader br = new BufferedReader(new FileReader(responseFile))) {
            String mockResponse = br.lines().collect(Collectors.joining());
            String messageId = jmsMessage.getJMSMessageID();

            String payload = messageConverter.getMessage(jmsMessage);

            log.info("Mock input message: >>>{}<<<", payload);

            customMessageCreator.setCcsid(ccsid);
            customMessageCreator.setCorrelationId(messageId);
            customMessageCreator.setEncoding(encoding);
            customMessageCreator.setMessageTypeLabel(messageType);
            customMessageCreator.setMessage(mockResponse);

            jmsTemplate.send(targetQueue, customMessageCreator);
        } catch (IOException | JMSException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
