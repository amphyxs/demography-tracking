package com.assistant.centralservice.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Цвет волос")
public enum HairColor {
    @Schema(description = "Блондин")
    BLONDE,

    @Schema(description = "Брюнет")
    BRUNETTE,

    @Schema(description = "Шатен")
    BROWN,

    @Schema(description = "Рыжий")
    RED,

    @Schema(description = "Черный")
    BLACK,

    @Schema(description = "Серый")
    GRAY,

    @Schema(description = "Белый")
    WHITE
}