package edu.utah.cs.uparknow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @JsonProperty("Place_ID")
    private Integer placeId;

    @Column(name = "Place_Name", nullable = false, length = 64)
    @JsonProperty("Place_Name")
    private String placeName;

    @Column(name = "Place_Name_Abv", nullable = false, length = 64)
    @JsonProperty("Place_Name_Abv")
    private String placeNameAbv;

    @Column(name = "Place_Lat", nullable = false)
    @JsonProperty("Place_Lat")
    private Double placeLat;

    @Column(name = "Place_Lon", nullable = false)
    @JsonProperty("Place_Lon")
    private Double placeLon;
}
