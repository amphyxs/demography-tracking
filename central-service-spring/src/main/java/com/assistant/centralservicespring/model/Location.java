package com.assistant.centralservicespring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Location {

    @Column(name = "location_x")
    private float x;

    @Column(name = "location_y", nullable = false)
    @NotNull(message = "Координата Y не может быть null")
    private Integer y;

    @Column(name = "location_name", nullable = false)
    @NotNull(message = "Название места не может быть null")
    @NotBlank(message = "Название места не может быть пустым")
    private String name;
}