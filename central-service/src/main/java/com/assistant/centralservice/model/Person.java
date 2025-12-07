package com.assistant.centralservice.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "persons")
@Schema(description = "Модель человека")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор", example = "1", readOnly = true)
    @Positive(message = "ID должно быть больше 0")
    private Integer id;

    @Column(name = "name", nullable = false)
    @Schema(description = "Имя человека", example = "Иван Петров", required = true)
    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Embedded
    @Schema(description = "Координаты местоположения", required = true)
    @NotNull(message = "Координаты не могут быть null")
    @Valid
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Дата создания записи", example = "2025-09-26T10:30:00Z", readOnly = true)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date creationDate;

    @Column(name = "height", nullable = false)
    @Schema(description = "Рост в сантиметрах", example = "175.5", required = true)
    @Positive(message = "Рост должен быть больше 0")
    private double height;

    @Column(name = "birthday", nullable = false)
    @Schema(description = "Дата рождения", example = "1990-05-15", required = true)
    @NotNull(message = "Дата рождения не может быть null")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate birthday;

    @Column(name = "weight")
    @Schema(description = "Вес в килограммах", example = "70")
    @Positive(message = "Вес должен быть больше 0")
    private Long weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    @Schema(description = "Национальность", example = "RUSSIA", required = true)
    @NotNull(message = "Национальность не может быть null")
    private Country nationality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y")),
            @AttributeOverride(name = "name", column = @Column(name = "location_name"))
    })
    @Schema(description = "Местоположение", required = true)
    @NotNull(message = "Местоположение не может быть null")
    @Valid
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    @Schema(description = "Цвет волос", example = "BROWN")
    private HairColor hairColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color")
    @Schema(description = "Цвет глаз", example = "BLUE")
    private EyeColor eyeColor;

    public Person() {
        this.creationDate = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }
}