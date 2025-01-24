package edu.utah.cs.uparknow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.ParkingSpaces;

@Repository
public interface ParkingSpacesRepository extends JpaRepository<ParkingSpaces, Integer> {

    /**
     * 根据 (lotId, spaceRow, spaceColumn) 查找某个车位
     */
    @Query("SELECT p FROM ParkingSpaces p " +
           "WHERE p.Lot_ID = :lotId " +
           "  AND p.Space_Row = :row " +
           "  AND p.Space_Column = :col")
    Optional<ParkingSpaces> findByLotIdAndSpaceRowAndSpaceColumn(
            Integer lotId,
            Integer row,
            Integer col
    );

    /**
     * 获取当前表里最大的 Space_ID。如果还没有任何记录，则返回 0
     */
    @Query("SELECT COALESCE(MAX(p.Space_ID), 0) FROM ParkingSpaces p")
    int getMaxSpaceId();
}
