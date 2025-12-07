package com.assistant.centralservicespring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "ID должно быть больше 0")
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Embedded
    @NotNull(message = "Координаты не могут быть null")
    @Valid
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date creationDate;

    @Column(name = "height", nullable = false)
    @Positive(message = "Рост должен быть больше 0")
    private double height;

    @Column(name = "birthday", nullable = false)
    @NotNull(message = "Дата рождения не может быть null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(name = "weight")
    @Positive(message = "Вес должен быть больше 0")
    private Long weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    @NotNull(message = "Национальность не может быть null")
    private Country nationality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y")),
            @AttributeOverride(name = "name", column = @Column(name = "location_name"))
    })
    @NotNull(message = "Местоположение не может быть null")
    @Valid
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    private HairColor hairColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color")
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