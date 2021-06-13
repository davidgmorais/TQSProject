package com.example.book2door.repository;

import com.example.book2door.entities.Book;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);
    Book findById(long id);
    ArrayList<Book> findAll();
    
}