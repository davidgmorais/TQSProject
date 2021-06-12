package com.example.engine.repositoryTests;

import com.example.engine.entity.Contrib;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
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
