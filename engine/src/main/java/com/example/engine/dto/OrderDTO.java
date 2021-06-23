package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

public class OrderDTO {
    @ApiModelProperty(notes = "Price value of the order in euros.", required = true, example = "20")
    private final Double value;
    @ApiModelProperty(notes = "Latitude of the coordinates where the order should be picked up by a rider.", required = true, example = "40.7", position = 1)
    private final Double pickupLat;
    @ApiModelProperty(notes = "Longitude of the coordinates where the order should be picked up by a rider.", required = true, example = "-8.1", position = 2)
    private final Double pickupLon;
    @ApiModelProperty(notes = "Latitude of the coordinates where the order should be delivered to by a rider.", required = true, example = "41.2", position = 3)
    private final Double deliveryLat;
    @ApiModelProperty(notes = "Longitude of the coordinates where the order should be delivered to by a rider.", required = true, example = "-7.8", position = 4)
    private final Double deliveryLon;


    public OrderDTO(Double value, Double pickupLat, Double pickupLon, Double deliveryLat, Double deliveryLon) {
        this.value = value;
        this.deliveryLat = deliveryLat;
        this.deliveryLon = deliveryLon;

        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
    }


    public Double getValue() {
        return value;
    }

    public Double getDeliveryLat() {
        return deliveryLat;
    }

    public Double getDeliveryLon() {
        return deliveryLon;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLon() {
        return pickupLon;
    }
}
