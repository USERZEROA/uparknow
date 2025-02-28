package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.Telemetry;
import edu.utah.cs.uparknow.model.TelemetryId;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, TelemetryId> {
    
}
