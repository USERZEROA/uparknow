package edu.utah.cs.uparknow.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telemetry")
@IdClass(TelemetryId.class)
@Data
@NoArgsConstructor
public class Telemetry {

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tel_Datetime", nullable = false)
    private Date Tel_Datetime;

    @Column(name = "Tel_Status", length = 8)
    private String Tel_Status;

    @Id
    @Column(name = "Space_ID", nullable = false)
    private Integer Space_ID;

    // Foreign key relationship - ParkingSpaces
    @ManyToOne
    @JoinColumn(name = "Space_ID", referencedColumnName = "Space_ID", insertable = false, updatable = false)
    @JsonBackReference("parkingSpaces-telemetryList")
    private ParkingSpaces parkingSpace;

    // other Foreign key relationship
}
