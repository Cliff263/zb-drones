package com.zbinterview.service;

import com.zbinterview.entity.Drone;
import com.zbinterview.entity.Medication;
import com.zbinterview.model.DroneState;
import com.zbinterview.repository.DroneRepository;
import com.zbinterview.repository.MedicationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DroneService {
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    public DroneService(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    public Drone registerDrone(Drone drone) {
        if (drone.getWeightLimitGrams() > 500) {
            throw new IllegalArgumentException("Weight limit cannot exceed 500 grams");
        }
        if (drone.getBatteryPercentage() < 0 || drone.getBatteryPercentage() > 100) {
            throw new IllegalArgumentException("Battery percentage must be between 0 and 100");
        }
        if (drone.getState() == null) {
            drone.setState(DroneState.IDLE);
        }
        return droneRepository.save(drone);
    }

    public Optional<Drone> findBySerial(String serial) {
        return droneRepository.findBySerialNumber(serial);
    }

    public List<Drone> findAvailableForLoading() {
        return droneRepository.findByState(DroneState.IDLE);
    }

    public int getBatteryLevel(String serial) {
        Drone drone = droneRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new NoSuchElementException("Drone not found"));
        return drone.getBatteryPercentage();
    }

    @Transactional
    public Drone loadMedications(String serial, List<String> medicationCodes) {
        Drone drone = droneRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new NoSuchElementException("Drone not found"));

        if (drone.getBatteryPercentage() < 25) {
            throw new IllegalStateException("Cannot load drone with battery below 25%");
        }

        if (!(drone.getState() == DroneState.IDLE || drone.getState() == DroneState.LOADING)) {
            throw new IllegalStateException("Drone is not available for loading");
        }

        Set<Medication> meds = new HashSet<>(drone.getMedications());
        for (String code : medicationCodes) {
            Medication med = medicationRepository.findByCode(code)
                    .orElseThrow(() -> new NoSuchElementException("Medication not found: " + code));
            meds.add(med);
        }

        int totalWeight = meds.stream().mapToInt(Medication::getWeightGrams).sum();
        if (totalWeight > drone.getWeightLimitGrams()) {
            throw new IllegalStateException("Total weight exceeds drone limit");
        }

        drone.setMedications(meds);
        drone.setState(meds.isEmpty() ? DroneState.IDLE : DroneState.LOADED);
        return droneRepository.save(drone);
    }

    public Set<Medication> checkLoadedMedications(String serial) {
        Drone drone = droneRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new NoSuchElementException("Drone not found"));
        return drone.getMedications();
    }
}


