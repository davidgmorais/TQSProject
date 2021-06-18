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
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
        bobService.setId(1);
        bobService.setVerified(true);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        dakota.setId(2);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setId(2);
        dakotaService.setVerified(true);

        Rider riderBob = new Rider(bob);
        riderBob.setWorking(true);
        riderBob.setVerified(true);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setWorking(true);
        riderDakota.setVerified(true);

        Order order = new Order(20.0, bobService, new Location(42.72, -7.24));
        order.setId(1L);
        order.setPickupRider(riderBob);

        Order order2 = new Order(10.0, bobService, new Location(40.7, -7.29));
        order2.setId(2L);

        Order order3 = new Order(80.50, bobService, new Location(41.0, -7.0));
        order3.setId(3L);

        when(contribService.getContributorByUsername(bob.getUsername())).thenReturn(bobService);
        when(contribService.getContributorByUsername(dakota.getUsername())).thenReturn(dakotaService);
        when(contribService.getContributorByUsername("NonExistingContributor")).thenReturn(null);
        when(contribService.getContributorById(bobService.getId())).thenReturn(bobService);
        when(contribService.getContributorById(dakotaService.getId())).thenReturn(dakotaService);
        when(contribService.getContributorById(-1)).thenReturn(null);

        when(riderService.getRidersToDispatch()).thenReturn(Collections.singletonList(riderBob));
        when(riderService.getRiderByUsername(bob.getUsername())).thenReturn(riderBob);
        when(riderService.getRiderByUsername(dakota.getUsername())).thenReturn(riderDakota);
        when(riderService.getRiderByUsername("NonExistingRider")).thenReturn(null);

        when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        when(orderRepository.findOrderByPickupRiderUserUsernameAndStatusIn(bob.getUsername(), new HashSet<>(Arrays.asList(OrderStatus.ASSIGNED, OrderStatus.BEING_DELIVERED)))).thenReturn(order);
        when(orderRepository.findOrderByPickupRiderUserUsernameAndStatusIn(dakota.getUsername(), new HashSet<>(Arrays.asList(OrderStatus.ASSIGNED, OrderStatus.BEING_DELIVERED)))).thenReturn(null);
        when(orderRepository.findOrderById(-1L)).thenReturn(null);
        when(orderRepository.findAllByPickupRiderUserUsername(bob.getUsername())).thenReturn(Collections.singletonList(order));
        when(orderRepository.findAllByPickupRiderUserUsername("NonExistingRider")).thenReturn(new ArrayList<>());
        when(orderRepository.save(Mockito.any())).thenReturn(order);
        when(orderRepository.findOrdersByPickupRiderIsNullOrderById()).thenReturn(new ArrayList<>(Arrays.asList(order2, order3)));
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
        Order orderPlaced = orderService.placeOrder(1, orderDTO);
        System.out.println(orderPlaced);
        assertThat(orderPlaced).isNotNull().extracting(Order::getStatus).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenPlaceOrder_andContribIsInvalid_thenReturnNull() {
        OrderDTO orderDTO = new OrderDTO(20.0, 42.6, -7.1, 42.72, -7.24);
        Order orderPlaced = orderService.placeOrder(-1, orderDTO);
        assertThat(orderPlaced).isNull();
    }

    @Test
    void whenGetOrderInfoForContrib_andContribIsInvalid_returnNull() {
        Order orderInfo = orderService.getOrderInfoForContrib(1L, "NonExistingContributor");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForContrib_andOrderIsInvalid_returnNull() {
        Order orderInfo = orderService.getOrderInfoForContrib(-1L, "bob");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForContrib_andContribDoesNotMatchTheOwner_returnNull() {
        Order orderInfo = orderService.getOrderInfoForContrib(1L, "dakota");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForContrib_andValidContrib_andValidOrder_andContribMatchTheOwner_returnOrder() {
        Order orderInfo = orderService.getOrderInfoForContrib(1L, "bob");
        assertThat(orderInfo).isNotNull();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenGetCurrentOrderInfoForRider_andRiderIsInvalid_returnNull() {
        Order orderInfo = orderService.getCurrentOrderInfoForRider("NonExistingRider");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetCurrentOrderInfoForRider_andRiderDoesNotMatchThePickupRider_orOrderIsInvalid_returnNull() {
        Order orderInfo = orderService.getCurrentOrderInfoForRider("dakota");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetCurrentOrderInfoForRider_andValidRider_andValidOrder_andRiderMatchesPickupRider_returnOrder() {
        Order orderInfo = orderService.getCurrentOrderInfoForRider("bob");
        assertThat(orderInfo).isNotNull();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenUpdateCurrentOrderLocation_andInvalidRider_returnNull() {
        Order orderInfo = orderService.updateCurrentOrderLocation("NonExistingRider", 0.0, 0.0);
        assertThat(orderInfo).isNull();
        Mockito.verify(riderService, VerificationModeFactory.times(1))
                .getRiderByUsername(Mockito.anyString());
    }

    @Test
    void whenUpdateCurrentOrderLocation_andValidRider_returnOrder() {
        Order orderInfo = orderService.updateCurrentOrderLocation("bob", 0.0, 0.0);
        assertThat(orderInfo).isNotNull();
        Mockito.verify(riderService, VerificationModeFactory.times(2))
                .getRiderByUsername(Mockito.anyString());
    }

    @Test
    void whenUpdateCurrentOrderStatus_andInvalidRider_returnNull() {
        Order orderInfo = orderService.updateCurrentOrderStatus("NonExistingRider", OrderStatus.DELIVERED.toString());
        assertThat(orderInfo).isNull();
        Mockito.verify(riderService, VerificationModeFactory.times(1))
                .getRiderByUsername(Mockito.anyString());
    }

    @Test
    void whenUpdateCurrentOrderStatus_anValidRider_returnOrder() {
        Order orderInfo = orderService.updateCurrentOrderStatus("bob", OrderStatus.DELIVERED.toString());
        assertThat(orderInfo).isNotNull();
        Mockito.verify(riderService, VerificationModeFactory.times(2))
                .getRiderByUsername(Mockito.anyString());
    }

    @Test
    void whenGetRidesOrderHistory_andRiderIsInvalid_returnNull() {
        List<Order> ordersHistory = orderService.getRidersOrderHistory("NonExistingRider");
        assertThat(ordersHistory).isEmpty();
        Mockito.verify(riderService, VerificationModeFactory.times(1))
                .getRiderByUsername(Mockito.anyString());
    }

    @Test
    void whenGetRidesOrderHistory_andRiderIsValid_returnOrderHistory() {
        List<Order> ordersHistory = orderService.getRidersOrderHistory("bob");
        assertThat(ordersHistory).hasSize(1);
    }

    @Test
    void whenGetRidesOrderHistory_andRiderIInvValid_returnEmptyList() {
        List<Order> ordersHistory = orderService.getRidersOrderHistory("NonExistingRider");
        assertThat(ordersHistory).isEmpty();
    }

    @Test
    void whenGetOrderInfoForRider_andRiderIsInvalid_returnNull() {
        Order orderInfo = orderService.getOrderInfoForRider(1L, "NonExistingRider");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForRider_andOrderIsInvalid_returnNull() {
        Order orderInfo = orderService.getOrderInfoForRider(-1L, "bob");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForRider_andRiderDoesNotMatchThePickupRider_returnNull() {
        Order orderInfo = orderService.getOrderInfoForRider(1L, "dakota");
        assertThat(orderInfo).isNull();
    }

    @Test
    void whenGetOrderInfoForRider_andValidRider_andValidOrder_andRiderMatchThePickupRider_returnOrder() {
        Order orderInfo = orderService.getOrderInfoForRider(1L, "bob");
        assertThat(orderInfo).isNotNull();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    void whenGetQueueOrder_thenReturnListOfOrders() {
        List<Order> queue = orderService.getOrderQueue();
        assertThat(queue).hasSize(2).extracting(Order::getId).containsExactly(2L, 3L);
    }

}
