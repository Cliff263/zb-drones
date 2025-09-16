package com.zbinterview.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.zbinterview.entity.Drone;
import com.zbinterview.entity.Medication;
import com.zbinterview.model.DroneModel;
import com.zbinterview.model.DroneState;
import com.zbinterview.repository.DroneRepository;
import com.zbinterview.repository.MedicationRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    public DataLoader(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    @Override
    public void run(String... args) {
        if (droneRepository.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                Drone d = new Drone();
                d.setSerialNumber("DRN-" + (1000 + i));
                d.setModel(i <= 3 ? DroneModel.LIGHTWEIGHT : i <= 6 ? DroneModel.MIDDLEWEIGHT : i <= 8 ? DroneModel.CRUISERWEIGHT : DroneModel.HEAVYWEIGHT);
                d.setWeightLimitGrams(500);
                d.setBatteryPercentage(40 + (i % 3) * 20);
                d.setState(DroneState.IDLE);
                droneRepository.save(d);
            }
        }

        if (medicationRepository.count() == 0) {
            for (int i = 1; i <= 8; i++) {
                Medication m = new Medication();
                m.setName("Med_" + i);
                m.setCode("MED_" + i);
                m.setWeightGrams(30 + i * 20);
                m.setImageUrl("https://example.com/med_" + i + ".png");
                medicationRepository.save(m);
            }
        }
    }
}
