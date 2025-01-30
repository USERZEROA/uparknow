package edu.utah.cs.uparknow.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知属性
public class FrontEndSpacesDTO {

    @JsonProperty("space_Row")
    private Integer spaceRow;     

    @JsonProperty("space_Column")
    private Integer spaceColumn;

    @JsonProperty("space_Parked")
    private Boolean spaceParked;  

    @JsonProperty("lot_ID")
    private Integer lotId;        

    @JsonProperty("space_Sch")
    private Date spaceSch;       

    @JsonProperty("permit_ID")
    private Integer permitId;    

    @JsonProperty("space_ID")
    private Integer spaceId;      

    public FrontEndSpacesDTO() {
    }

    // Getters 和 Setters
    public Integer getSpaceRow() {
        return spaceRow;
    }

    public void setSpaceRow(Integer spaceRow) {
        this.spaceRow = spaceRow;
    }

    public Integer getSpaceColumn() {
        return spaceColumn;
    }

    public void setSpaceColumn(Integer spaceColumn) {
        this.spaceColumn = spaceColumn;
    }

    public Boolean getSpaceParked() {
        return spaceParked;
    }

    public void setSpaceParked(Boolean spaceParked) {
        this.spaceParked = spaceParked;
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Date getSpaceSch() {
        return spaceSch;
    }

    public void setSpaceSch(Date spaceSch) {
        this.spaceSch = spaceSch;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }
}
