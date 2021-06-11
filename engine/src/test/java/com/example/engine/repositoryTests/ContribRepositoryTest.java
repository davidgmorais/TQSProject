package com.example.engine.repositoryTests;

import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContribRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContribRepository contribRepository;

    @Test
    void whenFindContribById_andValidID_thenReturnContrib() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob Service");

        entityManager.persist(bob);
        entityManager.persist(bobService);
        entityManager.flush();

        Contrib found = contribRepository.findContribById(bobService.getId());
        assertThat(found).isEqualTo(bobService);
    }

    @Test
    void whenFindContribById_andInvalidID_thenReturnNull() {
        Contrib fromDb = contribRepository.findContribById(2);
        assertThat(fromDb).isNull();
    }

    @Test
    void givenContributors_whenFindAllVerifiedContributors_returnListOfAllContributors() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        bobService.setVerified(true);
        entityManager.persist(bob);
        entityManager.persist(bobService);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> allVerifiedContributors = contribRepository.findAllByVerifiedTrue();
        assertThat(allVerifiedContributors).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly(bobService.getStoreName());
    }

    @Test
    void givenNoContributors_whenFindAllVerifiedContributors_returnEmptyList() {
        List<Contrib> allVerifiedContributors = contribRepository.findAllByVerifiedTrue();
        assertThat(allVerifiedContributors).isEmpty();
    }

    @Test
    void givenContributors_whenFindAllNonVerifiedContributors_returnListOfAllContributors() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        bobService.setVerified(true);
        entityManager.persist(bob);
        entityManager.persist(bobService);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");
        dakotaService.setStoreName("Dakota's Service");
        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> allVerifiedContributors = contribRepository.findAllByVerifiedFalse();
        assertThat(allVerifiedContributors).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly(dakotaService.getStoreName());
    }

    @Test
    void givenNoContributors_whenFindAllNonVerifiedContributors_returnEmptyList() {
        List<Contrib> allVerifiedContributors = contribRepository.findAllByVerifiedTrue();
        assertThat(allVerifiedContributors).isEmpty();
    }

    @Test
    void whenGetContributorByUser_andContributorExists_thenReturnContributor() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Service");

        entityManager.persist(bob);
        entityManager.persist(bobService);
        entityManager.flush();

        Contrib found = contribRepository.getContribByUserId(bob.getId());
        assertThat(found).isEqualTo(bobService);
    }

    @Test
    void whenGetContributorByUser_andContributorDoesNotExists_thenReturnNull() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        entityManager.persist(bob);

        Contrib found = contribRepository.getContribByUserId(bob.getId());
        assertThat(found).isNull();
    }

    @Test
    void whenFindContribByUserUsername_andMatchingUsernames_thenListContributors() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob Service");
        bobService.setVerified(true);
        User bob27 = new User("bob27", "bob@gmail.com", "12345", "Bob", null, 2);
        Contrib bob27Service = new Contrib(bob27, "Bob Shop");
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");
        dakotaService.setVerified(true);

        entityManager.persist(bob);
        entityManager.persist(bobService);
        entityManager.persist(bob27);
        entityManager.persist(bob27Service);
        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> found = contribRepository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase("bob");
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly(bobService.getStoreName());
    }

    @Test
    void whenFindContribByUserUsername_andNoMatchingUsernames_thenReturnEmptyList() {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");

        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> found = contribRepository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase("bob");
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindContribByServiceName_andMatchingServiceNames_thenListContributors() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob Service");
        bobService.setVerified(true);
        User bob27 = new User("bob27", "bob@gmail.com", "12345", "Bob", null, 2);
        Contrib bob27Service = new Contrib(bob27, "Bob Shop");
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");
        dakotaService.setVerified(true);

        entityManager.persist(bob);
        entityManager.persist(bobService);
        entityManager.persist(bob27);
        entityManager.persist(bob27Service);
        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> found = contribRepository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase("Bob");
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly(bobService.getStoreName());

        found = contribRepository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase("bob");
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly(bobService.getStoreName());
    }

    @Test
    void whenFindContribByStoreName_andNoMatchingStoreNames_thenReturnEmptyList() {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");

        entityManager.persist(dakota);
        entityManager.persist(dakotaService);
        entityManager.flush();

        List<Contrib> found = contribRepository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase("bob");
        assertThat(found).isEmpty();
    }

}
