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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

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
        jwt = jwt.replace("Bearer ", "");
        logger.info("{}", jwt);
        String contribUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized service {}", contribUsername);

        var order = orderService.placeOrder(contribUsername, orderToPlace);
        logger.info("order {}", order);

        return (order != null) ? new ResponseEntity<>(order, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
