package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Order;
import com.example.engine.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/contributor/order")
    public ResponseEntity<Order> placeOrder(@RequestHeader(value = "Authorization") String jwt, @RequestBody OrderDTO orderToPlace) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String contribUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized service {}", contribUsername);

        var order = orderService.placeOrder(contribUsername, orderToPlace);
        logger.info("order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/contributor/order/{orderId}")
    public ResponseEntity<Order> getOrderStatus(@PathVariable Long orderId, @RequestHeader(value = "Authorization") String jwt) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String contribUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized service {}", contribUsername);

        var order = orderService.getOrderInfoForContrib(orderId, contribUsername);
        logger.info("order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // add get mapping to /contributor/order to return all orders history of a contributor

    @GetMapping("/rider/order/current")
    public ResponseEntity<Order> getRidersCurrentOrderStatus(@RequestHeader(value = "Authorization") String jwt) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized rider {}", riderUsername);

        var order = orderService.getCurrentOrderInfoForRider(riderUsername);
        logger.info("Order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rider/order/{orderId}")
    public ResponseEntity<Order> getRidersCurrentOrderStatus(@RequestHeader(value = "Authorization") String jwt, @PathVariable Long orderId) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Rider username {}", riderUsername);

        var order = orderService.getOrderInfoForRider(orderId, riderUsername);
        logger.info("Order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/rider/order/current")
    public ResponseEntity<Order> updateRidersCurrentOrderStatus(@RequestHeader(value = "Authorization") String jwt,
                                                                @RequestBody Map<String, String> status) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Rider username {}", riderUsername);

        String latitudeKey = "latitude";
        String longitudeKey = "longitude";
        String statusKey = "status";

        if (status.keySet().containsAll(Arrays.asList(latitudeKey, longitudeKey, statusKey))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Order order;
        if (status.keySet().containsAll(Arrays.asList(latitudeKey, longitudeKey))) {
            try {
                order = orderService.updateCurrentOrderLocation(
                        riderUsername, Double.parseDouble(status.get(latitudeKey)), Double.parseDouble(status.get(longitudeKey)));
                logger.info("location updated");
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } else if (status.containsKey(statusKey) && !(status.containsKey(latitudeKey) || status.containsKey(longitudeKey))) {
            order = orderService.updateCurrentOrderStatus(riderUsername, status.get(statusKey));
            logger.info("status (and consequently location) updated {}", order);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.ACCEPTED) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private String trimToken(String token) {
        return token.replace("Bearer ", "");
    }

}
