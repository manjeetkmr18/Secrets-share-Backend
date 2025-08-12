package com.OnePassLink.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
    description = "Request to create a new secret",
    example = """
        {
            "ciphertext": "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRIipRkwB0K1Y96Qsv2Lm+31cmzaAILwyt",
            "expiresInSec": 3600
        }
        """
)
public class SecretRequest {

    @Schema(
        description = "AES-GCM encrypted secret (base64 encoded). Must be encrypted client-side before sending.",
        example = "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRIipRkwB0K1Y96Qsv2Lm+31cmzaAILwyt",
        required = true,
        maxLength = 100000
    )
    @NotBlank
    @Size(max = 100_000)
    private String ciphertext;

    @Schema(
        description = "Time-to-live in seconds (1 minute to 7 days)",
        example = "3600",
        required = true,
        minimum = "60",
        maximum = "604800"
    )
    @Min(60)
    @Max(604_800)
    private long expiresInSec;

    public SecretRequest() {}

    public SecretRequest(String ciphertext, long expiresInSec) {
        this.ciphertext = ciphertext;
        this.expiresInSec = expiresInSec;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public long getExpiresInSec() {
        return expiresInSec;
    }

    public void setExpiresInSec(long expiresInSec) {
        this.expiresInSec = expiresInSec;
    }

}
