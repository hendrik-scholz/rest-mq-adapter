package name.hendrik_scholz.mq.adapter.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDtoTest {

    @Test
    void errorMessage() {
        ErrorDto errorDto = new ErrorDto("Something went wrong!");
        assertEquals("Something went wrong!", errorDto.errorMessage());
    }
}