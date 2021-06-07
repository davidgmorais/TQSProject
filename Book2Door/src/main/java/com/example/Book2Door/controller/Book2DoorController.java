package com.example.Book2Door.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Book2DoorController {
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
    public String order()
    {
        return "searchPage";
    }

    @GetMapping(value="/store")
    public String storePage()
    {
        return "storePage";
    }

    @GetMapping(value="/book")
    public String bookPage()
    {
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
