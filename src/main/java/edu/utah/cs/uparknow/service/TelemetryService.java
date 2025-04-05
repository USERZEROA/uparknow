package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Telemetry;
import edu.utah.cs.uparknow.model.TelemetryId;
import edu.utah.cs.uparknow.repository.TelemetryRepository;

@Service
public class TelemetryService {

    @Autowired
    private TelemetryRepository telemetryRepository;

    public List<Telemetry> getAllTelemetry() {
        return telemetryRepository.findAll();
    }

    public Optional<Telemetry> getTelemetryById(TelemetryId id) {
        return telemetryRepository.findById(id);
    }

    public Telemetry createTelemetry(Telemetry telemetry) {
        return telemetryRepository.save(telemetry);
    }

    public Telemetry updateTelemetry(TelemetryId id, Telemetry telemetryDetails) {
        Telemetry telemetry = telemetryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Telemetry not found for this id :: " + id));

        telemetry.setTel_Status(telemetryDetails.getTel_Status());
        return telemetryRepository.save(telemetry);
    }

    public void deleteTelemetry(TelemetryId id) {
        Telemetry telemetry = telemetryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Telemetry not found for this id :: " + id));
        telemetryRepository.delete(telemetry);
    }
}
