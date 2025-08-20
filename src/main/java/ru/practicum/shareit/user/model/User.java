package ru.practicum.shareit.user.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}