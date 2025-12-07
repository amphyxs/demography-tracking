package com.assistant.centralservicespring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Coordinates {

    @Column(name = "coord_x", nullable = false)
    @NotNull(message = "Координата X не может быть null")
    private Double x;

    @Column(name = "coord_y", nullable = false)
    private float y;
}