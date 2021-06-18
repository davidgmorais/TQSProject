package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserDTO implements Serializable {
    @ApiModelProperty(notes = "User's unique username", required = true, example = "johnDoe")
    private final String username;
    @ApiModelProperty(notes = "User's password to access the account", required = true, example = "password", position = 1)
    private final String password;
    @ApiModelProperty(notes = "User's email address to be linked to the account", required = true, example = "johnDoe@email.com", position = 2)
    private final String email;
    @ApiModelProperty(notes = "User's first name", required = false, example = "John", position = 3)
    private final String firstName;
    @ApiModelProperty(notes = "User's last name", required = false, example = "Doe", position = 4)
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
