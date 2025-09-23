package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static Booking toEntity(BookingInputDto req, Item item, User booker) {
        if (req == null) return null;
        return Booking.builder()
                .start(req.getStart())
                .end(req.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto toDto(Booking b) {
        if (b == null) return null;
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .booker(UserMapper.toShortDto(b.getBooker()))
                .item(ItemMapper.toShortDto(b.getItem()))
                .build();
    }

    public static BookingShortDto toShortDto(Booking b) {
        if (b == null) return null;
        return BookingShortDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .bookerId(b.getBooker().getId())
                .build();
    }
}