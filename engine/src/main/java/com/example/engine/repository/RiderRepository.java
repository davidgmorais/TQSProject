package com.example.engine.repository;

import com.example.engine.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Rider findRiderById(int riderId);
    Rider getRiderByUserId(int userId);
    List<Rider> findAllByVerifiedTrue();
    List<Rider> findAllByVerifiedFalse();
    List<Rider> findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase(String username);
}
