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
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingLotsRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import edu.utah.cs.uparknow.repository.PermitsRepository;

@RestController
@RequestMapping("/api/v1")
public class ParkingSpacesController {

    @Autowired
    private ParkingSpacesRepository parkingSpacesRepository;

    @Autowired
    private ParkingLotsRepository parkingLotsRepository;

    @Autowired
    private PermitsRepository permitsRepository;

    @GetMapping("/parkingspaces")
    public List<ParkingSpaces> getAllParkingSpaces() {
        return parkingSpacesRepository.findAll();
    }

    @GetMapping("/parkingspaces/{spaceId}")
    public ResponseEntity<ParkingSpaces> getParkingSpaceById(@PathVariable(value = "spaceId") Integer spaceId)
            throws ResourceNotFoundException {
        ParkingSpaces parkingSpace = parkingSpacesRepository.findById(spaceId)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingSpace not found for this id :: " + spaceId));
        return ResponseEntity.ok().body(parkingSpace);
    }

    @PostMapping("/parkingspaces")
    public ResponseEntity<ParkingSpaces> createParkingSpace(@RequestBody ParkingSpaces parkingSpace) throws ResourceNotFoundException {
        if (parkingSpace.getLot_ID() != null) {
            parkingSpace.setParkingLot(parkingLotsRepository.findById(parkingSpace.getLot_ID())
            .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found for id :: " + parkingSpace.getLot_ID())));
        }

        if (parkingSpace.getPermit_ID() != null) {
            parkingSpace.setPermit(permitsRepository.findById(parkingSpace.getPermit_ID())
            .orElseThrow(() -> new ResourceNotFoundException("Permit not found for id :: " + parkingSpace.getPermit_ID())));
        }
        ParkingSpaces createdParkingSpace = parkingSpacesRepository.save(parkingSpace);
        return ResponseEntity.status(201).body(createdParkingSpace);
    }

    @PutMapping("/parkingspaces/{spaceId}")
    public ResponseEntity<ParkingSpaces> updateParkingSpace(@PathVariable(value = "spaceId") Integer spaceId,
            @RequestBody ParkingSpaces parkingSpaceDetails) throws ResourceNotFoundException {

        ParkingSpaces parkingSpace = parkingSpacesRepository.findById(spaceId)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingSpace not found for this id :: " + spaceId));

        parkingSpace.setSpace_Row(parkingSpaceDetails.getSpace_Row());
        parkingSpace.setSpace_Column(parkingSpaceDetails.getSpace_Column());
        parkingSpace.setSpace_Parked(parkingSpaceDetails.getSpace_Parked());
        parkingSpace.setSpace_Sch(parkingSpaceDetails.getSpace_Sch());

        if (parkingSpaceDetails.getLot_ID() != null) {
            parkingSpace.setParkingLot(parkingLotsRepository.findById(parkingSpaceDetails.getLot_ID())
            .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found for id :: " + parkingSpaceDetails.getLot_ID())));
        }

        if (parkingSpaceDetails.getPermit_ID() != null) {
            parkingSpace.setPermit(permitsRepository.findById(parkingSpaceDetails.getPermit_ID())
            .orElseThrow(() -> new ResourceNotFoundException("Permit not found for id :: " + parkingSpaceDetails.getPermit_ID())));
        }

        final ParkingSpaces updatedParkingSpace = parkingSpacesRepository.save(parkingSpace);
        return ResponseEntity.ok(updatedParkingSpace);
    }

    @DeleteMapping("/parkingspaces/{spaceId}")
    public ResponseEntity<Void> deleteParkingSpace(@PathVariable(value = "spaceId") Integer spaceId)
            throws ResourceNotFoundException {

        ParkingSpaces parkingSpace = parkingSpacesRepository.findById(spaceId)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingSpace not found for this id :: " + spaceId));
        parkingSpacesRepository.delete(parkingSpace);
        return ResponseEntity.noContent().build();
    }
}
