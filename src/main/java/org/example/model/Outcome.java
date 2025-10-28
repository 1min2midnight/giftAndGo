package org.example.model;

import java.math.BigDecimal;

public class Outcome {
    private String name;
    private String transport;
    private BigDecimal topSpeed;

    public Outcome(String name, String transport, BigDecimal topSpeed) {
        this.name = name;
        this.transport = transport;
        this.topSpeed = topSpeed;
    }

    public String getName() {
        return name;
    }

    public String getTransport() {
        return transport;
    }

    public BigDecimal getTopSpeed() {
        return topSpeed;
    }
}