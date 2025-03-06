package edu.utah.cs.uparknow.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TelemetryId implements Serializable {

    private Date Tel_Datetime;
    private Integer Space_ID;

    public TelemetryId() {}

    public TelemetryId(Date Tel_Datetime, Integer Space_ID) {
        this.Tel_Datetime = Tel_Datetime;
        this.Space_ID = Space_ID;
    }

    // Getters 和 Setters
    public Date getTel_Datetime() {
        return Tel_Datetime;
    }

    public void setTel_Datetime(Date Tel_Datetime) {
        this.Tel_Datetime = Tel_Datetime;
    }

    public Integer getSpace_ID() {
        return Space_ID;
    }

    public void setSpace_ID(Integer Space_ID) {
        this.Space_ID = Space_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelemetryId that = (TelemetryId) o;

        return Objects.equals(Tel_Datetime, that.Tel_Datetime) &&
               Objects.equals(Space_ID, that.Space_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Tel_Datetime, Space_ID);
    }
}
