package com.example.book2door.entities;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;



@Entity
@Table(name = "Store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false, name = "storeName")
    private String storeName;
    @Column(unique = true,nullable = false, name = "storeAddress")
    private String storeAddress;
    @Column(nullable = false,name = "fullName")
    private String fullName;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(unique = true,nullable = false, name = "storePhone")
    private String storePhone;
    @Column(nullable = false,unique = true, name = "storeEmail")
    private String storeEmail;
    @Column(name = "rating")
    private Double rating=0.0;
    @ManyToMany(mappedBy = "sellers")
    private Set<Book> bookList = new HashSet<>();
    private boolean logged=false;
    @Column(name = "accepted")
    private boolean accepted;
    @Column(name = "numberOfRates")
    private Double numberOfRates=0.0;
    @Column(name = "totalRate")
    private Double totalRate=0.0;
    

    public Store() {
    }

    public Store(String storeName, String storeAddress, String fullName, String password, String storePhone, String storeEmail) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.fullName = fullName;
        this.password = password;
        this.storePhone = storePhone;
        this.storeEmail = storeEmail;
        this.accepted = false;
    }



    public void accept(){
        this.accepted=true;
    }

    public Double getRating(){
        return this.rating;
    }

    public void setRating(Double rating){
        this.numberOfRates+=1;
        this.totalRate+=rating;
        this.rating = this.totalRate/this.numberOfRates;
        
    }

    public boolean wasAccepted(){
        return this.accepted;
    }
    

    public boolean isLogged(){
        return this.logged;
    }
    
    public void setLogged(boolean isLogged){
        this.logged=isLogged;
    }

    public Long getId() {
        return this.id;
    }


    public Set<Book> getBookList() {
        return this.bookList;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return this.storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStorePhone() {
        return this.storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreEmail() {
        return this.storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }



    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Store)) {
            return false;
        }
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(storeName, store.storeName) && Objects.equals(storeAddress, store.storeAddress) && Objects.equals(fullName, store.fullName) && Objects.equals(password, store.password) && Objects.equals(storePhone, store.storePhone) && Objects.equals(storeEmail, store.storeEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeName, storeAddress, fullName, password, storePhone, storeEmail);
    }



    public boolean getLogged() {
        return this.logged;
    }


    public boolean isAccepted() {
        return this.accepted;
    }

    public boolean getAccepted() {
        return this.accepted;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", storeName='" + getStoreName() + "'" +
            ", storeAddress='" + getStoreAddress() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", password='" + getPassword() + "'" +
            ", storePhone='" + getStorePhone() + "'" +
            ", storeEmail='" + getStoreEmail() + "'" +
            ", rating='" + getRating() + "'" +
            ", bookList='" + getBookList() + "'" +
            ", logged='" + isLogged() + "'" +
            ", accepted='" + isAccepted() + "'" +
            "}";
    }


}