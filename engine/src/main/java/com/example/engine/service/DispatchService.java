package com.example.engine.service;

import com.example.engine.entity.Order;

public interface DispatchService {
    Order dispatchOrderToNearestRider(Long orderID);
}
