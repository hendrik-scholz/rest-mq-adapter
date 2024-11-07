package name.hendrik_scholz.mq.adapter.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTypeExceptionTest {

    @Test
    void create() {
        MessageTypeException messageTypeException = new MessageTypeException("Wrong message type!");
        assertEquals("Wrong message type!", messageTypeException.getMessage());
    }
}