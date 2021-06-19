package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

public class LocationDTO {
    @ApiModelProperty(notes = "Location latitude coordinate", required = true, example = "40.7")
    private final Double latitude;
    @ApiModelProperty(notes = "Location longitude coordinate", required = true, example = "-8.1")
    private final Double longitude;

    public LocationDTO(Double latitude, Double longitude) {
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
