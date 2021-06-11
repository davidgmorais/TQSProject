package com.example.engine.repository;

import com.example.engine.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Rider getRiderByUserId(int userId);
}
