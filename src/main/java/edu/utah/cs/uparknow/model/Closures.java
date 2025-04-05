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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "closures")
@IdClass(ClosuresId.class)
@Data
@NoArgsConstructor
public class Closures {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "Space_ID", nullable = false)
    private Integer spaceId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "Mana_ID", nullable = false)
    private Integer manaId;

    @Id
    @EqualsAndHashCode.Include
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Mod_Start", nullable = false)
    private Date modStart;

    @Column(name = "Mod_End")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modEnd;

    @Column(name = "Mod_Reason", nullable = false, columnDefinition = "TEXT")
    private String modReason;

    @ManyToOne
    @JoinColumn(name = "Space_ID", referencedColumnName = "Space_ID", insertable = false, updatable = false)
    @JsonBackReference("parkingSpaces-closures")
    private ParkingSpaces parkingSpace;

    @ManyToOne
    @JoinColumn(name = "Mana_ID", referencedColumnName = "Mana_ID", insertable = false, updatable = false)
    @JsonBackReference("managers-closures")
    private Managers manager;
}
