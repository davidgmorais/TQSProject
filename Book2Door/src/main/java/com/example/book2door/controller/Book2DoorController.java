package com.example.book2door.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.book2door.component.JwtUtils;
import com.example.book2door.entities.Admin;
import com.example.book2door.entities.Book;
import com.example.book2door.entities.Client;
import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.BookOrder;
import com.example.book2door.entities.Store;
import com.example.book2door.repository.AdminRepository;
import com.example.book2door.repository.BookRepository;
import com.example.book2door.repository.ClientRepository;
import com.example.book2door.repository.OrderRepository;
import com.example.book2door.repository.StoreRepository;
import com.example.book2door.service.ClientService;
import com.example.book2door.service.StoreService;

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
    private static final String REDIRECT_ADMIN = "redirect:/admin";
    private static final String AUTH_SUCCESS = "Authentication successful - Authorization token was sent in the header.";
    private static final String MODEL_BOOKS_ATTR = "books";
    private static final String MODEL_STORE_ATTR = "store";

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    OrderRepository orderRepository;


    @Autowired
    ClientService clientService;

    @Autowired
    StoreService storeService;

    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    BookRepository bookRepository;

    @Autowired
    AdminRepository adminRepository;


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
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        var client = new Client(email,name, encodedPassword,phone,city, address,zipcode);
        
        return clientService.register(client)==null? "signup" : REDIRECT_LOGIN;
    }

    @PostMapping(value = "/log")
    public String log(@RequestParam String email,@RequestParam String password) {
        boolean isStore = storeRepository.findBystoreEmail(email)!=null;
        if(storeRepository.findBystoreEmail(email).wasAccepted()==2 || storeRepository.findBystoreEmail(email).wasAccepted() ==0){
            return "error";
        }
        HashMap<String, String> creds = new HashMap<>();
        creds.put(EMAIL_CONST, email);
        creds.put(PASS, password);
        ResponseEntity<Map<String, String>> authentication = authenticateClient(creds,isStore);
        if (authentication.getHeaders().containsKey(AUTHO)) {
            List<String> authList = authentication.getHeaders().get(AUTHO);
            if (authList != null) {
                String token = authList.get(0);
                logger.info("Token to include on header: {}", token);
                var body= authentication.getBody();
                if(body!= null){
                    String role = body.get("role");
                    if (role.equals("0")) {
                        return REDIRECT_ADMIN;
                    }
                    else if (role.equals("1")) {
                        return "redirect:/storeDashboard";
                    }
                    else{
                        return "redirect:/";
                    }
                }
                else{
                    return REDIRECT_LOGIN;
                }
                
            }

        }

        return REDIRECT_LOGIN;

    }

    @GetMapping(value="/search")
    public String order(Model model)
    {
        Set<Store> stores = storeRepository.findAllTopTwelveByAccepted(1);
        model.addAttribute("stores",stores);
        Set<Book> books = bookRepository.findAllTopTwelveByPopularity(0);
        model.addAttribute(MODEL_BOOKS_ATTR,books);
        
        return "searchPage";
    }

    @PostMapping(value="/search")
    public String searchBookOrStore(@RequestParam String param,Model model)
    {
        var store = storeRepository.findBystoreName(param);
        if(store!=null){
            model.addAttribute(MODEL_STORE_ATTR,store);
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
        var bookList = store.getBookList();
        model.addAttribute("bookList", bookList);
        model.addAttribute(MODEL_STORE_ATTR,store);
        
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
    public String cart(Model model)
    {
        JwtUser jwtClient= (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        List<Long> booksOnCart = client.getCart();
        double total=0;
        Map<Book,Integer> books = new HashMap<>();
        Book book;
        for(Long id : booksOnCart){
            book = bookRepository.findById(id).orElse(null);
            books.merge(book,1, Integer::sum);
            total+=book==null? 0: book.getPrice();  
        }
        model.addAttribute("total",total);
        model.addAttribute(MODEL_BOOKS_ATTR,books);
        return "cartPage";
    }


    @GetMapping(value="/cart/remove")
    public String removeFromCart(@RequestParam long id, Model model)
    {
        JwtUser jwtClient= (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        client.getCart().remove(id);
        clientRepository.save(client);
        return "redirect:/cart";
    }
    @GetMapping(value="/cart/add")
    public String addToCart(@RequestParam long id, Model model)
    {
        JwtUser jwtClient= (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        client.getCart().add(id);
        clientRepository.save(client);
        return "redirect:/cart";
    }



    @GetMapping(value="/checkout")
    public String checkout(Model model)
    {
        JwtUser jwtClient= (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        List<Long> booksOnCart = client.getCart();
        double total=0;
        List<Book> books= new ArrayList<>();
        Set<Store> bookSellers= new HashSet<>();
        for(Long id : booksOnCart){
            var book = bookRepository.findById(id).orElse(null);
            if(book!=null){
                books.add(book);
                total+=book.getPrice();
                bookSellers=book.getSellers();
            }
        }
        List<Store> stores;
        if(bookSellers!=null){
            stores= new ArrayList<>(bookSellers);
        }
        else{
            stores = new ArrayList<>();
        }
        model.addAttribute("total",total);
        model.addAttribute(MODEL_BOOKS_ATTR, books);
        model.addAttribute("client",client);
        model.addAttribute(MODEL_STORE_ATTR,stores.get(0));
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
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        var store = new Store(storeName,storeAddress,fullName,encodedPassword,storePhone,storeEmail);
        return storeService.register(store)==null? "addStorePage" : REDIRECT_LOGIN;
        
    }


    @GetMapping(value="/admin")
    public String adminHome(Model model)
    {
        ArrayList<Store> stores = storeRepository.findByAccepted(0);
        model.addAttribute("stores",stores);
        return "adminFrontPage";
    }
    
    @PostMapping(value="/admin/accept")
    public String acceptStore(@RequestParam long id, Model model){
        var store = storeRepository.findById(id);
        store.accept();
        storeRepository.save(store);
        return REDIRECT_ADMIN;
    }  
    @PostMapping(value="/admin/deny")
    public String denyStore(@RequestParam long id, Model model){
        var store = storeRepository.findById(id);
        store.deny();
        storeRepository.save(store);
        return REDIRECT_ADMIN;
    }    
    
    @GetMapping(value="/order")
    public String orderProcess(Long storeId, Model model)
    {   
        JwtUser jwtClient= (JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        List<Long> booksOnCart = client.getCart();
        double total=0;
        Book book;
        List<String> books= new ArrayList<>();
        for(Long id : booksOnCart){
            book = bookRepository.findById(id).orElse(null);
            if(book!=null){
                books.add(book.getTitle());
                total+=book.getPrice();
            }
        }
        var order = new BookOrder(client.getAddress(), books, total,storeRepository.getById(storeId).getStoreAddress());
        orderRepository.save(order);

        return "orderPage";
    }

    @GetMapping(value="/storeDashboard")
    public String adminStore(Model model)
    {
        JwtUser jwtstore =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var store = storeRepository.findBystoreEmail(jwtstore.getEmail());
        Set<Book> bookList = store.getBookList();
        model.addAttribute(MODEL_STORE_ATTR, store);
        model.addAttribute("bookList", bookList);
        return "adminStorePage";
    }

    @PostMapping(value="/storeDashboard")
    public String addBookToStore(@RequestParam String title, @RequestParam String synopsis, @RequestParam String author,
    @RequestParam int stock, @RequestParam double price)
    {   
        var book= bookRepository.findByTitle(title);
        JwtUser jwtstore =(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var store = storeRepository.findBystoreEmail(jwtstore.getEmail());
        if(book==null){
            book = new Book(title,synopsis,author,price,stock);
            book.getSellers().add(store);
            bookRepository.save(book);
            store.getBookList().add(book);
            storeRepository.save(store);
        }
        else{
            book.getSellers().add(store);
            bookRepository.save(book);
            store.getBookList().add(book);
            storeRepository.save(store);
        }
        return "redirect:/storeDashboard";
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
            responseBody.put("data", AUTH_SUCCESS);
            responseBody.put(EMAIL_CONST, user.getEmail());
            responseBody.put("role", String.valueOf(user.getRole()));
        }
        else{
            var adm = new Admin();
            if(body.get(EMAIL_CONST).equalsIgnoreCase(adm.getEmail())){
                BeanUtils.copyProperties(jwtUser, adm);
                responseHeader = new HttpHeaders();
                responseHeader.set(AUTHO, jwt);
                responseBody = new HashMap<>();
                responseBody.put("data", AUTH_SUCCESS);
                responseBody.put(EMAIL_CONST, adm.getEmail());
                responseBody.put("role", String.valueOf(adm.getRole()));
            }
            else{
                var user = new Client();
                BeanUtils.copyProperties(jwtUser, user);
                responseHeader = new HttpHeaders();
                responseHeader.set(AUTHO, jwt);
                responseBody = new HashMap<>();
                responseBody.put("data", AUTH_SUCCESS);
                responseBody.put(EMAIL_CONST, user.getEmail());
                responseBody.put("role", String.valueOf(user.getRole()));
            }
            

        }
        return new ResponseEntity<>(responseBody, responseHeader, HttpStatus.OK);
    }

}
