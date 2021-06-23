package com.example.engine.repository;

import com.example.engine.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Rider findRiderById(int riderId);
    Rider getRiderByUserId(int userId);
    Rider getRiderByUserUsername(String username);
    List<Rider> findAllByVerifiedTrue();
    List<Rider> findAllByVerifiedFalse();
    List<Rider> findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase(String username);

    @Query(value = "SELECT new Rider(r.id, r.verified, r.isWorking, r.locationLat, r.locationLon, r.user) " +
            "FROM Rider r left outer join Order p on r.id = p.pickupRider.id where r.isWorking = true and (p.pickupRider is null or p.pickupRider" +
            " in (SELECT rider.id FROM Rider rider left outer join Order package on rider.id = package.pickupRider.id where rider.isWorking = true and package.pickupRider is not null and package.status = 'DELIVERED')" +
            "AND p.pickupRider not in (SELECT rider.id FROM Rider rider left outer join Order package on rider.id = package.pickupRider.id where rider.isWorking = true and package.pickupRider is not null and package.status <> 'DELIVERED'))")
    List<Rider> findRidersToDispatch();
}
