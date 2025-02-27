package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.utah.cs.uparknow.model.ParkingLotBounds;

public interface ParkingLotBoundsRepository extends JpaRepository<ParkingLotBounds, Integer> {
}
