package com.example.book2door.service;

import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.Store;
import com.example.book2door.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements UserDetailsService, StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var store = storeRepository.findBystoreEmail(email);
        if (store != null) {
            return new JwtUser(store);
        }
        throw new UsernameNotFoundException(email);
    }

    @Override
    public Store register(Store store) {
        if (storeRepository.findBystoreName(store.getStoreName()) == null && storeRepository.findBystoreEmail(store.getEmail()) == null) {
            return storeRepository.save(store);
        }
        return null;
    }
 

  
}