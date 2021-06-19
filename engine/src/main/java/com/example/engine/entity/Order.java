package com.example.engine.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "Package")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Order's unique ID automatically generated", required = true, example = "1")
    private Long id;

    @Column(name = "price", nullable = false)
    @ApiModelProperty(notes = "Order's price in Euros", required = true, example = "20", position = 1)
    private Double value;

    @OneToOne
    @JoinColumn(name = "Rider_id")
    @ApiModelProperty(notes = "Pickup rider's ID responsible for the delivery", position = 2)
    private Rider pickupRider;

    @OneToOne
    @JoinColumn(name = "Contrib_id", nullable = false)
    @ApiModelProperty(notes = "Contributor's ID from where the order is dispatched", required = true, position = 3)
    private Contrib serviceOwner;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Current status of the package - it can take the values of WAITING, ASSIGNED, BEING_DELIVERED and DELIVERED", position = 4)
    private OrderStatus status;

    @OneToOne
    @JoinColumn(name = "service_location_id", nullable = false)
    @ApiModelProperty(notes = "Location from where the order should be picked up from", required = true, position = 5)
    private Location serviceLocation;

    @OneToOne
    @JoinColumn(name = "delivery_location_id", nullable = false)
    @ApiModelProperty(notes = "Location where the order should be delivered to", required = true, position = 6)
    private Location deliveryLocation;

    public Order() {}
    public Order(Double value, Contrib serviceOwner, Location deliveryLocation) {
        this.value = value;
        this.status = OrderStatus.WAITING;
        this.serviceOwner = serviceOwner;
        this.deliveryLocation = deliveryLocation;
    }

    public Long getId() {
        return id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Rider getPickupRider() {
        return pickupRider;
    }

    public void setPickupRider(Rider pickupRider) {
        this.pickupRider = pickupRider;
    }

    public Contrib getServiceOwner() {
        return serviceOwner;
    }

    public void setServiceOwner(Contrib serviceOwner) {
        this.serviceOwner = serviceOwner;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Location getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(Location serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(Location deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
