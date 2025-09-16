package com.zbinterview.controller;

import com.zbinterview.entity.Drone;
import com.zbinterview.entity.Medication;
import com.zbinterview.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        return ResponseEntity.ok(droneService.registerDrone(drone));
    }

    @PostMapping("/{serial}/load")
    public ResponseEntity<Drone> load(@PathVariable String serial, @RequestBody List<String> medicationCodes) {
        return ResponseEntity.ok(droneService.loadMedications(serial, medicationCodes));
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


