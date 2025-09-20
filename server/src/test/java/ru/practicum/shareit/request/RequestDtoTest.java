package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {


    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerializeRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 5, 4, 10, 30);

        ItemShortDto item = new ItemShortDto();
        item.setId(10L);
        item.setName("ru/practicum/shareit/item");

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(100L)
                .description("description")
                .created(created)
                .items(List.of(item))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-05-04T10:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
    }

    @Test
    void testDeserializeRequestDto() throws Exception {
        String jsonContent =
                "{\n" +
                        "  \"id\": 100,\n" +
                        "  \"description\": \"description\",\n" +
                        "  \"created\": \"2025-05-04T10:30:00\",\n" +
                        "  \"items\": [\n" +
                        "    {\n" +
                        "      \"id\": 10,\n" +
                        "      \"name\": \"item\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        ItemRequestDto dto = json.parseObject(jsonContent);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getCreated()).isNull();
        assertThat(dto.getItems()).isNull();
    }
}