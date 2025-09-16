package com.zbinterview.entity;

import java.util.HashSet;
import java.util.Set;

import com.zbinterview.model.DroneModel;
import com.zbinterview.model.DroneState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DroneModel model;

    @Max(500)
    @Positive
    @Column(nullable = false)
    private int weightLimitGrams;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private int batteryPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DroneState state;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "drone_medications",
            joinColumns = @JoinColumn(name = "drone_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id"))
    private Set<Medication> medications = new HashSet<>();

    public Long getId() { return id; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public DroneModel getModel() { return model; }
    public void setModel(DroneModel model) { this.model = model; }
    public int getWeightLimitGrams() { return weightLimitGrams; }
    public void setWeightLimitGrams(int weightLimitGrams) { this.weightLimitGrams = weightLimitGrams; }
    public int getBatteryPercentage() { return batteryPercentage; }
    public void setBatteryPercentage(int batteryPercentage) { this.batteryPercentage = batteryPercentage; }
    public DroneState getState() { return state; }
    public void setState(DroneState state) { this.state = state; }
    public Set<Medication> getMedications() { return medications; }
    public void setMedications(Set<Medication> medications) { this.medications = medications; }
}


