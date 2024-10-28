package name.hendrik_scholz.mq.adapter.exception;

import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import name.hendrik_scholz.mq.adapter.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class MqAdapterExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MqAdapterExceptionHandler.class);

    @ExceptionHandler(value = { JmsException.class, JMSException.class })
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.internalServerError()
            .body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(value = { MessageTypeException.class })
    public  ResponseEntity<ErrorDto> handleMessageFormatException(MessageTypeException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest()
            .body(new ErrorDto(exception.getMessage()));
    }
}
