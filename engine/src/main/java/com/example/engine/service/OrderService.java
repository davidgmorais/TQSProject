package com.example.engine.service;

import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Order;
import com.example.engine.entity.Rider;

import java.util.List;

public interface OrderService {
    Order placeOrder(int contribId, OrderDTO orderToPlace);
    Order getOrderByI(Long orderID);
    Order saveOrder(Order order);
    Order getOrderInfoForContrib(Long orderId, String contribUsername);
    Order getCurrentOrderInfoForRider(String riderUsername);
    List<Order> getRidersOrderHistory(String riderUsername);
    List<Order> getContributorOrderHistory(String contributorUsername);
    Order updateCurrentOrderLocation(String riderUsername, Double latitude, Double longitude);
    Order updateCurrentOrderStatus(String riderUsername, String status);
    Order getOrderInfoForRider(Long orderId, String riderUsername);
    List<Order> getOrderQueue();
    Rider rateRider(int riderId, boolean riderThumbsUp);
    Contrib rateContrib(int contribId, boolean contribThumbsUp);
}
