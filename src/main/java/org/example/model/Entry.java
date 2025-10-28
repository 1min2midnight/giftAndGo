package org.example.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class Entry {

    @NotNull
    private UUID uuid;

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String likes;

    @NotBlank
    private String transport;

    @DecimalMin(value = "0.0", inclusive = false, message = "Average speed must be > 0")
    private BigDecimal averageSpeed;

    @DecimalMin(value = "0.0", inclusive = false, message = "Top speed must be > 0")
    private BigDecimal topSpeed;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public BigDecimal getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(BigDecimal averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public BigDecimal getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(BigDecimal topSpeed) {
        this.topSpeed = topSpeed;
    }
}
