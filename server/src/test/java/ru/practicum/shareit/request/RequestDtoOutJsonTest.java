package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoOutJsonTest {

    @Autowired
    private JacksonTester<RequestDtoOut> jacksonTester;

    private final LocalDateTime created = LocalDateTime.of(2099, 1, 1, 1, 1, 1);

    @Test
    void bookingDtoOut_startAndEndTest() throws Exception {
        RequestDtoOut requestDtoOut = RequestDtoOut.builder()
                .id(1L)
                .description("test_description")
                .created(created)
                .build();
        JsonContent<RequestDtoOut> jsonContent = jacksonTester.write(requestDtoOut);
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.toString());
    }
}
