package com.example.engine.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Rider")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "verified", nullable = false, columnDefinition = "boolean default false")
    private Boolean verified;

    @Column(name = "working", nullable = false, columnDefinition = "boolean default false")
    private Boolean isWorking;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lon")
    private Double locationLon;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Rider() {}
    public Rider(@NotNull User user) {
        this.user = user;
        this.verified = false;
        this.isWorking = false;
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
