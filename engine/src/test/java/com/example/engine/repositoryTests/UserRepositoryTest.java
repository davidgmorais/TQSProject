package com.example.engine.repositoryTests;

import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindUserByUsername_andUsernameExists_thenReturnUser() {
        User admin = new User("admin", "admin@email.pt", "adminPassword", null, null, 0);
        entityManager.persistAndFlush(admin);

        User fromDb = userRepository.findUserByUsername(admin.getUsername());
        assertThat(fromDb).isEqualTo(admin);
    }

    @Test
    void whenFindUserByUsername_andUsernameDoesNotExists_thenReturnNull() {
        User fromDb = userRepository.findUserByUsername("Not Existing Username");
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindUserByEmail_andEmailExists_thenReturnUser() {
        User admin = new User("admin", "admin@email.pt", "adminPassword", null, null, 0);
        entityManager.persistAndFlush(admin);

        User fromDb = userRepository.findUserByEmail(admin.getEmail());
        assertThat(fromDb).isEqualTo(admin);
    }

    @Test
    void whenFindUserByEmail_andEmailDoesNotExists_thenReturnNull() {
        User fromDb = userRepository.findUserByEmail("notExisting@gmail.com");
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindUserId_andValidID_thenReturnUser() {
        User admin = new User("admin", "admin@email.pt", "adminPassword", null, null, 0);
        entityManager.persistAndFlush(admin);

        User fromDb = userRepository.findUserById(admin.getId());
        assertThat(fromDb).isEqualTo(admin);
    }

    @Test
    void whenFindUserById_andInvalidId_thenReturnNull() {
        User fromDb = userRepository.findUserById(-1);
        assertThat(fromDb).isNull();
    }

}