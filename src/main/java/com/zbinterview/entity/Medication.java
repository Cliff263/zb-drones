package com.zbinterview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9_-]+")
    @Column(nullable = false)
    private String name;

    @Positive
    @Column(nullable = false)
    private int weightGrams;

    @NotBlank
    @Pattern(regexp = "[A-Z0-9_]+")
    @Column(nullable = false, unique = true)
    private String code;

    private String imageUrl;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWeightGrams() { return weightGrams; }
    public void setWeightGrams(int weightGrams) { this.weightGrams = weightGrams; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}


