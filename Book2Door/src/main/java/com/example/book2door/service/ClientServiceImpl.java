package com.example.book2door.service;

import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.Client;
import com.example.book2door.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements UserDetailsService, ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var client = clientRepository.findClientByEmail(email);
        if (client != null) {
            return new JwtUser(client);
        }
        throw new UsernameNotFoundException(email);
    }

    @Override
    public Client register(Client client) {
        if (clientRepository.findClientByName(client.getName()) == null && clientRepository.findClientByEmail(client.getEmail()) == null) {
            return clientRepository.save(client);
        }
        return null;
    }

 

  
}
