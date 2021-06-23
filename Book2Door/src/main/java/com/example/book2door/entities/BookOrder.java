package com.example.book2door.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="BookOrder")
public class BookOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="clientAddress")
    private String clientAddress;
    @Column(name= "books", nullable = true)
    @ElementCollection(targetClass=String.class)
    private List<String> books = new ArrayList<>();
    @Column(name="total")
    private double total;
    @Column(name="storeAddress")
    private String storeAddress;
    @Column(name="clientId")
    private Long clientId;

    //this constructor is needed to place an order
    public BookOrder() {}
  
    public BookOrder( String clientAddress, List<String> books, double total, String storeAddress, Long clientId) {
        this.clientAddress = clientAddress;
        this.books = books;
        this.total = total;
        this.clientId = clientId;
        this.storeAddress = storeAddress;
    }

    public Long getId() {
        return this.id;
    }

    public Long getClientId(){
        return this.clientId;
    }

    public void setClientId(Long clientId){
        this.clientId = clientId;
    }

    public String getClientAddress() {
        return this.clientAddress;
    }

    public List<String> getBooks() {
        return this.books;
    }

    public double getTotal() {
        return this.total;
    }

    public String getStoreAddress() {
        return this.storeAddress;
    }




   
}
