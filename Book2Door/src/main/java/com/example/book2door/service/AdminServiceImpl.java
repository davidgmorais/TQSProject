package com.example.book2door.service;

import org.springframework.stereotype.Service;

import com.example.book2door.entities.Admin;
import com.example.book2door.entities.JwtUser;
import com.example.book2door.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AdminServiceImpl implements UserDetailsService, AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return new JwtUser(admin);
        }
        throw new UsernameNotFoundException(email);
    }

    @Override
    public Admin register(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()) == null) {
            return adminRepository.save(admin);
        }
        return null;
    }


}
