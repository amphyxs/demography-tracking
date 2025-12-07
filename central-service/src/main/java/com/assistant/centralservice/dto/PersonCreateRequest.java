package com.assistant.centralservice.dto;

import com.assistant.centralservice.model.Coordinates;
import com.assistant.centralservice.model.Country;
import com.assistant.centralservice.model.Location;
import com.assistant.centralservice.model.HairColor;
import com.assistant.centralservice.model.EyeColor;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;

@Setter
@Getter
@Schema(description = "Запрос на создание/обновление человека")
public class PersonCreateRequest {

    @Schema(description = "Имя человека", example = "Иван Петров", required = true)
    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Schema(description = "Координаты местоположения", required = true)
    @NotNull(message = "Координаты не могут быть null")
    @Valid
    private Coordinates coordinates;

    @Schema(description = "Рост в сантиметрах", example = "175.5", required = true)
    @Positive(message = "Рост должен быть больше 0")
    private double height;

    @Schema(description = "Дата рождения", example = "1990-05-15", required = true)
    @NotNull(message = "Дата рождения не может быть null")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "Вес в килограммах", example = "70")
    @Positive(message = "Вес должен быть больше 0")
    private Long weight;

    @Schema(description = "Национальность", example = "RUSSIA", required = true)
    @NotNull(message = "Национальность не может быть null")
    private Country nationality;

    @Schema(description = "Местоположение", required = true)
    @NotNull(message = "Местоположение не может быть null")
    @Valid
    private Location location;

    @Schema(description = "Цвет волос", example = "BROWN")
    private HairColor hairColor;

    @Schema(description = "Цвет глаз", example = "BLUE")
    private EyeColor eyeColor;
}