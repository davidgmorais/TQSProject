package com.example.book2door.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class JwtUser implements UserDetails {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final int role;
    private final Collection<? extends GrantedAuthority> authorities;
    protected static final String[] ROLES = {"ROLE_ADMIN", "ROLE_STORE", "ROLE_CLIENT"};

    public JwtUser(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.email = client.getEmail();
        this.password = client.getPassword();
        this.role = 2;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(ROLES[2]));
    }

    public JwtUser(Store store) {
        this.id = store.getId();
        this.name = store.getStoreName();
        this.email = store.getEmail();
        this.password = store.getPassword();
        this.role = 1;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(ROLES[1]));
    }

    public JwtUser(Admin adm) {
        this.id = (long)1;
        this.email = adm.getEmail();
        this.name ="Admin";
        this.password = adm.getPassword();
        this.role = 0;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(ROLES[0]));
    }
    
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }



    public int getRole() {
        return this.role;
    }


    @Override
    public String getUsername() {
        return this.name;
    }


}
