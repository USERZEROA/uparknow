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
import edu.utah.cs.uparknow.model.ParkingLots;
import edu.utah.cs.uparknow.service.ParkingLotsService;

@RestController
@RequestMapping("/api/v1")
public class ParkingLotsController {

    @Autowired
    private ParkingLotsService parkingLotsService;

    @GetMapping("/parkinglots")
    public List<ParkingLots> getAllParkingLots() {
        return parkingLotsService.getAllParkingLots();
    }

    @GetMapping("/parkinglots/{id}")
    public ResponseEntity<ParkingLots> getParkingLotById(@PathVariable("id") Integer id) {
        ParkingLots parkingLot = parkingLotsService.getParkingLotById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found for this id :: " + id));
        return ResponseEntity.ok().body(parkingLot);
    }

    @PostMapping("/parkinglots")
    public ParkingLots createParkingLot(@RequestBody ParkingLots parkingLot) {
        return parkingLotsService.createParkingLot(parkingLot);
    }

    @PutMapping("/parkinglots/{id}")
    public ResponseEntity<ParkingLots> updateParkingLot(@PathVariable("id") Integer id,
                                                        @RequestBody ParkingLots parkingLotDetails) {
        ParkingLots updatedParkingLot = parkingLotsService.updateParkingLot(id, parkingLotDetails);
        return ResponseEntity.ok(updatedParkingLot);
    }

    @DeleteMapping("/parkinglots/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable("id") Integer id) {
        parkingLotsService.deleteParkingLot(id);
        return ResponseEntity.noContent().build();
    }
}
