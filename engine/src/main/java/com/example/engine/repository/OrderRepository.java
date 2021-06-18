package com.example.engine.repository;

import com.example.engine.entity.Order;
import com.example.engine.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(Long id);
    List<Order> findAllByPickupRiderUserUsername(String riderUsername);
    Order findOrderByPickupRiderUserUsernameAndStatusIn(String riderUsername, Set<OrderStatus> orderStatuses);
    List<Order> findOrdersByPickupRiderIsNullOrderById();
}
