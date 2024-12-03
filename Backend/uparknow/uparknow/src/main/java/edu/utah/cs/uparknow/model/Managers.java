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
@Table(name = "managers")
@Data
@NoArgsConstructor
public class Managers {

    @Id
    @Column(name = "Mana_ID", nullable = false)
    private Integer Mana_ID;

    @Column(name = "Mana_Name", nullable = false, length = 64)
    private String Mana_Name;

    // One-to-many relationship, management end - Closures
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("managers-closures")
    private List<Closures> closures;

    // other Foreign key relationship
}
