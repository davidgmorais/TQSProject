package com.example.engine.repository;

import com.example.engine.entity.Contrib;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContribRepository extends JpaRepository<Contrib, Long> {
    Contrib findContribById(int contribId);
    List<Contrib> findAllByVerifiedTrue();
    List<Contrib> findAllByVerifiedFalse();
    Contrib getContribByUserId(int userId);
    List<Contrib> findContribByUserUsernameContainingIgnoreCase(String username);
    List<Contrib> findContribByStoreNameContainingIgnoreCase(String storeName);
}
