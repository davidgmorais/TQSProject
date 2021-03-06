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
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;


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
    private static final String MODEL_STORES_ATTR = "stores";
    private static final String ERROR_TEMPLATE = "error";
    private static final String TOTAL = "total";
    private static final String ANON = "anonymoususer";
    private static final String REDIRECT = "redirect:/";
    private static final String REDIRECT_CART ="redirect:/cart";

    private long orderId;

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
    public String index(Model model, String address)
    {
        Set<Store> stores = storeRepository.findAllTopTwelveByAccepted(1);
        model.addAttribute(MODEL_STORES_ATTR,stores);
        model.addAttribute("address", address);
        return "index";
    }

    @PostMapping(value = "/")
    public String index(@RequestParam String param, Model model) {
        var store = storeRepository.findBystoreName(param);
        if(store!=null){
            model.addAttribute(MODEL_STORE_ATTR,store);
            return "redirect:/store?name="+param;
        }
        return REDIRECT;
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
        
        return clientService.register(client)==null? "redirect:/signup" : REDIRECT_LOGIN;
    }

    @PostMapping(value = "/log")
    public String log(@RequestParam String email,@RequestParam String password) {
        boolean isStore = storeRepository.findBystoreEmail(email)!=null;
        if(isStore && (storeRepository.findBystoreEmail(email).wasAccepted()==2 || storeRepository.findBystoreEmail(email).wasAccepted() ==0)){
            return ERROR_TEMPLATE;
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
                        return "redirect:/store/dashboard";
                    }
                    return REDIRECT;
                }
                
            }

        }

        return REDIRECT_LOGIN;

    }

    @GetMapping(value="/search")
    public String order(Model model)
    {
        Set<Store> stores = storeRepository.findAllTopTwelveByAccepted(1);
        model.addAttribute(MODEL_STORES_ATTR,stores);
        Set<Book> books = bookRepository.findAllTopTwelveByPopularity(0);
        model.addAttribute(MODEL_BOOKS_ATTR,books);
        
        return "searchPage";
    }


    @PostMapping(value = "/search/location")
    public String searchLocation(@ModelAttribute String address) {
        return "redirect:/search";

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
        model.addAttribute("book",null);
        return ERROR_TEMPLATE;
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
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
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
        model.addAttribute(TOTAL,total);
        model.addAttribute(MODEL_BOOKS_ATTR,books);
        return "cartPage";
    }


    @GetMapping(value="/cart/remove")
    public String removeFromCart(@RequestParam long id, Model model)
    {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        client.getCart().clear();

        clientRepository.save(client);
        return REDIRECT_CART;
    }

    @GetMapping(value="/cart/add")
    public String addToCart(@RequestParam long id, Model model)
    {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        client.getCart().add(id);
        clientRepository.save(client);
        return REDIRECT_CART;
    }

    @GetMapping(value="/cart/decrease")
    public String decreaseBookNumber(@RequestParam long id, Model model)
    {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
        var client = clientRepository.findClientByEmail(jwtClient.getEmail());
        client.getCart().remove(id);
        clientRepository.save(client);
        return REDIRECT_CART;
    }



    @GetMapping(value="/checkout")
    public String checkout(Model model)
    {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
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
        model.addAttribute(TOTAL,total);
        model.addAttribute(MODEL_BOOKS_ATTR, books);
        model.addAttribute("client",client);
        model.addAttribute(MODEL_STORE_ATTR,stores.isEmpty()? new Store():stores.get(0));
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
        return storeService.register(store)==null? "redirect:/addStore" : REDIRECT_LOGIN;
        
    }


    @GetMapping(value="/admin")
    public String adminHome(Model model)
    {
        ArrayList<Store> storesToAccept = storeRepository.findByAccepted(0);
        ArrayList<Store> storesAccepted = storeRepository.findByAccepted(1);
        model.addAttribute("storesToAccept",storesToAccept);
        model.addAttribute("storesAccepted",storesAccepted);
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

    @GetMapping(value = "/location")
    public String searchLocation() {
        return "searchPageLocation";
    }

    @GetMapping(value="/order/{id}")
    public String orderProcess(@PathVariable Long id, Model model) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        orderId = id;
        return "redirect:/order";
    }

    @GetMapping(value="/order")
    public String orderProcess(Model model) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        model.addAttribute("orderId", orderId);
        return "orderPage";
    }

    
    @PostMapping(value="/order")
    @Transactional
    public String orderProcess(Long storeId)
    {   
        var auth=SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtClient= (JwtUser)(auth.getPrincipal());
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
        var order = new BookOrder(client.getAddress(), books, total,storeRepository.getById(storeId).getStoreAddress(), client.getId());
        orderRepository.save(order);
        client.getCart().clear();
        clientRepository.save(client);
        sendOrderToEngine(2, order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping(value="/store/dashboard")
    public String adminStore(Model model)
    {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtstore= (JwtUser)(auth.getPrincipal());
        var store = storeRepository.findBystoreEmail(jwtstore.getEmail());
        Set<Book> bookList = store.getBookList();
        model.addAttribute(MODEL_STORE_ATTR, store);
        model.addAttribute("bookList", bookList);
        return "adminStorePage";
    }

    @PostMapping(value="/store/dashboard")
    public String addBookToStore(@RequestParam String title, @RequestParam String synopsis, @RequestParam String author,
    @RequestParam int stock, @RequestParam double price)
    {   
        var book= bookRepository.findByTitle(title);
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(ANON)){
            return REDIRECT_LOGIN;
        }
        JwtUser jwtstore= (JwtUser)(auth.getPrincipal());
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
        return "redirect:/store/dashboard";

    }


    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> authenticateClient(@RequestBody Map<String, String> body, boolean isStore) {
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

    @PostMapping(value = "/rating/{riderid}/{reviewRider}/{reviewContrib}")
    public String rating(@PathVariable int reviewRider, @PathVariable int reviewContrib, @PathVariable int riderid) {
        if (reviewRider == 1 && reviewContrib == 1) {
            sendRatingToEngine(2, true, true, riderid);
        }
        else if (reviewRider == 1 && reviewContrib == 0) {
            sendRatingToEngine(2, true, false, riderid);
        }
        else if (reviewRider == 0 && reviewContrib == 0) {
            sendRatingToEngine(2, false, false, riderid);
        }
        else if (reviewRider == 0 && reviewContrib == 1) {
            sendRatingToEngine(2, false, true, riderid);
        }
        return REDIRECT;

    }

    private void sendRatingToEngine(int contribID, boolean reviewRider, boolean reviewContrib, int riderID) {
        final var uri = "http://localhost:8080/api/rating";
        Map<String, Object> request = Map.of("contribId", contribID, "contribThumbsUp", reviewContrib, "riderId", riderID, "riderThumbsUp", reviewRider);
        var restTemplate = new RestTemplate();
        restTemplate.put(uri, request);

    }

    private void sendOrderToEngine(Integer contribID, BookOrder bookOrder){
        final var uri = "http://localhost:8080/api/order/" + contribID;
        Map<String, Double> request = Map.of("value", bookOrder.getTotal(),
                "pickupLat", 40.631375, "pickupLon", -8.659969, "deliveryLat",  40.6407372, "deliveryLon", -8.6516916);
        var restTemplate = new RestTemplate();
        ResponseEntity<BookOrder> responseEntity = restTemplate.postForEntity(uri, request, BookOrder.class);
        var body = responseEntity.getBody();
        if (body != null) {
            orderId = body.getId();
        }
    }

}
