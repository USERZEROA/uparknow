package edu.utah.cs.uparknow.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ClosuresId implements Serializable {

    private Integer Space_ID;
    private Integer Mana_ID;
    private Date Mod_Start;

    public ClosuresId() {}

    public ClosuresId(Integer Space_ID, Integer Mana_ID, Date Mod_Start) {
        this.Space_ID = Space_ID;
        this.Mana_ID = Mana_ID;
        this.Mod_Start = Mod_Start;
    }

    // Getters and Setters
    public Integer getSpace_ID() {
        return Space_ID;
    }

    public void setSpace_ID(Integer Space_ID) {
        this.Space_ID = Space_ID;
    }

    public Integer getMana_ID() {
        return Mana_ID;
    }

    public void setMana_ID(Integer Mana_ID) {
        this.Mana_ID = Mana_ID;
    }

    public Date getMod_Start() {
        return Mod_Start;
    }

    public void setMod_Start(Date Mod_Start) {
        this.Mod_Start = Mod_Start;
    }

    // rewrite equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClosuresId that = (ClosuresId) o;

        return Objects.equals(Space_ID, that.Space_ID) &&
               Objects.equals(Mana_ID, that.Mana_ID) &&
               Objects.equals(Mod_Start, that.Mod_Start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Space_ID, Mana_ID, Mod_Start);
    }
}
