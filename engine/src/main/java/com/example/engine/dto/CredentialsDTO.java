package com.example.engine.dto;

import io.swagger.annotations.ApiModelProperty;

public class CredentialsDTO {
    @ApiModelProperty(notes = "The user's username to be authenticated with", required = true, example = "username")
    private final String username;
    @ApiModelProperty(notes = "The user's password to be authenticated with", required = true, example = "password", position = 1)
    private final String password;

    public CredentialsDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
