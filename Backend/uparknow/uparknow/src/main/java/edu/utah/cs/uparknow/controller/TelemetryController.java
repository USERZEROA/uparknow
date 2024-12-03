package edu.utah.cs.uparknow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Telemetry;
import edu.utah.cs.uparknow.model.TelemetryId;
import edu.utah.cs.uparknow.service.TelemetryService;

@RestController
@RequestMapping("/api/v1")
public class TelemetryController {

    @Autowired
    private TelemetryService telemetryService;

    @GetMapping("/telemetry")
    public List<Telemetry> getAllTelemetry() {
        return telemetryService.getAllTelemetry();
    }

    @GetMapping("/telemetry/{telDatetime}/{spaceId}")
    public ResponseEntity<Telemetry> getTelemetryById(@PathVariable("telDatetime") String telDatetime,
                                                     @PathVariable("spaceId") Integer spaceId) {
        // Parse the telDatetime string into a Date object
        java.util.Date telDatetimeDate = parseDate(telDatetime);
        TelemetryId id = new TelemetryId(telDatetimeDate, spaceId);
        Telemetry telemetry = telemetryService.getTelemetryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telemetry not found for id :: " + id));
        return ResponseEntity.ok().body(telemetry);
    }

    @PostMapping("/telemetry")
    public Telemetry createTelemetry(@RequestBody Telemetry telemetry) {
        return telemetryService.createTelemetry(telemetry);
    }

    @PutMapping("/telemetry/{telDatetime}/{spaceId}")
    public ResponseEntity<Telemetry> updateTelemetry(@PathVariable("telDatetime") String telDatetime,
                                                     @PathVariable("spaceId") Integer spaceId,
                                                     @RequestBody Telemetry telemetryDetails) {
        // Parse the telDatetime string into a Date object
        java.util.Date telDatetimeDate = parseDate(telDatetime);
        TelemetryId id = new TelemetryId(telDatetimeDate, spaceId);
        Telemetry updatedTelemetry = telemetryService.updateTelemetry(id, telemetryDetails);
        return ResponseEntity.ok(updatedTelemetry);
    }

    @DeleteMapping("/telemetry/{telDatetime}/{spaceId}")
    public ResponseEntity<Void> deleteTelemetry(@PathVariable("telDatetime") String telDatetime,
                                               @PathVariable("spaceId") Integer spaceId) {
        // Parse the telDatetime string into a Date object
        java.util.Date telDatetimeDate = parseDate(telDatetime);
        TelemetryId id = new TelemetryId(telDatetimeDate, spaceId);
        telemetryService.deleteTelemetry(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method: Parse the date string
    private java.util.Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateStr);
        } catch (java.text.ParseException e) {
            throw new ResourceNotFoundException("Invalid date format for telDatetime :: " + dateStr);
        }
    }
}
