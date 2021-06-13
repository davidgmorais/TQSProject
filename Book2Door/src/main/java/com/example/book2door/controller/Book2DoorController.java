package com.example.book2door.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.book2door.component.JwtUtils;
import com.example.book2door.entities.Book;
import com.example.book2door.entities.Client;
import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.Store;
import com.example.book2door.repository.BookRepository;
import com.example.book2door.repository.ClientRepository;
import com.example.book2door.repository.StoreRepository;
import com.example.book2door.service.ClientService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class Book2DoorController {
    private static final Logger logger = LoggerFactory.getLogger(Book2DoorController.class);
    private static final String AUTHO = "Authorization";
    private static final String PASS = "password";
    private static final String REDIRECT_LOGIN = "redirect:/login" ;
    private static final String EMAIL_CONST = "email" ;
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClientService clientService;

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
    public String signup(){
        return "signup";
    }

    @PostMapping(value = "/signup")
    public String signup(@RequestParam String name,@RequestParam String email,@RequestParam String zipcode,
    @RequestParam String password,@RequestParam String city,@RequestParam String address,@RequestParam String phone) {
        var c =clientRepository.findClientByEmail(email);
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        if(c == null){
            var client = new Client(email,name, encodedPassword,phone,city, address,zipcode);
            clientRepository.save(client);
            return REDIRECT_LOGIN;
        }
        
        return "signup";
    }

    @PostMapping(value = "/log")
    public String log(@RequestParam String email,@RequestParam String password) {
        boolean isStore = clientRepository.findClientByEmail(email)==null;
        HashMap<String, String> creds = new HashMap<>();
        creds.put(EMAIL_CONST, email);
        creds.put(PASS, password);
        ResponseEntity<Map<String, String>> authentication = authenticateClient(creds,isStore);
        if (authentication.getHeaders().containsKey(AUTHO)) {
            List<String> authList = authentication.getHeaders().get(AUTHO);
            if (authList != null && !authList.isEmpty()) {
                String token = authList.get(0);
                logger.info("Token to include on header: {}", token);
                if(authentication.hasBody() && authentication.getBody().containsKey("role")){
                    String role = authentication.getBody().get("role");
                    if (role.equals("0")) {
                        return "redirect:/admin";
                    }
                    else {
                        return "redirect:/";
                    }
                }
                
            }

        }

        return REDIRECT_LOGIN;

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
    public String searchBookOrStore(@RequestParam String param,Model model)
    {
        var store = storeRepository.findBystoreName(param);
        if(store!=null){
            model.addAttribute("store",store);
            return "redirect:/store?name="+param;
        }
        var book = bookRepository.findByTitle(param);
        if(book!=null){
            model.addAttribute("book",book);
            return "redirect:/book?title="+param;
        }
        return "error";
    }

    @GetMapping(value="/store")
    public String storePage(@RequestParam String name,Model model)
    {   
        var store = storeRepository.findBystoreName(name);
        model.addAttribute("store",store);
        
        return "storePage";
    }

    @GetMapping(value="/book")
    public String bookPage(@RequestParam String title,Model model)
    {   
        var book = bookRepository.findByTitle(title);
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

    @PostMapping(value = "/addStore")
    public String createStore(@RequestParam String storeName,@RequestParam String storeAddress,@RequestParam String storePhone,
    @RequestParam String storeEmail,@RequestParam String fullName,@RequestParam String password) {
        var s = storeRepository.findBystoreEmail(storeEmail);
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        if(s==null){
            var store = new Store(storeName,storeAddress,fullName,encodedPassword,storePhone,storeEmail);
            storeRepository.save(store);
            return REDIRECT_LOGIN;
        }
        return "addStore";
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


    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> authenticateClient(@RequestBody Map<String, String> body, boolean isStore) {
        final String usernameKey = EMAIL_CONST;
        if (!body.containsKey(usernameKey) || !body.containsKey(PASS)) {
            
            return new ResponseEntity<>( Map.of("data", "Must provide email and password"), HttpStatus.BAD_REQUEST);
        }
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get(EMAIL_CONST), body.get(PASS)));
        logger.info("{}", auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        var jwt = jwtUtils.generateJwtToken(auth);

        var jwtUser = (JwtUser) auth.getPrincipal();
        logger.info("Authenticated as {}", jwtUser.getUsername());
        HashMap<String, String> responseBody;
        HttpHeaders responseHeader;
        if(isStore){
            var user = new Store();
            BeanUtils.copyProperties(jwtUser, user);
            responseHeader = new HttpHeaders();
            responseHeader.set(AUTHO, jwt);
    
            responseBody = new HashMap<>();
            responseBody.put("data", "Authentication successful - Authorization token was sent in the header.");
            responseBody.put(EMAIL_CONST, user.getEmail());
            responseBody.put("role", String.valueOf(user.getRole()));
        }
        else{
            var user = new Client();
            BeanUtils.copyProperties(jwtUser, user);
            responseHeader = new HttpHeaders();
            responseHeader.set(AUTHO, jwt);
            responseBody = new HashMap<>();
            responseBody.put("data", "Authentication successful - Authorization token was sent in the header.");
            responseBody.put(EMAIL_CONST, user.getEmail());
            responseBody.put("role", String.valueOf(user.getRole()));

        }
        return new ResponseEntity<>(responseBody, responseHeader, HttpStatus.OK);
    }

}
