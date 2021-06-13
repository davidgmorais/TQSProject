package com.example.book2door.entities;


import java.util.*;

import javax.persistence.*;


@Entity
@Table(name = "Book")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true,name = "title", nullable = false)
    private String title;
    @Column(name = "releaseYear")
    private int releaseYear;
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
    @Column(name= "language")
    private String language;
    @Column(name= "stock", nullable = false)
    private int stock=0;
    @Column(name= "genres")
    private ArrayList<String> genres = new ArrayList<>();
    


    public Book() {
    }

    public Book(String title, String synopsis, int releaseYear, String author, double price, String language) {
        this.synopsis = synopsis;
        this.title = title;
        this.releaseYear = releaseYear;
        this.author = author;
        this.price = price;
        this.language = language;
        this.stock+=1;
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

    public int getReleaseYear() {
        return this.releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
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

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getGenres() {
        return this.genres;
    }

    public void addGenres(String genres) {
        this.genres.add(genres);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Book)) {
            return false;
        }
        var book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && releaseYear == book.releaseYear && Objects.equals(author, book.author) && price == book.price && Objects.equals(sellers, book.sellers) && Objects.equals(language, book.language) && Objects.equals(genres, book.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, releaseYear, author, price, sellers, language, genres);
    }

    

    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", releaseYear='" + getReleaseYear() + "'" +
            ", author='" + getAuthor() + "'" +
            ", price='" + getPrice() + "'" +
            ", sellers='" + getSellers() + "'" +
            ", language='" + getLanguage() + "'" +
            ", genres='" + getGenres() + "'" +
            "}";
    }

}