package com.example.book2door.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table (name ="Admin")
public class Admin {
    
    @Id
    private Long id;
    @Column(nullable = false,unique = true, name = "email")
    private final String email;
    @Column(nullable = false,unique = true, name = "password")
    private final String password;
    @Column(nullable = false,unique = true, name = "role")
    private final int role;

    public Admin(){
        this.id = (long)1;
        this.email = "admin@service.pt";
        this.password = new BCryptPasswordEncoder().encode("serviceAdminPassword");
        this.role = 0;
    }
    public Long getId(){
        return this.id;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public int getRole(){
        return this.role;
    }


}
