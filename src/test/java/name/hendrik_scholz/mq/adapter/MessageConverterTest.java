package name.hendrik_scholz.mq.adapter;

import jakarta.jms.BytesMessage;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import name.hendrik_scholz.mq.adapter.exception.UnsupportedMessageTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class MessageConverterTest {

    private static Stream<Arguments> unsupportedMessageTypes() {
        return Stream.of(
            Arguments.of(Mockito.mock(MapMessage.class)),
            Arguments.of(Mockito.mock(ObjectMessage.class)),
            Arguments.of(Mockito.mock(StreamMessage.class))
        );
    }

    @Test
    void getMessageWhenBytesMessageThenString() throws JMSException {
        MessageConverter messageConverter = new MessageConverter();
        BytesMessage bytesMessageMock = Mockito.mock(BytesMessage.class);
        when(bytesMessageMock.getBodyLength()).thenReturn(7L);
        when(bytesMessageMock.readBytes(any(), anyInt())).then((Answer<Integer>) invocation -> {
            byte[] argument = invocation.getArgument(0);
            System.arraycopy("message".getBytes(UTF_8), 0, argument, 0, 7);
            return 7;
        });

        assertEquals("message", messageConverter.getMessage(bytesMessageMock));
    }

    @Test
    void getMessageWhenTextMessageThenString() throws JMSException {
        MessageConverter messageConverter = new MessageConverter();
        TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        when(textMessageMock.getBody(any())).thenReturn("message");
        assertEquals("message", messageConverter.getMessage(textMessageMock));
    }

    @ParameterizedTest
    @MethodSource("unsupportedMessageTypes")
    void getMessageWhenUnsupportedMessageTypesThenException(Message message) throws JMSException {
        MessageConverter messageConverter = new MessageConverter();
        UnsupportedMessageTypeException unsupportedMessageTypeException = assertThrows(UnsupportedMessageTypeException.class, () -> messageConverter.getMessage(message));
        assertEquals("Message type is not supported by converter.", unsupportedMessageTypeException.getMessage());
    }
}