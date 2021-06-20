package com.example.book2door.repository;

import com.example.book2door.entities.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long>{
    Admin findByEmail(String email);
    long deleteById(long id);
}
