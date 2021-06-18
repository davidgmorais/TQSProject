package com.example.engine.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "Location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Location's unique ID automatically generated", required = true, example = "1")
    private Long id;

    @Column(name = "latitude", nullable = false)
    @ApiModelProperty(notes = "Location's latitude coordinate", required = true, example = "10.8", position = 1)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    @ApiModelProperty(notes = "Location's longitude coordinate", required = true, example = "-7.1", position = 2)
    private Double longitude;

    public Location() {}
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
