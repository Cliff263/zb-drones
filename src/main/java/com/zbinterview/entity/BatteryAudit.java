package com.zbinterview.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class BatteryAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String droneSerialNumber;

    @Column(nullable = false)
    private int batteryPercentage;

    @Column(nullable = false)
    private Instant recordedAt;

    public BatteryAudit() {}
    public BatteryAudit(String droneSerialNumber, int batteryPercentage, Instant recordedAt) {
        this.droneSerialNumber = droneSerialNumber;
        this.batteryPercentage = batteryPercentage;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public String getDroneSerialNumber() { return droneSerialNumber; }
    public int getBatteryPercentage() { return batteryPercentage; }
    public Instant getRecordedAt() { return recordedAt; }
}


