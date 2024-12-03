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
@Table(name = "locations")
@Data
@NoArgsConstructor
public class Locations {

    @Id
    @Column(name = "Place_ID", nullable = false)
    private Integer Place_ID;

    @Column(name = "Place_Name", nullable = false, length = 64)
    private String Place_Name;

    // One-to-many relationship, management end - Adjacencies
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("locations-adjacencies")
    private List<Adjacency> adjacencies;

    // other foreign key relationship
}
