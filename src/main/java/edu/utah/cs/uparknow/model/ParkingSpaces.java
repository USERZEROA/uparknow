package edu.utah.cs.uparknow.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_spaces")
@Data
@NoArgsConstructor
public class ParkingSpaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
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

    @Column(name = "Space_Disabled", nullable = false)
    @JsonProperty("space_Disabled")
    private Boolean spaceDisabled = false;
    
    @Column(name = "Space_Lon")
    private Double Space_Lon;     // 车位经度

    @Column(name = "Space_Lat")
    private Double Space_Lat;     // 车位纬度

    @Column(name = "Lot_ID", nullable = false)
    private Integer Lot_ID;

    @Column(name = "Permit_ID", nullable = false)
    private Integer Permit_ID;

    @Column(name = "Space_Monitored")
    @JsonProperty("space_Monitored")
    private Boolean spaceMonitored;

    @Version
    @JsonIgnore
    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "Lot_ID", referencedColumnName = "Lot_ID", insertable = false, updatable = false)
    @JsonBackReference("parkingLots-parkingSpaces")
    private ParkingLots parkingLot;

    @ManyToOne
    @JoinColumn(name = "Permit_ID", referencedColumnName = "Permit_ID", insertable = false, updatable = false)
    @JsonBackReference("permits-parkingSpaces")
    private Permits permit;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingSpaces-closures")
    @JsonIgnore
    private List<Closures> closures;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parkingSpaces-telemetryList")
    @JsonIgnore
    private List<Telemetry> telemetryList;
}
