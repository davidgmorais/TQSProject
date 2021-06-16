package com.example.engine.service;

import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Order;

public interface OrderService {
    Order placeOrder(String contribUsername, OrderDTO orderToPlace);
    Order getOrderByI(Long orderID);
    Order saveOrder(Order order);
}
