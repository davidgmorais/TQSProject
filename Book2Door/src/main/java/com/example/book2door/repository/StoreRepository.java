package com.example.book2door.repository;

import java.util.ArrayList;
import java.util.Set;

import com.example.book2door.entities.Store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findBystoreEmail(String storeEmail);
    Set<Store> findAllTopTwelveByAccepted(int accepted);
    ArrayList<Store> findByAccepted(int accepted);
    Store findBystoreName(String storeName);
    Store findById(long id);
}