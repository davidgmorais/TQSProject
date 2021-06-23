package com.example.book2door.entities;


import java.util.*;

import javax.persistence.*;


@Entity
@Table(name = "Book")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title",unique=true, nullable = false)
    private String title;
    @Column(name = "author", nullable = false)
    private String author;
    @Column(name = "synopsis")
    private String synopsis;
    @Column(name = "price", nullable = false)
    private double price;
    @ManyToMany
    @JoinTable(
    name = "booksBySellers", 
    joinColumns = @JoinColumn(name = "book_sellers"), 
    inverseJoinColumns = @JoinColumn(name = "store_bookList"))
    public Set<Store> sellers = new HashSet<>();
    @Column(name= "stock")
    private int stock;
    @Column(name= "popularity")
    private int popularity;

    public Book() {
        this.popularity=0;
    }

    public Book(String title, String synopsis, String author, double price, int stock) {
        this.synopsis = synopsis;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock=stock;
        this.popularity=0;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStock() {
        return this.stock;
    }


    public void setStock(int stock) {
        this.stock = stock;
    }


    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Store> getSellers() {
        return this.sellers;
    }

    public void addSeller(Store seller) {
        this.sellers.add(seller);
    }

   

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Book)) {
            return false;
        }
        var book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && price == book.price && Objects.equals(sellers, book.sellers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, price, sellers);
    }

    

    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", author='" + getAuthor() + "'" +
            ", price='" + getPrice() + "'" +
            "}";
    }

}