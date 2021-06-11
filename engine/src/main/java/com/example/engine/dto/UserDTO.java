package com.example.engine.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private final String username;
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;

    public UserDTO(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
