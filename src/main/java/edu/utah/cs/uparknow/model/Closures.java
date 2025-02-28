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
@Table(name = "closures")
@IdClass(ClosuresId.class)
@Data
@NoArgsConstructor
public class Closures {

    @Id
    @Column(name = "Space_ID", nullable = false)
    private Integer Space_ID;

    @Id
    @Column(name = "Mana_ID", nullable = false)
    private Integer Mana_ID;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Mod_Start", nullable = false)
    private Date Mod_Start;

    @Column(name = "Mod_End")
    @Temporal(TemporalType.TIMESTAMP)
    private Date Mod_End;

    @Column(name = "Mod_Type", nullable = false, length = 16)
    private String Mod_Type;

    // Foreign key relationship - ParkingSpaces
    @ManyToOne
    @JoinColumn(name = "Space_ID", referencedColumnName = "Space_ID", insertable = false, updatable = false)
    @JsonBackReference("parkingSpaces-closures")
    private ParkingSpaces parkingSpace;

    // Foreign key relationship - Managers
    @ManyToOne
    @JoinColumn(name = "Mana_ID", referencedColumnName = "Mana_ID", insertable = false, updatable = false)
    @JsonBackReference("managers-closures")
    private Managers manager;

    // other foreign key relationship
}
