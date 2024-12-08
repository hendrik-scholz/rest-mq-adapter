package name.hendrik_scholz.mq.adapter.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsupportedMessageTypeExceptionTest {

    @Test
    void create() {
        UnsupportedMessageTypeException unsupportedMessageTypeException = new UnsupportedMessageTypeException("Unsupported message type!");
        assertEquals("Unsupported message type!", unsupportedMessageTypeException.getMessage());
    }
}