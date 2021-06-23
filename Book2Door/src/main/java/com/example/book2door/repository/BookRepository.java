package com.example.book2door.repository;

import com.example.book2door.entities.Book;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);
    Book findById(long id);
    ArrayList<Book> findAll();
    Set<Book> findAllTopTwelveByPopularity(int popularity);
    Long removeByTitle(String string);
    
}