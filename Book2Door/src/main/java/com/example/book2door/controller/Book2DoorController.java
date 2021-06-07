package com.example.book2door.controller;

import java.util.ArrayList;

import com.example.book2door.entities.Book;
import com.example.book2door.entities.Store;
import com.example.book2door.repository.BookRepository;
import com.example.book2door.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class Book2DoorController {
    
    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    BookRepository bookRepository;


    @GetMapping(value="/")
    public String index()
    {    

        return "index";

    }

    @GetMapping(value="/login")
    public String login()
    {

        return "login";

    }
    @GetMapping(value="/signup")
    public String signup()
    {

        return "signup";

    }
    @GetMapping(value="/search")
    public String order(Model model)
    {
        
        ArrayList<Store> stores = storeRepository.findAllTopTwelveByAccepted(false);
        model.addAttribute("stores",stores);
        ArrayList<Book> books = bookRepository.findAll();
        model.addAttribute("books",books);
        
        return "searchPage";
    }

    @PostMapping(value="/search")
    public String searchBookOrStore(@RequestParam String searchWord,Model model)
    {
        Store store = storeRepository.findBystoreName(searchWord);
        if(store!=null){
            model.addAttribute("store",store);
            return "redirect:/store?searchWord="+searchWord;
        }
        Book book = bookRepository.findByTitle(searchWord);
        if(book!=null){
            model.addAttribute("book",book);
            return "redirect:/book?searchWord="+searchWord;
        }
        return "searchPage";
    }

    @GetMapping(value="/store")
    public String storePage(@RequestParam String searchWord,Model model)
    {   
        Store store = storeRepository.findBystoreName(searchWord);
        model.addAttribute("store",store);
        
        return "storePage";
    }

    @GetMapping(value="/book")
    public String bookPage(@RequestParam String searchWord,Model model)
    {   
        Book book = bookRepository.findByTitle(searchWord);
        model.addAttribute("book",book);
        return "bookPage";
    }

    @GetMapping(value="/cart")
    public String cart()
    {
        return "cartPage";
    }

    @GetMapping(value="/checkout")
    public String checkout()
    {
        return "checkoutPage";
    }

    @GetMapping(value="/addStore")
    public String addStore()
    {
        return "addStorePage";
    }

    @GetMapping(value="/admin")
    public String adminHome()
    {
        return "adminFrontPage";
    }

    @GetMapping(value="/order")
    public String orderProcess()
    {
        return "orderPage";
    }

    @GetMapping(value="/adminStore")
    public String adminStore()
    {
        return "adminStorePage";
    }

}
