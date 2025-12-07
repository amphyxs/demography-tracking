package com.assistant.centralservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Местоположение")
public class Location {

    @Column(name = "location_x")
    @Schema(description = "Координата X местоположения", example = "55.7558")
    private float x;

    @Column(name = "location_y", nullable = false)
    @Schema(description = "Координата Y местоположения", example = "37", required = true)
    @NotNull(message = "Координата Y не может быть null")
    private Integer y;

    @Column(name = "location_name", nullable = false)
    @Schema(description = "Название места", example = "Москва", required = true)
    @NotNull(message = "Название места не может быть null")
    @NotBlank(message = "Название места не может быть пустым")
    private String name;
}