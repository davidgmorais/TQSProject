package com.example.book2door.component;

import com.example.book2door.entities.Admin;
import com.example.book2door.service.AdminServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component    
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    AdminServiceImpl adminServiceImpl;

    @Override
    public void run(String...args) throws Exception {
        var admin = new Admin();
        adminServiceImpl.register(admin);
    }
}