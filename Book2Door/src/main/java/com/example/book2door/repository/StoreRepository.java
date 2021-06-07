package com.example.book2door.repository;

import java.util.ArrayList;

import com.example.book2door.entities.Store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findBystoreEmail(String storeEmail);
    ArrayList<Store> findAllTopTwelveByAccepted(boolean accepted);
    ArrayList<Store> findByAccepted(boolean accepted);
    Store findBystoreName(String storeName);
    Store findById(long id);
}