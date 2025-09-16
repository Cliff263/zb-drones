package com.zbinterview.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zbinterview.entity.Medication;
import com.zbinterview.repository.MedicationRepository;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {
    private final MedicationRepository medicationRepository;

    public MedicationController(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Medication>> listAll() {
        return ResponseEntity.ok(medicationRepository.findAll());
    }
}


