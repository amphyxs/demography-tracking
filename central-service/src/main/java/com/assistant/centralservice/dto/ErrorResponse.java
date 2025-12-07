package com.assistant.centralservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Стандартная структура ответа об ошибке")
public class ErrorResponse {

    @Schema(description = "Краткое описание ошибки", example = "Ошибка валидации")
    private String error;

    @Schema(description = "Детальное описание ошибки", example = "Поле 'name' не может быть пустым")
    private String message;

    @Schema(description = "HTTP статус код", example = "400")
    private int status;

    @Schema(description = "Временная метка возникновения ошибки")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Путь запроса, где произошла ошибка", example = "/api/persons")
    private String path;

    @Schema(description = "Список ошибок валидации")
    private List<ValidationError> validationErrors;

    @Schema(description = "Дополнительные данные об ошибке")
    private Map<String, Object> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Детали ошибки валидации")
    public static class ValidationError {
        @Schema(description = "Поле, в котором произошла ошибка", example = "name")
        private String field;

        @Schema(description = "Отклоненное значение", example = "null")
        private Object rejectedValue;

        @Schema(description = "Сообщение об ошибке", example = "Имя не может быть пустым")
        private String message;
    }

    public static ErrorResponse badRequest(String message) {
        return ErrorResponse.builder()
                .error("Bad Request")
                .message(message)
                .status(400)
                .build();
    }

    public static ErrorResponse notFound(String message) {
        return ErrorResponse.builder()
                .error("Not Found")
                .message(message)
                .status(404)
                .build();
    }

    public static ErrorResponse validationError(String message, List<ValidationError> errors) {
        return ErrorResponse.builder()
                .error("Validation Error")
                .message(message)
                .status(422)
                .validationErrors(errors)
                .build();
    }

    public static ErrorResponse internalError(String message) {
        return ErrorResponse.builder()
                .error("Internal Server Error")
                .message(message)
                .status(500)
                .build();
    }
}