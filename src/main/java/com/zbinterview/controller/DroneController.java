package com.zbinterview.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zbinterview.entity.Drone;
import com.zbinterview.entity.Medication;
import com.zbinterview.service.DroneService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drones")
public class DroneController {
    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping
    public ResponseEntity<List<Drone>> listAll() {
        return ResponseEntity.ok(droneService.findAvailableForLoading());
    }

    @PostMapping
    public ResponseEntity<Drone> register(@Valid @RequestBody Drone drone) {
        Drone created = droneService.registerDrone(drone);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/{serial}/load")
    public ResponseEntity<Drone> load(@PathVariable String serial, @RequestBody List<String> medicationCodes) {
        Drone updated = droneService.loadMedications(serial, medicationCodes);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{serial}/medications")
    public ResponseEntity<Set<Medication>> getLoaded(@PathVariable String serial) {
        return ResponseEntity.ok(droneService.checkLoadedMedications(serial));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Drone>> available() {
        return ResponseEntity.ok(droneService.findAvailableForLoading());
    }

    @GetMapping("/{serial}/battery")
    public ResponseEntity<Map<String, Object>> battery(@PathVariable String serial) {
        int level = droneService.getBatteryLevel(serial);
        return ResponseEntity.ok(Map.of("serial", serial, "battery", level));
    }
}


