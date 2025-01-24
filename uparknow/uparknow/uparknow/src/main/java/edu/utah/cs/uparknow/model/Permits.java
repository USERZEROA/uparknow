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
@Table(name = "permits")
@Data
@NoArgsConstructor
public class Permits {

    @Id
    @Column(name = "Permit_ID", nullable = false)
    private Integer Permit_ID;

    @Column(name = "Permit_Name", nullable = false, length = 64)
    private String Permit_Name;

    // One-to-many relationship, management end - ParkingSpaces
    @OneToMany(mappedBy = "permit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("permits-parkingSpaces")
    private List<ParkingSpaces> parkingSpaces;

    // other Foreign key relationship
}
