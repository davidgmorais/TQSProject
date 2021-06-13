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

@Service
public class OrderServiceImpl implements OrderService{
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ContribServiceImpl contribService;

    @Override
    public Order placeOrder(String contribUsername, OrderDTO orderToPlace) {
        var contributor = contribService.getContributorByUsername(contribUsername);
        logger.info("Contributor found {}", contributor);
        if (contributor != null) {
            logger.info("Starting Order with value {}€ to be delivered at {}, {}...", orderToPlace.getValue(), orderToPlace.getDeliveryLat(), orderToPlace.getDeliveryLon());

            Location deliveryLocation = new Location(orderToPlace.getDeliveryLat(), orderToPlace.getDeliveryLon());
            locationRepository.save(deliveryLocation);
            Location pickUpLocation = new Location(orderToPlace.getPickupLat(), orderToPlace.getPickupLon());
            locationRepository.save(pickUpLocation);

            Order order = new Order();
            order.setValue(orderToPlace.getValue());
            order.setDeliveryLocation(deliveryLocation);
            order.setServiceLocation(pickUpLocation);
            order.setServiceOwner(contributor);
            order.setStatus(OrderStatus.WAITING);
            return orderRepository.save(order);
        }
        return null;
    }
}
