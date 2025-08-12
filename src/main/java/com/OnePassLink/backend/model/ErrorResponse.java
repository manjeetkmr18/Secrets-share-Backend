package com.OnePassLink.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(
    description = "Error response structure",
    example = """
        {
            "error": "VALIDATION_ERROR",
            "message": "Ciphertext exceeds maximum size limit",
            "status": 400,
            "timestamp": "2025-08-11T10:30:45.123Z"
        }
        """
)
public class ErrorResponse {

    @Schema(
        description = "Error code identifying the type of error",
        example = "VALIDATION_ERROR"
    )
    private String error;

    @Schema(
        description = "Human-readable error message",
        example = "Ciphertext exceeds maximum size limit"
    )
    private String message;

    @Schema(
        description = "HTTP status code",
        example = "400"
    )
    private int status;

    @Schema(
        description = "Timestamp when the error occurred",
        example = "2025-08-11T10:30:45.123Z"
    )
    private Instant timestamp;

    public ErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
