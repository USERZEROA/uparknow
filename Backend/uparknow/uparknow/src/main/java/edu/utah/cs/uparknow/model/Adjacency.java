package edu.utah.cs.uparknow.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "adjacency")
@IdClass(AdjacencyId.class)
@Data
@NoArgsConstructor
public class Adjacency {

    @Id
    @Column(name = "Lot_ID", nullable = false)
    private Integer Lot_ID;

    @Id
    @Column(name = "Place_ID", nullable = false)
    private Integer Place_ID;

    // Foreign key relationship - ParkingLots
    @ManyToOne
    @JoinColumn(name = "Lot_ID", referencedColumnName = "Lot_ID", insertable = false, updatable = false)
    @JsonBackReference("parkingLots-adjacencies")
    private ParkingLots parkingLot;

    // Foreign key relationship - Locations
    @ManyToOne
    @JoinColumn(name = "Place_ID", referencedColumnName = "Place_ID", insertable = false, updatable = false)
    @JsonBackReference("locations-adjacencies")
    private Locations location;

    // other foreign key relationship
}
