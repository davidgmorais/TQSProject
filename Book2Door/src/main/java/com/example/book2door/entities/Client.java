package com.example.book2door.entities;

import java.util.Objects;

import javax.persistence.Column;
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
    @Column(unique = true, name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(unique=true, name = "phone")
    private String phone;
    @Column(name = "city")
    private String city;
    @Column(name = "address")
    private String address;
    @Column(name = "zipcode")
    private String zipcode;
    private boolean logged =false;


    public Client() {
    }

    public Client(String email, String name, String password, String phone, String city, String address, String zipcode) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
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
        Client client = (Client) o;
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
            "}";
    }


    
}