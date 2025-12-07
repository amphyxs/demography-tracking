package com.assistant.centralservice.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Цвет глаз")
public enum EyeColor {
    @Schema(description = "Карие")
    BROWN,

    @Schema(description = "Голубые")
    BLUE,

    @Schema(description = "Зеленые")
    GREEN,

    @Schema(description = "Серые")
    GRAY,

    @Schema(description = "Черные")
    BLACK,

    @Schema(description = "Янтарные")
    AMBER,

    @Schema(description = "Ореховые")
    HAZEL
}