package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(10L)
                .text("text_test")
                .authorName("Anton")
                .created(LocalDateTime.of(2025, 5, 4, 10, 0))
                .build();

        var result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text_test");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Anton");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-05-04T10:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\": 10,"
                + "\"text\": \"text_test\","
                + "\"authorName\": \"Anton\","
                + "\"created\": \"2025-05-04T12:30:00\""
                + "}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getText()).isEqualTo("text_test");
        assertThat(dto.getAuthorName()).isNull();
        assertThat(dto.getCreated()).isNull();
    }
}