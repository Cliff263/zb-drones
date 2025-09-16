package com.zbinterview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zbinterview.entity.Drone;
import com.zbinterview.model.DroneState;

public interface DroneRepository extends JpaRepository<Drone, Long> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    List<Drone> findByState(DroneState state);

    @EntityGraph(attributePaths = {"medications"})
    Optional<Drone> findWithMedicationsBySerialNumber(String serialNumber);
}


