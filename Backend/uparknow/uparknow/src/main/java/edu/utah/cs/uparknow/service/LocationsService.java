package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Locations;
import edu.utah.cs.uparknow.repository.LocationsRepository;

@Service
public class LocationsService {

    @Autowired
    private LocationsRepository locationsRepository;

    public List<Locations> getAllLocations() {
        return locationsRepository.findAll();
    }

    public Optional<Locations> getLocationById(Integer id) {
        return locationsRepository.findById(id);
    }

    public Locations createLocation(Locations location) {
        return locationsRepository.save(location);
    }

    public Locations updateLocation(Integer id, Locations locationDetails) {
        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found for this id :: " + id));

        location.setPlace_Name(locationDetails.getPlace_Name());
        

        return locationsRepository.save(location);
    }

    public void deleteLocation(Integer id) {
        Locations location = locationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found for this id :: " + id));
        locationsRepository.delete(location);
    }
}
