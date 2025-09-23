package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingShortDto lastBooking;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingShortDto nextBooking;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<CommentDto> comments;
}