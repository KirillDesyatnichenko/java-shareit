package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;

class BookingMapperTest {

    @Test
    void toEntity_withNull_shouldReturnNull() {
        assertNull(BookingMapper.toEntity(null, null, null));
    }

    @Test
    void toDto_withNull_shouldReturnNull() {
        assertNull(BookingMapper.toDto(null));
    }

    @Test
    void toShortDto_withNull_shouldReturnNull() {
        assertNull(BookingMapper.toShortDto(null));
    }
}