package com.OnePassLink.backend.model;

import java.time.Instant;

public class Secret {
    private String id;
    private String ciphertext;
    private Instant createdAt;
    private Instant expiresAt;
    private int maxViews = 1;
    private int views = 0;

    public Secret() {}

    public Secret(String id, String ciphertext, Instant createdAt, Instant expiresAt, int maxViews, int views) {
        this.id = id;
        this.ciphertext = ciphertext;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.maxViews = maxViews;
        this.views = views;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getMaxViews() {
        return maxViews;
    }

    public void setMaxViews(int maxViews) {
        this.maxViews = maxViews;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }



}
