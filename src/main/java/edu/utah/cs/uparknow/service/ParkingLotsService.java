package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.ParkingLots;
import edu.utah.cs.uparknow.repository.ParkingLotsRepository;

@Service
public class ParkingLotsService {

    @Autowired
    private ParkingLotsRepository parkingLotsRepository;

    public List<ParkingLots> getAllParkingLots() {
        return parkingLotsRepository.findAll();
    }

    public Optional<ParkingLots> getParkingLotById(Integer id) {
        return parkingLotsRepository.findById(id);
    }

    public ParkingLots createParkingLot(ParkingLots parkingLot) {
        return parkingLotsRepository.save(parkingLot);
    }

    public ParkingLots updateParkingLot(Integer id, ParkingLots parkingLotDetails) {
        ParkingLots parkingLot = parkingLotsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found for this id :: " + id));

        parkingLot.setLot_Name(parkingLotDetails.getLot_Name());
        return parkingLotsRepository.save(parkingLot);
    }

    public void deleteParkingLot(Integer id) {
        ParkingLots parkingLot = parkingLotsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found for this id :: " + id));
        parkingLotsRepository.delete(parkingLot);
    }
}
