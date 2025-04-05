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
import edu.utah.cs.uparknow.model.Locations;
import edu.utah.cs.uparknow.service.LocationsService;

@RestController
@RequestMapping("/api/v1")
public class LocationsController {

    @Autowired
    private LocationsService locationsService;

    @GetMapping("/locations")
    public List<Locations> getAllLocations() {
        return locationsService.getAllLocations();
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Locations> getLocationById(@PathVariable("id") Integer id) {
        Locations location = locationsService.getLocationById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Location not found for this id :: " + id));
        return ResponseEntity.ok().body(location);
    }

    @PostMapping("/locations")
    public Locations createLocation(@RequestBody Locations location) {
        return locationsService.createLocation(location);
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<Locations> updateLocation(@PathVariable("id") Integer id,
                                                    @RequestBody Locations locationDetails) {
        Locations updatedLocation = locationsService.updateLocation(id, locationDetails);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Integer id) {
        locationsService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
