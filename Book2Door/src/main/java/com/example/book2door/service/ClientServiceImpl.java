package com.example.book2door.service;

import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.Client;
import com.example.book2door.repository.ClientRepository;
import com.example.book2door.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements UserDetailsService, ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StoreRepository storeRepository;


    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var client = clientRepository.findClientByEmail(email);
        if (client != null) {
            return new JwtUser(client);
        }
        var store = storeRepository.findBystoreEmail(email);
        if (store != null) {
            return new JwtUser(store);
        }
        throw new UsernameNotFoundException(email);
    }

    @Override
    public Client register(Client client) {
        if (storeRepository.findBystoreEmail(client.getEmail())==null && !client.getEmail().equalsIgnoreCase("admin@service.pt") && clientRepository.findClientByName(client.getName()) == null && clientRepository.findClientByEmail(client.getEmail()) == null) {
            return clientRepository.save(client);
        }
        return null;
    }

 

  
}
