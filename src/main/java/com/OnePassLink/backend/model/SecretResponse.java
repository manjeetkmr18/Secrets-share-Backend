package com.OnePassLink.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing secret data or ID")
public class SecretResponse {

    @Schema(
        description = "Unique secret identifier (returned when creating secrets)",
        example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
        nullable = true
    )
    private String id;

    @Schema(
        description = "AES-GCM encrypted secret data (returned when retrieving secrets)",
        example = "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRIipRkwB0K1Y96Qsv2Lm+31cmzaAILwyt",
        nullable = true
    )
    private String ciphertext;

    public SecretResponse() {}

    public SecretResponse(String id) {
        this.id = id;
    }

    public SecretResponse(String id, String ciphertext) {
        this.id = id;
        this.ciphertext = ciphertext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}
