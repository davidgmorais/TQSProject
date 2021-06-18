package com.example.book2door.repository;

import com.example.book2door.entities.BookOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<BookOrder, Long> {

   
}