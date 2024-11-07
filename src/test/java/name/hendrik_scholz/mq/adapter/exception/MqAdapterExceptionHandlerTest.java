package name.hendrik_scholz.mq.adapter.exception;

import jakarta.jms.JMSException;
import name.hendrik_scholz.mq.adapter.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class MqAdapterExceptionHandlerTest {

    @Test
    void handleException() {
        MqAdapterExceptionHandler mqAdapterExceptionHandler = new MqAdapterExceptionHandler();
        ResponseEntity<ErrorDto> responseEntity = mqAdapterExceptionHandler.handleException(new JMSException("Something went wrong!", "0000"));
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Something went wrong!", Objects.requireNonNull(responseEntity.getBody()).errorMessage());
    }

    @Test
    void handleMessageFormatException() {
        MqAdapterExceptionHandler mqAdapterExceptionHandler = new MqAdapterExceptionHandler();
        ResponseEntity<ErrorDto> responseEntity = mqAdapterExceptionHandler.handleMessageFormatException(new MessageTypeException("Wrong message type!"));
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Wrong message type!", Objects.requireNonNull(responseEntity.getBody()).errorMessage());
    }
}