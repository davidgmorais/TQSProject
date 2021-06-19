package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Order;
import com.example.engine.service.OrderService;
import io.swagger.annotations.*;
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
@Api( tags = "Order Manager")
@SwaggerDefinition(tags = {
        @Tag(name = "Order Manager", description = "Operations pertinent to the placement, update and list of orders in the engine.")
})
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;
    @Autowired
    JwtUtils jwtUtils;

    @ApiOperation(value = "Public endpoint for placing an order by the corresponding contributors id", response = Order.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Order was placed successfully."),
            @ApiResponse(code = 400, message = "The contributor does not exist or the order failed to be placed."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PostMapping("/order/{contribId}")
    public ResponseEntity<Order> placeOrder(
            @ApiParam(name = "Contributor's ID", value = "Unique ID of the contributor whose service you are using to place the order", required = true, example = "1") @PathVariable int contribId,
            @ApiParam(name = "Order", value = "Order to be placed and dispatched by the engine.", required = true) @RequestBody OrderDTO orderToPlace) {
        var order = orderService.placeOrder(contribId, orderToPlace);
        logger.info("order {}", order);
        return (order != null) ? new ResponseEntity<>(order, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Public endpoint for getting updates of the order status.", response = Order.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the order information."),
            @ApiResponse(code = 404, message = "The requested order does not exists.")
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrderInfo(
            @ApiParam(name = "Order's ID", value = "Unique ID of the order whose information is supposed to retrieve", required = true, example = "1") @PathVariable Long orderId) {
        var order = orderService.getOrderByI(orderId);
        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Endpoint for getting updates of the order status for contributors.", response = Order.class, notes = "This endpoint will only retrieve the order if the contributor mapped from the token is the Service Owner of said order.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the order information."),
            @ApiResponse(code = 404, message = "The requested order does not exists or the service is not the owner of the order.")
    })
    @GetMapping("/contributor/order/{orderId}")
    public ResponseEntity<Order> getOrderStatus(
            @ApiParam(name = "Order's ID", value = "Unique ID of the order whose information is supposed to retrieve", required = true, example = "1") @PathVariable Long orderId,
            @ApiParam(name = "Authorization", value = "JWT token used for authentication and to fetch the corresponding contributor's username.", required = true, example = "Bearer ContributorJWTTokenString") @RequestHeader(value = "Authorization") String jwt) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String contribUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized service {}", contribUsername);

        var order = orderService.getOrderInfoForContrib(orderId, contribUsername);
        logger.info("order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // add get mapping to /contributor/order to return all orders history of a contributor

    @ApiOperation(value = "Endpoint for getting the status of the current order of a rider.", response = Order.class, notes = "If the rider doesn't have a current order, then it will return a 404 response.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the rider's current order information."),
            @ApiResponse(code = 404, message = "The rider does not exist or does is not distributing any orders.")
    })
    @GetMapping("/rider/order/current")
    public ResponseEntity<Order> getRidersCurrentOrderStatus(
            @ApiParam(name = "Authorization", value = "JWT token used for authentication and to fetch the corresponding rider's username.", required = true, example = "Bearer RiderJWTTokenString") @RequestHeader(value = "Authorization") String jwt) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized rider {}", riderUsername);

        var order = orderService.getCurrentOrderInfoForRider(riderUsername);
        logger.info("Order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Endpoint for getting information about an order for the riders.", response = Order.class, notes = "This endpoint will only retrieve the order if the rider mapped from the token is the Pickup Rider of said order.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the order information."),
            @ApiResponse(code = 404, message = "The requested order does not exists or the rider is not the pickup rider of the order.")
    })
    @GetMapping("/rider/order/{orderId}")
    public ResponseEntity<Order> getRidersCurrentOrderStatus(
            @ApiParam(name = "Authorization", value = "JWT token used for authentication and to fetch the corresponding rider's username.", required = true, example = "Bearer RiderJWTTokenString") @RequestHeader(value = "Authorization") String jwt,
            @ApiParam(name = "Order's ID", value = "Unique ID of the order whose information is supposed to retrieve", required = true, example = "1") @PathVariable Long orderId) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Rider username {}", riderUsername);

        var order = orderService.getOrderInfoForRider(orderId, riderUsername);
        logger.info("Order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Endpoint for updating the locations or status of the rider's current order.", response = Order.class, notes = "This endpoint either receives the current location of a rider in order to update the order location, or the order status to which update and updates the rider location accordingly.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Order status was updated successfully."),
            @ApiResponse(code = 400, message = "Invalid parameters. Parameter should either be 'status' - BEING_DELIVERED or DELIVERED - or a pair 'latitude' and 'longitude'."),
            @ApiResponse(code = 404, message = "The requested order does not exists or the rider is not the pickup rider of the order.")
    })
    @PutMapping("/rider/order/current")
    public ResponseEntity<Order> updateRidersCurrentOrderStatus(
            @ApiParam(name = "Authorization", value = "JWT token used for authentication and to fetch the corresponding rider's username.", required = true, example = "Bearer RiderJWTTokenString") @RequestHeader(value = "Authorization") String jwt,
            @ApiParam(name = "Status", value = "Values to update the order according to", required = true) @RequestBody Map<String, String> status) {
        jwt = this.trimToken(jwt);
        logger.info("{}", jwt);
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Rider username {}", riderUsername);

        var latitudeKey = "latitude";
        var longitudeKey = "longitude";
        var statusKey = "status";

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
