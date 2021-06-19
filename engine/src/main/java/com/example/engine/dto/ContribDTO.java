package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

public class ContribDTO {
    @ApiModelProperty(notes = "Contributor's service name", required = true, example = "Store Name")
    private final String storeName;
    @ApiModelProperty(notes = "Contributor's unique username", required = true, example = "johnDoe", position = 1)
    private final String username;
    @ApiModelProperty(notes = "Contributor's password to access it's account", required = true, example = "12345", position = 2)
    private final String password;
    @ApiModelProperty(notes = "Contributor's unique email address to be associated with the account", required = true, example = "johndoe@email.com", position = 3)
    private final String email;
    @ApiModelProperty(notes = "Contributor's first name", required = false, example = "John", position = 4)
    private final String firstName;
    @ApiModelProperty(notes = "Contributor's last name", required = false, example = "Doe", position = 5)
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
