package name.hendrik_scholz.mq.adapter.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CCSID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.CORRELATION_ID;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.ENCODING;
import static name.hendrik_scholz.mq.adapter.enums.HeaderName.MESSAGE_TYPE;
import static org.junit.jupiter.api.Assertions.*;

class HeaderNameTest {

    private static Stream<Arguments> enumString() {
        return Stream.of(
            Arguments.of(CCSID, "ccsid"),
            Arguments.of(CORRELATION_ID, "correlation-id"),
            Arguments.of(ENCODING, "encoding"),
            Arguments.of(MESSAGE_TYPE, "message-type")
        );
    }

    @ParameterizedTest
    @MethodSource("enumString")
    void valueOfLabel(HeaderName headerName, String headerNameLabel) {
        assertEquals(headerName, HeaderName.valueOfLabel(headerNameLabel));
    }

    @ParameterizedTest
    @MethodSource("enumString")
    void testToString(HeaderName headerName, String headerNameLabel) {
        assertEquals(headerNameLabel, headerName.toString());
    }
}