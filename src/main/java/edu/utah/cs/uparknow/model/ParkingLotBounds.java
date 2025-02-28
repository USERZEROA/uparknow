package edu.utah.cs.uparknow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_lots_bounds")
@Data
@NoArgsConstructor
public class ParkingLotBounds {
    @Id
    @Column(name = "Lot_ID", nullable = false)
    @JsonProperty("lot_ID")
    private Integer lotId;

    @Column(name = "Lat_tl", nullable = false)
    @JsonProperty("lat_tl")
    private Double latTl;

    @Column(name = "Lon_tl", nullable = false)
    @JsonProperty("lon_tl")
    private Double lonTl;

    @Column(name = "Lat_tr", nullable = false)
    @JsonProperty("lat_tr")
    private Double latTr;

    @Column(name = "Lon_tr", nullable = false)
    @JsonProperty("lon_tr")
    private Double lonTr;

    @Column(name = "Lat_bl", nullable = false)
    @JsonProperty("lat_bl")
    private Double latBl;

    @Column(name = "Lon_bl", nullable = false)
    @JsonProperty("lon_bl")
    private Double lonBl;

    @Column(name = "Lat_br", nullable = false)
    @JsonProperty("lat_br")
    private Double latBr;

    @Column(name = "Lon_br", nullable = false)
    @JsonProperty("lon_br")
    private Double lonBr;
}
