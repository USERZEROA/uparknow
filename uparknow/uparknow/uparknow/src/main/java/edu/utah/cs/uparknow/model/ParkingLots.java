package edu.utah.cs.uparknow.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_lots")
@Data
@NoArgsConstructor
public class ParkingLots {

    @Id
    @Column(name = "Lot_ID", nullable = false)
    private Integer Lot_ID;

    @Column(name = "Lot_Name", nullable = false, length = 64)
    private String Lot_Name;

    // One-to-many relationship, management end - Adjacencies
    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingLots-adjacencies")
    private List<Adjacency> adjacencies;

    // One-to-many relationship, management end - ParkingSpaces
    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingLots-parkingSpaces")
    private List<ParkingSpaces> parkingSpaces;
}
