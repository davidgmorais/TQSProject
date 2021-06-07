package com.example.engine.dto;

public class ContribDTO {
    private final String storeName;
    private final String username;
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;

    public ContribDTO(String username, String password, String email, String firstName, String lastName, String storeName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.storeName = storeName;
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

    public String getStoreName() {
        return storeName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
