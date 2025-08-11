package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;

    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    int duration;

    @NotNull
    Mpa mpa;
    Set<Genre> genres = new HashSet<>();
    Set<Long> likes = new HashSet<>();

    public boolean isValidReleaseDate() {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}
