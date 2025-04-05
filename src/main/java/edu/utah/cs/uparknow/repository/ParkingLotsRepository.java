package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.utah.cs.uparknow.model.ParkingLots;

@Repository
public interface ParkingLotsRepository extends JpaRepository<ParkingLots, Integer> {
    
}
