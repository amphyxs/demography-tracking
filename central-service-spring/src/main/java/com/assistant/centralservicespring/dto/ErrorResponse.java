package com.assistant.centralservicespring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String error;

    private String message;

    private int status;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String path;

    private List<ValidationError> validationErrors;

    private Map<String, Object> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;

        private Object rejectedValue;

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