package com.example.engine.entity;

import io.swagger.annotations.ApiModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "Contrib")
public class Contrib {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Contributor's unique ID automatically generated", required = true, example = "1")
    private int id;

    @NotBlank
    @Column(name = "store_name", nullable = false)
    @ApiModelProperty(notes = "Contributor's service name", required = true, example = "Store Name")
    private String storeName;

    @Column(name = "verified", nullable = false, columnDefinition = "boolean default false")
    @ApiModelProperty(notes = "Flag to mark if the correspondent Contributor is validated by the admin or not", required = true)
    private Boolean verified;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(notes = "User account information linked to the contributor", required = true)
    private User user;

    public Contrib() {}
    public Contrib(@NotNull User user, String storeName) {
        this.storeName = storeName;
        this.user = user;
        this.verified = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public User getUser() {
        return user;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
