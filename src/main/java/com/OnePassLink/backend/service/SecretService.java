package com.OnePassLink.backend.service;

import com.OnePassLink.backend.model.Secret;
import com.OnePassLink.backend.model.SecretRequest;
import com.OnePassLink.backend.model.SecretResponse;
import com.OnePassLink.backend.repository.SecretRepository;
import com.OnePassLink.backend.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class SecretService {

    private final SecretRepository secretRepository;
    private final IdGenerator idGenerator;

    public SecretService(SecretRepository secretRepository, IdGenerator idGenerator) {
        this.secretRepository = secretRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * Creates a new secret with the given ciphertext and TTL
     * @param request Contains ciphertext and expiresInSec
     * @return SecretResponse with the generated ID
     */
    public SecretResponse createSecret(SecretRequest request) {
        String id = idGenerator.generateId();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(request.getExpiresInSec());

        Secret secret = new Secret(
            id,
            request.getCiphertext(),
            now,
            expiresAt,
            1, // maxViews - default to 1 for one-time secrets
            0  // views - starts at 0
        );

        secretRepository.save(secret);
        return new SecretResponse(id);
    }

    /**
     * Retrieves and deletes a secret atomically (one-time access)
     * @param id The secret ID
     * @return SecretResponse with ciphertext if found, empty if not found or expired
     */
    public Optional<SecretResponse> getAndDeleteSecret(String id) {
        Optional<Secret> secretOpt = secretRepository.findAndDelete(id);

        if (secretOpt.isPresent()) {
            Secret secret = secretOpt.get();

            // Check if secret has expired (redundant with Redis TTL, but good practice)
            if (secret.getExpiresAt().isBefore(Instant.now())) {
                return Optional.empty();
            }

            // Check if already viewed max times (future-proofing)
            if (secret.getViews() >= secret.getMaxViews()) {
                return Optional.empty();
            }

            return Optional.of(new SecretResponse(secret.getId(), secret.getCiphertext()));
        }

        return Optional.empty();
    }

    /**
     * Checks if a secret exists without retrieving it
     * @param id The secret ID
     * @return true if exists, false otherwise
     */
    public boolean secretExists(String id) {
        return secretRepository.exists(id);
    }
}
