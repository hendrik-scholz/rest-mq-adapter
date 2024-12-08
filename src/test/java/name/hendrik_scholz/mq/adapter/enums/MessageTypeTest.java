package name.hendrik_scholz.mq.adapter.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static name.hendrik_scholz.mq.adapter.enums.MessageType.BINARY;
import static name.hendrik_scholz.mq.adapter.enums.MessageType.TEXT;
import static org.junit.jupiter.api.Assertions.*;

class MessageTypeTest {

    private static Stream<Arguments> enumString() {
        return Stream.of(
            Arguments.of(BINARY, "binary"),
            Arguments.of(TEXT, "text")
        );
    }

    @ParameterizedTest
    @MethodSource("enumString")
    void valueOfLabel(MessageType messageType, String messageTypeLabel) {
        assertEquals(messageType, MessageType.valueOfLabel(messageTypeLabel));
    }

    @ParameterizedTest
    @MethodSource("enumString")
    void testToString(MessageType messageType, String messageTypeLabel) {
        assertEquals(messageTypeLabel, messageType.toString());
    }
}