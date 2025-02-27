package edu.utah.cs.uparknow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.ParkingSpaces;

@Repository
public interface ParkingSpacesRepository extends JpaRepository<ParkingSpaces, Integer> {
    
    @Query("SELECT p FROM ParkingSpaces p " +
           "WHERE p.Lot_ID = :lotId " +
           "  AND p.Space_Row = :row " +
           "  AND p.Space_Column = :col")
    Optional<ParkingSpaces> findByLotIdAndSpaceRowAndSpaceColumn(
            Integer lotId,
            Integer row,
            Integer col
    );

}
