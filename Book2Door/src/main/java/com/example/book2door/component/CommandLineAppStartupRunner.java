package com.example.book2door.component;

import com.example.book2door.entities.Admin;
import com.example.book2door.entities.Book;
import com.example.book2door.entities.Client;
import com.example.book2door.entities.Store;
import com.example.book2door.repository.AdminRepository;
import com.example.book2door.repository.BookRepository;
import com.example.book2door.repository.ClientRepository;
import com.example.book2door.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component    
public class CommandLineAppStartupRunner implements CommandLineRunner {
    
    
    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    BookRepository bookRepository;


    @Override
    public void run(String...args) throws Exception {
        var admin = new Admin();
        adminRepository.save(admin);
        var client = new Client("client@a.pt","client","clientpw","111","clientCity","ClientAddress","Clientzip");
        clientRepository.save(client);
        var store = new Store("StoreName","StoreAddress","StoreFullName","StorePass","222","store@store.pt");
        storeRepository.save(store);
        var book = new Book("TestBook", "TestSyno", "TestAuth", 1.0 ,1);
        bookRepository.save(book);
        
    }
}