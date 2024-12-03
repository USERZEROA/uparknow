package edu.utah.cs.uparknow.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_spaces")
@Data
@NoArgsConstructor
public class ParkingSpaces {

    @Id
    @Column(name = "Space_ID", nullable = false)
    private Integer Space_ID;

    @Column(name = "Space_Row", nullable = false)
    private Integer Space_Row;

    @Column(name = "Space_Column", nullable = false)
    private Integer Space_Column;

    @Column(name = "Space_Parked", nullable = false)
    private Boolean Space_Parked;

    @Column(name = "Space_Sch")
    @Temporal(TemporalType.TIMESTAMP)
    private Date Space_Sch;

    @Column(name = "Lot_ID", nullable = false, insertable = false, updatable = false)
    private Integer Lot_ID;

    @Column(name = "Permit_ID", nullable = false, insertable = false, updatable = false)
    private Integer Permit_ID;

    // Foreign key relationship - ParkingLots
    @ManyToOne
    @JoinColumn(name = "Lot_ID", referencedColumnName = "Lot_ID")
    @JsonBackReference("parkingLots-parkingSpaces")
    private ParkingLots parkingLot;

    // Foreign key relationship - Permits
    @ManyToOne
    @JoinColumn(name = "Permit_ID", referencedColumnName = "Permit_ID")
    @JsonBackReference("permits-parkingSpaces")
    private Permits permit;

    // One-to-many relationship, management end - Closures
    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingSpaces-closures")
    private List<Closures> closures;

    // One-to-many relationship, management end - Telemetry
    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingSpaces-telemetryList")
    private List<Telemetry> telemetryList;

    // other Foreign key relationship
}
