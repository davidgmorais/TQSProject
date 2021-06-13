package com.example.engine.dto;

public class OrderDTO {
    private final Double value;
    private final Double pickupLat;
    private final Double pickupLon;
    private final Double deliveryLat;
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
