package com.example.engine.service;

import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Location;
import com.example.engine.entity.Order;
import com.example.engine.entity.OrderStatus;
import com.example.engine.repository.LocationRepository;
import com.example.engine.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService{
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ContribServiceImpl contribService;

    @Autowired
    RiderServiceImpl riderService;

    @Autowired
    DispatchService dispatchService;

    @Override
    public Order placeOrder(int contribId, OrderDTO orderToPlace) {
        var contributor = contribService.getContributorById(contribId);
        logger.info("Contributor found {}", contributor);
        if (contributor != null) {
            logger.info("Starting Order with value {}€ to be delivered at {}, {}...", orderToPlace.getValue(), orderToPlace.getDeliveryLat(), orderToPlace.getDeliveryLon());

            var deliveryLocation = new Location(orderToPlace.getDeliveryLat(), orderToPlace.getDeliveryLon());
            locationRepository.save(deliveryLocation);
            var pickUpLocation = new Location(orderToPlace.getPickupLat(), orderToPlace.getPickupLon());
            locationRepository.save(pickUpLocation);

            var order = new Order();
            order.setValue(orderToPlace.getValue());
            order.setDeliveryLocation(deliveryLocation);
            order.setServiceLocation(pickUpLocation);
            order.setServiceOwner(contributor);
            order.setStatus(OrderStatus.WAITING);
            order = orderRepository.save(order);
            var dispatchedOrder = dispatchService.dispatchOrderToNearestRider(order.getId());
            return (dispatchedOrder != null) ? dispatchedOrder : order;
        }
        return null;
    }


    @Override
    public Order getOrderByI(Long orderID) {
        return orderRepository.findOrderById(orderID);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderInfoForContrib(Long orderId, String contribUsername) {
        var contrib = contribService.getContributorByUsername(contribUsername);
        if (contrib == null) {
            return null;
        }

        var order = getOrderByI(orderId);
        if (order == null) {
            return null;
        }

        return (order.getServiceOwner() == contrib) ? order : null;
    }

    @Override
    public Order getOrderInfoForRider(Long orderId, String riderUsername) {
        var rider = riderService.getRiderByUsername(riderUsername);
        if (rider == null) {
            return null;
        }

        var order = getOrderByI(orderId);
        if (order == null) {
            return null;
        }
        return (order.getPickupRider() == rider) ? order : null;
    }

    @Override
    public List<Order> getOrderQueue() {
        return orderRepository.findOrdersByPickupRiderIsNullOrderById();
    }

    @Override
    public Order getCurrentOrderInfoForRider(String riderUsername) {
        var rider = riderService.getRiderByUsername(riderUsername);
        if (rider == null) {
            return null;
        }

        return orderRepository.findOrderByPickupRiderUserUsernameAndStatusIn(
                rider.getUser().getUsername(),
                new HashSet<>(Arrays.asList(OrderStatus.ASSIGNED, OrderStatus.BEING_DELIVERED)));
    }

    @Override
    public List<Order> getRidersOrderHistory(String riderUsername) {
        var rider = riderService.getRiderByUsername(riderUsername);
        if (rider == null) {
            return new ArrayList<>();
        }

        return orderRepository.findAllByPickupRiderUserUsername(rider.getUser().getUsername());
    }

    @Override
    public Order updateCurrentOrderLocation(String riderUsername, Double latitude, Double longitude) {
        var rider = riderService.getRiderByUsername(riderUsername);
        if (rider == null) {
            return null;
        }
        rider.setLocation(latitude, longitude);
        riderService.save(rider);
        return this.getCurrentOrderInfoForRider(riderUsername);
    }

    @Override
    public Order updateCurrentOrderStatus(String riderUsername, String status) {
        var rider = riderService.getRiderByUsername(riderUsername);
        if (rider == null) {
            return null;
        }
        var order = this.getCurrentOrderInfoForRider(riderUsername);

        switch (status.toUpperCase(Locale.ROOT)) {
            case "BEING_DELIVERED":
                rider.setLocation(order.getServiceLocation().getLatitude(), order.getServiceLocation().getLongitude());
                order.setStatus(OrderStatus.BEING_DELIVERED);
                break;
            case "DELIVERED":
                rider.setLocation(order.getDeliveryLocation().getLatitude(), order.getDeliveryLocation().getLongitude());
                order.setStatus(OrderStatus.DELIVERED);
                dispatchService.dispatchNextOrderInQueue(rider.getUser().getUsername());
                break;
            default:
                return null;
        }
        riderService.save(rider);
        return orderRepository.save(order);
    }

}
