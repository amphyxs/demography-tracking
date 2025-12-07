package com.assistant.centralservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Координаты местоположения")
public class Coordinates {

    @Column(name = "coord_x", nullable = false)
    @Schema(description = "Координата X", example = "10.5", required = true)
    @NotNull(message = "Координата X не может быть null")
    private Double x;

    @Column(name = "coord_y", nullable = false)
    @Schema(description = "Координата Y", example = "20.7", required = true)
    private float y;
}