package com.example.engine.service;

import com.example.engine.entity.Order;
import com.example.engine.entity.OrderStatus;
import com.example.engine.entity.Rider;
import com.example.engine.repository.OrderRepository;
import com.example.engine.repository.RiderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DispatchServiceImpl implements DispatchService {
    private static final Logger logger = LoggerFactory.getLogger(DispatchServiceImpl.class);

    @Autowired
    OrderService orderService;

    @Autowired
    RiderService riderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RiderRepository riderRepository;


    @Override
    public Order dispatchOrderToNearestRider(Long orderID) {
        logger.info("Dispatching a rider for you, please wait...");
        var order = orderService.getOrderByI(orderID);
        if (order == null) {
            logger.error("Order {} cannot be found anywhere", orderID);
            return null;
        }

        List<Rider> availableRiders = riderService.getRidersToDispatch();
        if (availableRiders.isEmpty()) {
            logger.error("Seems that all riders are busy at the moment");
            return null;
        }

        var nearestAvailableRider = Collections.min(availableRiders, Comparator.comparing(rider ->
                Math.sqrt(Math.pow(Math.abs(rider.getLocation()[0] - order.getServiceLocation().getLatitude()), 2) + Math.pow(Math.abs(rider.getLocation()[1] - order.getServiceLocation().getLongitude()), 2))   // rider distance to pickup location
                        + Math.sqrt(Math.pow(Math.abs(order.getServiceLocation().getLatitude() - order.getDeliveryLocation().getLatitude()), 2) + Math.pow(Math.abs(order.getServiceLocation().getLongitude() - order.getDeliveryLocation().getLongitude()), 2))   // order's distance from pickup location to delivery location
        ));

        nearestAvailableRider = riderRepository.getRiderByUserId(nearestAvailableRider.getUser().getId());

        logger.info("Rider {} will pick up the order", nearestAvailableRider.getUser().getUsername());
        order.setPickupRider(nearestAvailableRider);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);
        return order;

    }
}
