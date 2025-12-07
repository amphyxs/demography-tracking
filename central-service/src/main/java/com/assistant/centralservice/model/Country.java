package com.assistant.centralservice.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Национальность")
public enum Country {
    @Schema(description = "Россия")
    RUSSIA,

    @Schema(description = "Китай")
    CHINA,

    @Schema(description = "Индия")
    INDIA,

    @Schema(description = "Италия")
    ITALY,

    @Schema(description = "Южная Корея")
    SOUTH_KOREA
}