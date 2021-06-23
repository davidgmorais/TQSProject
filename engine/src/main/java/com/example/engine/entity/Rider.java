package com.example.engine.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Rider")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Rider's unique ID automatically generated", required = true)
    private int id;

    @Column(name = "verified", nullable = false, columnDefinition = "boolean default false")
    @ApiModelProperty(notes = "Flag to mark if the correspondent Rider is validated by the admin or not", required = true)
    private Boolean verified;

    @Column(name = "working", nullable = false, columnDefinition = "boolean default false")
    @ApiModelProperty(notes = "Flag to mark if the correspondent Rider is currently doing a shift or not", required = true)
    private Boolean isWorking;

    @Column(name = "location_lat")
    @ApiModelProperty(notes = "Current latitude coordinate of a rider")
    private Double locationLat;

    @Column(name = "location_lon")
    @ApiModelProperty(notes = "Current longitude coordinate of a rider")
    private Double locationLon;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(notes = "User account information linked to the rider", required = true)
    private User user;

    public Rider() {}
    public Rider(@NotNull User user) {
        this.user = user;
        this.verified = false;
        this.isWorking = false;
    }

    public Rider(int id, Boolean verified, Boolean isWorking, Double locationLat, Double locationLon, User user) {
        this.id = id;
        this.verified = verified;
        this.isWorking = isWorking;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return user;
    }

    public Boolean isWorking() {
        return isWorking;
    }

    public Double[] getLocation() {
        return new Double[]{this.locationLat, this.locationLon};
    }

    public void setLocation(Double lat, Double lon) {
        this.locationLat = lat;
        this.locationLon = lon;
    }

    public void setWorking(Boolean working) {
        isWorking = working;
    }

}
