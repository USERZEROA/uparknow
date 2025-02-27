package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

@Service
public class ParkingSpacesService {

    @Autowired
    private ParkingSpacesRepository parkingSpacesRepository;

    public List<ParkingSpaces> getAllParkingSpaces() {
        return parkingSpacesRepository.findAll();
    }

    public Optional<ParkingSpaces> getParkingSpaceById(Integer id) {
        return parkingSpacesRepository.findById(id);
    }

    public ParkingSpaces createParkingSpace(ParkingSpaces parkingSpace) {
        return parkingSpacesRepository.save(parkingSpace);
    }

    @Transactional
    public ParkingSpaces updateParkingSpace(Integer id, ParkingSpaces parkingSpaceDetails) {
        ParkingSpaces parkingSpace = parkingSpacesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSpace not found for this id :: " + id));

        parkingSpace.setSpace_Row(parkingSpaceDetails.getSpace_Row());
        parkingSpace.setSpace_Column(parkingSpaceDetails.getSpace_Column());
        parkingSpace.setSpace_Parked(parkingSpaceDetails.getSpace_Parked());
        parkingSpace.setSpace_Sch(parkingSpaceDetails.getSpace_Sch());
        

        return parkingSpacesRepository.save(parkingSpace);
    }

    public void deleteParkingSpace(Integer id) {
        ParkingSpaces parkingSpace = parkingSpacesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSpace not found for this id :: " + id));
        parkingSpacesRepository.delete(parkingSpace);
    }
}
