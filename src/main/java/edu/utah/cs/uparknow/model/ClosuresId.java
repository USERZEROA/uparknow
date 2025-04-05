package edu.utah.cs.uparknow.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ClosuresId implements Serializable {

    private Integer spaceId;
    private Integer manaId;
    private Date modStart;

    public ClosuresId() {}

    public ClosuresId(Integer spaceId, Integer manaId, Date modStart) {
        this.spaceId = spaceId;
        this.manaId = manaId;
        this.modStart = modStart;
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public Integer getManaId() {
        return manaId;
    }

    public void setManaId(Integer manaId) {
        this.manaId = manaId;
    }

    public Date getModStart() {
        return modStart;
    }

    public void setModStart(Date modStart) {
        this.modStart = modStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClosuresId)) return false;
        ClosuresId that = (ClosuresId) o;
        return Objects.equals(getSpaceId(), that.getSpaceId()) &&
               Objects.equals(getManaId(), that.getManaId()) &&
               Objects.equals(getModStart(), that.getModStart());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpaceId(), getManaId(), getModStart());
    }
}
