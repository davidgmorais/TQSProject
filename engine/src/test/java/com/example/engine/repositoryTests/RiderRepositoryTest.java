package com.example.engine.repositoryTests;

import com.example.engine.entity.*;
import com.example.engine.repository.OrderRepository;
import com.example.engine.repository.RiderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RiderRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void whenFindingOrderById_andOrderExists_thenReturnOrder() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "service");
        Location location = new Location(0.0, 0.0);
        Order order = new Order(20.0, bobService, location);
        order.setServiceLocation(location);

        entityManager.persist(bob);
        entityManager.persist(bobService);
        entityManager.persist(location);
        entityManager.persist(order);
        entityManager.flush();

        Order found = orderRepository.findOrderById(order.getId());
        assertThat(found).isEqualTo(order);
    }

    @Test
    void whenFindingOrderById_andOrderDoesNotExists_thenReturnNull() {
        Order found = orderRepository.findOrderById(1L);
        assertThat(found).isNull();
    }

    @Test
    void whenGettingRidersToDispatch_andWorkingRiders_andAvailableRiders_thenReturnRiderList() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        riderBob.setWorking(true);
        Contrib bobService = new Contrib(bob, "service");
        Location location = new Location(0.0, 0.0);
        Order order = new Order(20.0, bobService, location);
        order.setServiceLocation(location);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.persist(bobService);
        entityManager.persist(location);
        entityManager.persist(order);
        entityManager.flush();

        List<Rider> list = riderRepository.findRidersToDispatch();
        assertThat(list).hasSize(1).extracting(Rider::getUser).extracting(User::getUsername).containsOnly(bob.getUsername());
    }

    @Test
    void whenGettingRidersToDispatch_andWorkingRiders_andUnavailableRiders_thenReturnEmptyList() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        riderBob.setWorking(true);
        Contrib bobService = new Contrib(bob, "service");
        Location location = new Location(0.0, 0.0);
        Order order = new Order(20.0, bobService, location);
        order.setServiceLocation(location);
        order.setPickupRider(riderBob);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.persist(bobService);
        entityManager.persist(location);
        entityManager.persist(order);
        entityManager.flush();

        List<Rider> list = riderRepository.findRidersToDispatch();
        assertThat(list).isEmpty();
    }

    @Test
    void whenGettingRidersToDispatch_andNoWorkingRiders_andAvailableRiders_thenReturnEmptyList() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        Contrib bobService = new Contrib(bob, "service");
        Location location = new Location(0.0, 0.0);
        Order order = new Order(20.0, bobService, location);
        order.setServiceLocation(location);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.persist(bobService);
        entityManager.persist(location);
        entityManager.persist(order);
        entityManager.flush();

        List<Rider> list = riderRepository.findRidersToDispatch();
        assertThat(list).isEmpty();
    }

    @Test
    void whenGettingRidersToDispatch_andNoWorkingRiders_andNoAvailableRiders_thenReturnEmptyList() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        Contrib bobService = new Contrib(bob, "service");
        Location location = new Location(0.0, 0.0);
        Order order = new Order(20.0, bobService, location);
        order.setServiceLocation(location);
        order.setPickupRider(riderBob);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.persist(bobService);
        entityManager.persist(location);
        entityManager.persist(order);
        entityManager.flush();

        List<Rider> list = riderRepository.findRidersToDispatch();
        assertThat(list).isEmpty();
    }

    @Test
    void whenFindRiderById_andValidID_thenReturnRider() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.flush();

        Rider found = riderRepository.findRiderById(riderBob.getId());
        assertThat(found).isEqualTo(riderBob);
    }

    @Test
    void whenFindRiderById_andInvalidID_thenReturnNull() {
        Rider fromDb = riderRepository.findRiderById(2);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenGetRiderByUser_andRiderExists_thenReturnRider() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.flush();

        Rider found = riderRepository.getRiderByUserId(bob.getId());
        assertThat(found).isEqualTo(riderBob);
    }

    @Test
    void whenGetRiderByUser_andRiderDoesNotExists_thenReturnNull() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        entityManager.persist(bob);

        Rider found = riderRepository.getRiderByUserId(bob.getId());
        assertThat(found).isNull();
    }

    @Test
    void whenGetRiderByUsernamme_andRiderExists_thenReturnRider() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.flush();

        Rider found = riderRepository.getRiderByUserUsername(bob.getUsername());
        assertThat(found).isEqualTo(riderBob);
    }

    @Test
    void whenGetRiderByUsernname_andRiderDoesNotExists_thenReturnNull() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        entityManager.persist(bob);

        Rider found = riderRepository.getRiderByUserUsername(bob.getUsername());
        assertThat(found).isNull();
    }

    @Test
    void givenRiders_whenFindAllVerifiedRiders_returnListOfRiders() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        entityManager.persist(bob);
        entityManager.persist(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Rider riderDakota = new Rider(dakota);
        entityManager.persist(dakota);
        entityManager.persist(riderDakota);
        entityManager.flush();

        List<Rider> allVerifiedRiders = riderRepository.findAllByVerifiedTrue();
        assertThat(allVerifiedRiders).hasSize(1).extracting(Rider::getUser)
                .containsOnly(bob);
    }

    @Test
    void givenNoRiders_whenFindAllVerifiedRiders_returnEmptyList() {
        List<Rider> allVerifiedRiders = riderRepository.findAllByVerifiedTrue();
        assertThat(allVerifiedRiders).isEmpty();
    }

    @Test
    void givenRiders_whenFindAllNonVerifiedRiders_returnListOfRiders() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        entityManager.persist(bob);
        entityManager.persist(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Rider riderDakota = new Rider(dakota);
        entityManager.persist(dakota);
        entityManager.persist(riderDakota);
        entityManager.flush();

        List<Rider> allVerifiedRiders = riderRepository.findAllByVerifiedFalse();
        assertThat(allVerifiedRiders).hasSize(1).extracting(Rider::getUser)
                .containsOnly(dakota);
    }

    @Test
    void givenNoRiders_whenFindAllNonVerifiedRiders_returnEmptyList() {
        List<Rider> allVerifiedRiders = riderRepository.findAllByVerifiedFalse();
        assertThat(allVerifiedRiders).isEmpty();
    }

    @Test
    void whenFindRiderByUserUsername_andMatchingUsername_thenListVerifiedContributors() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        User bob27 = new User("bob27", "bob@gmail.com", "12345", "Bob", null, 2);
        Rider riderBob27 = new Rider(bob27);
        riderBob27.setVerified(true);
        User dakota = new User("dakotaB", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Rider riderDakota = new Rider(dakota);

        entityManager.persist(bob);
        entityManager.persist(riderBob);
        entityManager.persist(bob27);
        entityManager.persist(riderBob27);
        entityManager.persist(dakota);
        entityManager.persist(riderDakota);
        entityManager.flush();

        List<Rider> found = riderRepository.findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase("b");
        assertThat(found).hasSize(2).extracting(Rider::getUser)
                .contains(bob, bob27);
    }

    @Test
    void whenFindRiderByUserUsername_andNoMatchingUsername_thenReturnEmptyList() {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Rider riderDakota = new Rider(dakota);

        entityManager.persist(dakota);
        entityManager.persist(riderDakota);
        entityManager.flush();

        List<Rider> found = riderRepository.findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase("bob");
        assertThat(found).isEmpty();
    }

}
