package com.example.engine.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Package")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "price", nullable = false)
    private Double value;

    @Column(name = "tax")
    private Double tax;

    @OneToOne
    @JoinColumn(name = "Rider_id")
    private Rider pickupRider;

    @OneToOne
    @JoinColumn(name = "Contrib_id", nullable = false)
    private Contrib serviceOwner;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne
    @JoinColumn(name = "service_location_id", nullable = false)
    private Location serviceLocation;

    @OneToOne
    @JoinColumn(name = "delivery_location_id", nullable = false)
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

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
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
}
