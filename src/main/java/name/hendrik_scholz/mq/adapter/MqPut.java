package name.hendrik_scholz.mq.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqPut {

    private final CustomMessageCreator customMessageCreator;

    private final JmsTemplate jmsTemplate;

    public MqPut(CustomMessageCreator customMessageCreator, JmsTemplate jmsTemplate) {
        this.customMessageCreator = customMessageCreator;
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("put/{queueName}")
    ResponseEntity<Void> mqPut(@PathVariable String queueName,
                         @RequestHeader("message-type") String messageTypeLabel,
                         @RequestHeader("encoding") String encoding,
                         @RequestHeader("ccsid") String ccsid,
                         @RequestHeader("correlation-id") String correlationId,
                         @RequestBody String msg) {
        customMessageCreator.setCcsid(ccsid);
        customMessageCreator.setCorrelationId(correlationId);
        customMessageCreator.setEncoding(encoding);
        customMessageCreator.setMessageTypeLabel(messageTypeLabel);
        customMessageCreator.setMessage(msg);
        jmsTemplate.send(queueName, customMessageCreator);

        return ResponseEntity.accepted().build();
    }
}
