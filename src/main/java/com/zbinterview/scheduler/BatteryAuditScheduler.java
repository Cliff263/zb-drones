package com.zbinterview.scheduler;

import com.zbinterview.entity.BatteryAudit;
import com.zbinterview.entity.Drone;
import com.zbinterview.repository.BatteryAuditRepository;
import com.zbinterview.repository.DroneRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;

@Component
@EnableScheduling
public class BatteryAuditScheduler {
    private final DroneRepository droneRepository;
    private final BatteryAuditRepository auditRepository;
    private final TaskScheduler taskScheduler;

    @Value("${drones.audit.interval-ms:60000}")
    private long intervalMs;

    public BatteryAuditScheduler(DroneRepository droneRepository,
                                 BatteryAuditRepository auditRepository,
                                 TaskScheduler taskScheduler) {
        this.droneRepository = droneRepository;
        this.auditRepository = auditRepository;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void schedule() {
        taskScheduler.scheduleAtFixedRate(this::runAudit, intervalMs);
    }

    private void runAudit() {
        for (Drone d : droneRepository.findAll()) {
            BatteryAudit audit = new BatteryAudit(d.getSerialNumber(), d.getBatteryPercentage(), Instant.now());
            auditRepository.save(audit);
        }
    }
}


