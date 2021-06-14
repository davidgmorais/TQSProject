package com.example.engine.serviceUnitTests;

import com.example.engine.entity.*;
import com.example.engine.repository.OrderRepository;
import com.example.engine.repository.RiderRepository;
import com.example.engine.service.DispatchServiceImpl;
import com.example.engine.service.OrderServiceImpl;
import com.example.engine.service.RiderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatcherService_UnitTest {

    @Mock(lenient = true)
    private OrderServiceImpl orderService;

    @Mock(lenient = true)
    private RiderServiceImpl riderService;

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private RiderRepository riderRepository;

    @InjectMocks
    private DispatchServiceImpl dispatchService;


    @BeforeEach
    void setUp() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        bob.setId(1);

        Contrib bobService = new Contrib(bob, "Bob's service");

//        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
//        dakota.setId(2);
//        Rider riderDakota = new Rider(dakota);
//        riderDakota.setId(2);
//        riderDakota.setVerified(true);
//        riderDakota.setWorking(true);

        Order order = new Order(20.0, bobService, new Location(42.72, -7.24));
        order.setServiceLocation(new Location(40.0, -7.0));
        order.setId(1L);

        when(orderService.getOrderByI(1L)).thenReturn(order);
        when(orderService.getOrderByI(-1L)).thenReturn(null);
        when(orderRepository.save(Mockito.any())).thenReturn(null);
    }

    @Test
    void whenDispatchingOrder_andInvalidOrderId_thenReturnNull() {
        Order dispatchedOrder = dispatchService.dispatchOrderToNearestRider(-1L);
        assertThat(dispatchedOrder).isNull();
    }

    @Test
    void whenDispatchingOrder_andNoAvailableRider_thenReturnNull() {
        when(riderService.getRidersToDispatch()).thenReturn(new ArrayList<>());
        Order dispatchedOrder = dispatchService.dispatchOrderToNearestRider(1L);
        assertThat(dispatchedOrder).isNull();
    }

    @Test
    void whenDispatchingOrder_andValidOrder_andOneAvailableRiders_thenReturnOrder() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        bob.setId(1);
        Rider riderBob = new Rider(bob);
        riderBob.setId(3);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        when(riderService.getRidersToDispatch()).thenReturn(Collections.singletonList(riderBob));
        when(riderRepository.getRiderByUserId(1)).thenReturn(riderBob);

        Order dispatchedOrder = dispatchService.dispatchOrderToNearestRider(1L);
        assertThat(dispatchedOrder).isNotNull().extracting(Order::getStatus).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(dispatchedOrder).isNotNull().extracting(Order::getPickupRider).isEqualTo(riderBob);
    }



}
