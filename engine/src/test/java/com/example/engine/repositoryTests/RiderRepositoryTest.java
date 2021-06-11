package com.example.engine.repositoryTests;

import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RiderRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RiderRepository riderRepository;

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

}
