package com.example.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Rider's unique ID automatically generated", required = true)
    private int id;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    @ApiModelProperty(notes = "User unique username", required = true)
    private String username;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    @ApiModelProperty(notes = "Email associated with the user's account", required = true)
    private String email;

    @JsonIgnore
    @NotBlank
    @Column(name = "password", nullable = false)
    @ApiModelProperty(notes = "Password to authenticate the user by", required = true)
    private String password;

    @Column(name = "first_name")
    @ApiModelProperty(notes = "User's first name")
    private String firstName;

    @Column(name = "last_name")
    @ApiModelProperty(notes = "User's last name")
    private String lastName;

    // 0 - admin, 1 - rider, 2 - contrib
    @Column(name = "role", nullable = false)
    @ApiModelProperty(notes = "User's role in the engine - 0 are admins, 1 are riders and 2 are contributors", required = true)
    private int role;

    public User(String username, String email, String password, String firstName, String lastName, int role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return this.role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
