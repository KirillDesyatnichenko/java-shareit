package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Kesha")
                .email("Kesha_lol@mail.ru")
                .build();

        assertThat(json.write(userDto)).hasJsonPathNumberValue("$.id");
        assertThat(json.write(userDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(userDto)).extractingJsonPathStringValue("$.name").isEqualTo("Kesha");
        assertThat(json.write(userDto)).extractingJsonPathStringValue("$.email").isEqualTo("Kesha_lol@mail.ru");
    }

    @Test
    void testDeserializeUser() throws Exception {
        String jsonContent = "{"
                + "\"id\": 1,"
                + "\"name\": \"Kesha\","
                + "\"email\": \"Kesha_lol@mail.ru\""
                + "}";

        UserDto userDto = json.parseObject(jsonContent);

        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isEqualTo("Kesha");
        assertThat(userDto.getEmail()).isEqualTo("Kesha_lol@mail.ru");
    }
}