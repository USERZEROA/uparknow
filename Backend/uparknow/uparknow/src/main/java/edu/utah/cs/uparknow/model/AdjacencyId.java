package edu.utah.cs.uparknow.model;

import java.io.Serializable;
import java.util.Objects;

public class AdjacencyId implements Serializable {

    private Integer Lot_ID;
    private Integer Place_ID;

    public AdjacencyId() {}

    public AdjacencyId(Integer Lot_ID, Integer Place_ID) {
        this.Lot_ID = Lot_ID;
        this.Place_ID = Place_ID;
    }

    // Getters and Setters
    public Integer getLot_ID() {
        return Lot_ID;
    }

    public void setLot_ID(Integer Lot_ID) {
        this.Lot_ID = Lot_ID;
    }

    public Integer getPlace_ID() {
        return Place_ID;
    }

    public void setPlace_ID(Integer Place_ID) {
        this.Place_ID = Place_ID;
    }

    // rewrite equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdjacencyId that = (AdjacencyId) o;

        return Objects.equals(Lot_ID, that.Lot_ID) &&
               Objects.equals(Place_ID, that.Place_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Lot_ID, Place_ID);
    }
}
