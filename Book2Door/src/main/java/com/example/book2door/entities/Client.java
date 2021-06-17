package com.example.book2door.entities;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

@Entity
@Table(name = "Client")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true, name = "email")
    private String email;
    @Column(nullable = false,name = "name")
    private String name;
    @Column(nullable = false,name = "password")
    private String password;
    @Column(nullable = false,unique=true, name = "phone")
    private String phone;
    @Column(nullable = false,name = "city")
    private String city;
    @Column(nullable = false,name = "address")
    private String address;
    @Column(nullable = false,name = "zipcode")
    private String zipcode;
    @Column(name = "role", nullable = false)
    private int role;
    @Column(name= "cart", nullable = true)
    @ElementCollection()
    private List<Long> cart = new ArrayList<>();


    public Client() {
        this.role=2;
    }

    public Client(String email, String name, String password, String phone, String city, String address, String zipcode) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
        this.role=2;
    }

    
    public int getRole(){
        return this.role;
    }

    public Long getId() {
        return this.id;
    }

    public List<Long> getCart(){
        return this.cart;
    }
    
    public String getEmail() {
        return this.email;
    }

    @NonNull
    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getName() {
        return this.name;
    }

    @NonNull
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }
    @NonNull
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return this.phone;
    }

    @NonNull
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return this.city;
    }

    @NonNull
    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return this.address;
    }

    @NonNull
    public void setAddress(String address) {
        this.address = address;
    }

    public String getzipcode() {
        return this.zipcode;
    }

    @NonNull
    public void setzipcode(String zipcode) {
        this.zipcode = zipcode;
    }

   
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Client)) {
            return false;
        }
        var client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(email, client.email) && Objects.equals(name, client.name) && Objects.equals(password, client.password) && Objects.equals(phone, client.phone) && Objects.equals(city, client.city) && Objects.equals(address, client.address) && Objects.equals(zipcode, client.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, password, phone, city, address, zipcode);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", email='" + getEmail() + "'" +
            ", name='" + getName() + "'" +
            ", password='" + getPassword() + "'" +
            ", phone='" + getPhone() + "'" +
            ", city='" + getCity() + "'" +
            ", address='" + getAddress() + "'" +
            ", zipcode='" + getzipcode() + "'" +
            ", role='" + getRole() + "'" +
            ", cart='" + getCart() + "'" +
            "}";
    }

    
}