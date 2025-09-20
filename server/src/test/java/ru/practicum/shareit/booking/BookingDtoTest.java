package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeBookingDto_shouldContainExpectedFields() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 5, 4, 10, 0))
                .end(LocalDateTime.of(2025, 5, 4, 12, 0))
                .itemId(100L)
                .bookerId(200L)
                .status(BookingStatus.APPROVED)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-05-04T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-05-04T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(100);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(200);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void deserializeBookingDto_shouldMapFields() throws Exception {
        String content = "{"
                + "\"id\":1,"
                + "\"start\":\"2025-05-04T10:00:00\","
                + "\"end\":\"2025-05-04T12:00:00\","
                + "\"itemId\":100,"
                + "\"bookerId\":200,"
                + "\"status\":\"APPROVED\""
                + "}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 5, 4, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 5, 4, 12, 0));
        assertThat(dto.getItemId()).isEqualTo(100L);
        assertThat(dto.getBookerId()).isEqualTo(200L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}