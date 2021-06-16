package com.example.engine.serviceUnitTests;

import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.*;
import com.example.engine.repository.LocationRepository;
import com.example.engine.repository.OrderRepository;
import com.example.engine.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderService_UnitTest {
    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private LocationRepository locationRepository;

    @Mock(lenient = true)
    private ContribServiceImpl contribService;

    @Mock(lenient = true)
    private RiderServiceImpl riderService;

    @Mock(lenient = true)
    private DispatchServiceImpl dispatchService;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        bob.setId(1);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        bobService.setVerified(true);

        Order order = new Order(20.0, bobService, new Location(42.72, -7.24));
        order.setId(1L);

        Rider rider = new Rider(bob);
        rider.setWorking(true);
        rider.setVerified(true);

        when(contribService.getContributorByUsername(bob.getUsername())).thenReturn(bobService);
        when(contribService.getContributorByUsername("NonExistingContributor")).thenReturn(null);

        when(riderService.getRidersToDispatch()).thenReturn(Collections.singletonList(rider));

        when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        when(orderRepository.findOrderById(-1L)).thenReturn(null);

        when(orderRepository.save(Mockito.any())).thenReturn(order);
    }

    @Test
    void whenGetOrderById_andOrderExists_thenReturnOrder() {
        Order found = orderService.getOrderByI(1L);
        assertThat(found.getValue()).isEqualTo(20.0);
        assertThat(found.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenGetOrderById_andOrderDoesNotExists_thenReturnNull() {
        Order found = orderService.getOrderByI(-1L);
        assertThat(found).isNull();
    }

    @Test
    void whenPlaceOrder_andContribIsValid_thenPlaceOrder() {
        OrderDTO orderDTO = new OrderDTO(20.0, 42.6, -7.1, 42.72, -7.24);
        Order orderPlaced = orderService.placeOrder("bob", orderDTO);
        System.out.println(orderPlaced);
        assertThat(orderPlaced).isNotNull().extracting(Order::getStatus).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenPlaceOrder_andContribIsInvalid_thenReturnNull() {
        OrderDTO orderDTO = new OrderDTO(20.0, 42.6, -7.1, 42.72, -7.24);
        Order orderPlaced = orderService.placeOrder("NonExistingContributor", orderDTO);
        assertThat(orderPlaced).isNull();
    }
}
