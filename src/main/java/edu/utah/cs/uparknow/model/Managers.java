package edu.utah.cs.uparknow.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Mana_ID", nullable = false)
    private Integer manaId;

    @Column(name = "Mana_Name", nullable = false, length = 64)
    private String manaName;

    @Column(name = "Mana_Username", nullable = false, length = 64)
    private String manaUsername;

    @Column(name = "Mana_Password", nullable = false, length = 100)
    private String manaPassword;

    @Column(name = "Mana_Token", length = 256)
    private String manaToken;
    
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("managers-closures")
    private List<Closures> closures;
}
