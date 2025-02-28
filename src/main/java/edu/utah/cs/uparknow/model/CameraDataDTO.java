package edu.utah.cs.uparknow.model;

import java.util.List;

public class CameraDataDTO {
    private Integer role;                     // "Role": 1
    private Integer parkingLot;               // "ParkingLot": 1
    private List<Integer> parkingSpacePosition; // "ParkingSpacePosition": [1,1]
    private Integer availability;             // "Availability": 1->没车, 0->有车

    public CameraDataDTO() {
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(Integer parkingLot) {
        this.parkingLot = parkingLot;
    }

    public List<Integer> getParkingSpacePosition() {
        return parkingSpacePosition;
    }

    public void setParkingSpacePosition(List<Integer> parkingSpacePosition) {
        this.parkingSpacePosition = parkingSpacePosition;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }
}
