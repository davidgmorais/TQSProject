package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

public class RatingDTO {
    @ApiModelProperty(notes = "Unique ID of the service to be rated", required = true, example = "8")
    private final int contribId;
    @ApiModelProperty(notes = "Rating to be given to the service, set to true when positive review and to false when negative review", required = true, example = "true")
    private final boolean contribThumbsUp;
    @ApiModelProperty(notes = "Unique ID of the rider to be rated", required = true, example = "2")
    private final int riderId;
    @ApiModelProperty(notes = "Rating to be given to the rider, set to true when positive review and to false when negative review", required = true, example = "false")
    private final boolean riderThumbsUp;

    public RatingDTO(int contribId, boolean contribThumbsUp, int riderId, boolean riderThumbsUp) {
        this.contribId = contribId;
        this.contribThumbsUp = contribThumbsUp;
        this.riderId = riderId;
        this.riderThumbsUp = riderThumbsUp;
    }

    public int getContribId() {
        return contribId;
    }

    public boolean isContribThumbsUp() {
        return contribThumbsUp;
    }

    public int getRiderId() {
        return riderId;
    }

    public boolean isRiderThumbsUp() {
        return riderThumbsUp;
    }
}
