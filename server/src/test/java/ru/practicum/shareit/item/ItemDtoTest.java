package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerializeAndDeserialize() throws Exception {
        BookingShortDto last = BookingShortDto.builder()
                .id(100L)
                .start(LocalDateTime.of(2025, 6, 2, 0, 0))
                .end(LocalDateTime.of(2025, 6, 4, 0, 0))
                .bookerId(2L)
                .build();

        BookingShortDto next = BookingShortDto.builder()
                .id(101L)
                .start(LocalDateTime.of(2025, 6, 12, 0, 0))
                .end(LocalDateTime.of(2025, 6, 17, 0, 0))
                .bookerId(3L)
                .build();

        CommentDto comment = CommentDto.builder()
                .id(200L)
                .text("test_text")
                .authorName("Anton")
                .created(LocalDateTime.of(2025, 6, 1, 10, 0))
                .build();

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("new_item")
                .description("new_description")
                .available(true)
                .lastBooking(last)
                .nextBooking(next)
                .comments(List.of(comment))
                .build();

        var result = json.write(dto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathMapValue("$.lastBooking");
        assertThat(result).hasJsonPathMapValue("$.nextBooking");
        assertThat(result).hasJsonPathArrayValue("$.comments");

        String content = result.getJson();
        ItemDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isNull();
        assertThat(parsed.getLastBooking()).isNull();
        assertThat(parsed.getNextBooking()).isNull();
        assertThat(parsed.getComments()).isNull();

        assertThat(parsed.getName()).isEqualTo("new_item");
        assertThat(parsed.getDescription()).isEqualTo("new_description");
        assertThat(parsed.getAvailable()).isEqualTo(true);
    }
}