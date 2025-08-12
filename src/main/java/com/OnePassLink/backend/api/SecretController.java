package com.OnePassLink.backend.api;

import com.OnePassLink.backend.model.ErrorResponse;
import com.OnePassLink.backend.model.SecretRequest;
import com.OnePassLink.backend.model.SecretResponse;
import com.OnePassLink.backend.service.SecretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/secrets")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Secrets", description = "One-time secret sharing API with zero-knowledge encryption")
public class SecretController {

    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @Operation(
        summary = "Create a new secret",
        description = "Stores an encrypted secret with TTL. The secret is encrypted client-side before sending. " +
                     "Returns a unique ID that can be used once to retrieve the secret.",
        tags = {"Secrets"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Secret created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecretResponse.class),
                examples = @ExampleObject(
                    name = "Success",
                    value = "{\"id\": \"a1b2c3d4e5f6g7h8\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - ciphertext too large or invalid TTL",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"error\": \"VALIDATION_ERROR\", \"message\": \"Ciphertext exceeds maximum size limit\", \"status\": 400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Rate limit exceeded",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping
    public ResponseEntity<SecretResponse> createSecret(
        @Parameter(
            description = "Secret creation request containing encrypted data",
            required = true,
            schema = @Schema(implementation = SecretRequest.class)
        )
        @Valid @RequestBody SecretRequest request) {
        try {
            SecretResponse response = secretService.createSecret(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create secret", e);
        }
    }

    @Operation(
        summary = "Retrieve and delete secret (one-time access)",
        description = "Retrieves the encrypted secret and immediately deletes it from storage. " +
                     "This endpoint can only be called successfully once per secret ID. " +
                     "The returned ciphertext must be decrypted client-side using the key from the URL fragment.",
        tags = {"Secrets"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Secret retrieved successfully (and deleted)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecretResponse.class),
                examples = @ExampleObject(
                    name = "Success",
                    value = "{\"ciphertext\": \"U2FsdGVkX1+vupppZksvRf5pq5g5XjFRIipRkwB0K1Y96Qsv2Lm+31cmzaAILwyt\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "410",
            description = "Secret not found, expired, or already consumed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Gone",
                    value = "{\"error\": \"GONE\", \"message\": \"Secret not found or already consumed\", \"status\": 410}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Rate limit exceeded",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SecretResponse> getSecret(
        @Parameter(
            description = "Unique secret identifier (128+ bits entropy, URL-safe)",
            required = true,
            example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
        )
        @PathVariable String id) {
        Optional<SecretResponse> secret = secretService.getAndDeleteSecret(id);

        if (secret.isPresent()) {
            return ResponseEntity.ok(secret.get());
        } else {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }

    @Operation(
        summary = "Check if secret exists",
        description = "Verifies if a secret exists without retrieving or deleting it. " +
                     "Useful for checking secret availability before showing decrypt UI.",
        tags = {"Secrets"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Secret exists and is available for retrieval"
        ),
        @ApiResponse(
            responseCode = "410",
            description = "Secret not found, expired, or already consumed"
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Rate limit exceeded"
        )
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkSecret(
        @Parameter(
            description = "Unique secret identifier to check",
            required = true,
            example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
        )
        @PathVariable String id) {
        boolean exists = secretService.secretExists(id);

        if (exists) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }

    /**
     * Global exception handler for validation errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An error occurred processing your request",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
