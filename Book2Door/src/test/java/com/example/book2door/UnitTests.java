package com.example.book2door;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.example.book2door.component.JwtUtils;
import com.example.book2door.controller.MyErrorController;
import com.example.book2door.entities.Admin;
import com.example.book2door.entities.Book;
import com.example.book2door.entities.BookOrder;
import com.example.book2door.entities.Client;
import com.example.book2door.entities.JwtUser;
import com.example.book2door.entities.Store;
import com.example.book2door.service.StoreServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;

class UnitTests{
    Book book;
    Store store;

    @Autowired
    StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("TestTitle");
        book.setAuthor("TestAuthor");
        book.setPrice(12.99);
        book.setStock(2);
        book.setSynopsis("TestSynop");
        store = new Store();
        book.addSeller(store);
        store.setStoreAddress("TestAdd");
        store.setFullName("TestFullName");
        store.setStoreName("TestStoreName");
        store.setStoreEmail("TestEmail");
        store.setPassword("Test");
        store.setStorePhone("123123123");
        store.setRating(5.0);
        store.getBookList().add(book);
    }

    @Test
    void testStoreService(){
        assertThrows(NullPointerException.class, () -> {
            storeService.register(store);
          });
    }

    @Test
     void testBookGetters(){
        final String title = book.getTitle();
        final String author = book.getAuthor();
        final double price = book.getPrice();
        final int stock = book.getStock();
        final String syno = book.getSynopsis();
        final Set<Store> seller = book.getSellers();
        ArrayList<String> gl = new ArrayList<>();
        gl.add("TestGenre");
        assertThat(title).isEqualTo("TestTitle");
        assertThat(author).isEqualTo("TestAuthor");
        assertThat(price).isEqualTo(12.99);
        assertThat(syno).isEqualTo("TestSynop");
        assertThat(stock).isEqualTo(2);
        assertThat(seller).contains(store);
    }

    @Test
     void testStoreGetters(){
        final String add =store.getStoreAddress();
        assertThat(add).isEqualTo("TestAdd");
        final String fulln=  store.getFullName();
        assertThat(fulln).isEqualTo("TestFullName");
        final String email =store.getEmail();
        assertThat(email).isEqualTo("TestEmail");
        final String pass = store.getPassword();
        assertThat(pass).isEqualTo("Test");
        final String phone =store.getStorePhone();
        assertThat(phone).isEqualTo("123123123");
        final String name =store.getStoreName();
        assertThat(name).isEqualTo("TestStoreName");
        final double ratting=store.getRating();
        assertThat(ratting).isEqualTo(5.0);
        Set<Book> bookList = store.getBookList();
        assertThat(bookList.contains(book)).isTrue();
        assertThat(new Store()).isEqualTo(new Store());
        store.deny();
        assertThat(store.wasAccepted()).isEqualTo(2);
        store.accept();
        assertThat(store.wasAccepted()).isEqualTo(1);
        
    }   

    @Test
    void testStoreToString()
    {
        Store store = new Store();
        String expected = "{ id='null', storeName='null', storeAddress='null', fullName='null', storePhone='null', storeEmail='null', rating='0.0', accepted='0'}"; 
        assertThat(expected).isEqualTo(store.toString());

    }

    @Test
    void testBookToString()
    {
        Book book = new Book();
        String expected = "{ id='null', title='null', author='null', price='0.0'}"; 
        assertThat(expected).isEqualTo(book.toString());
    }

    @Test
    void testBookEquals()
    {
        Store store = new Store();
        Book book = new Book();
        book.getSellers().add(store);
        Book book2 = new Book();
        book2.getSellers().add(store);
        Book book3 = book;
        book2.setTitle("asds");
        boolean equal1 = book.equals(new Book());
        boolean equal2 = book.equals(book3);
        boolean Notequal1 = book.equals("book");
        boolean Notequal2 = book.equals(book2);
        assertThat(Notequal1).isFalse();
        assertThat(equal1).isFalse();
        assertThat(equal2).isTrue();
        assertThat(Notequal2).isFalse();
    }

    @Test
    void testClientGettersAndSetters(){
        Client client = new Client("a@a.a","a","aa","9","aveiro","aaa","aaaa");
        Client c = new Client();
        c.setName("a");
        c.setEmail("a@a.a");
        c.setCity("aveiro");
        c.setAddress("aaa");
        c.setPassword("aa");
        c.setPhone("9");
        c.setzipcode("aaaa");
        boolean eq = c.equals(client);
        assertThat(eq).isTrue();
        assertThat(client.getEmail()).isEqualTo("a@a.a");
        assertThat(client.getName()).isEqualTo("a");
        assertThat(client.getPassword()).isEqualTo("aa");
        assertThat(client.getPhone()).isEqualTo("9");
        assertThat(client.getCity()).isEqualTo("aveiro");
        assertThat(client.getAddress()).isEqualTo("aaa");
        assertThat(client.getzipcode()).isEqualTo("aaaa");
        assertThat(client.getRole()).isEqualTo(2);
        assertThat(client.getId()).isNull();
        assertThat(client.getCart()).isEmpty();
    }
    
    @Test
    void testClientEquals(){
        Client c1 = new Client();
        Client c2 = new Client();
        c2.setName("as");
        Client c3 = c1;
        boolean equal1 = c1.equals(new Client());
        boolean equal2 = c1.equals(c3);
        boolean Notequal1 = c1.equals("store");
        boolean Notequal2 = c1.equals(c2);
        assertThat(Notequal1).isFalse();
        assertThat(equal1).isTrue();
        assertThat(equal2).isTrue();
        assertThat(Notequal2).isFalse();
    }

    @Test
    void testErrorController(){
        MyErrorController mErr = new MyErrorController();
        assertThat(mErr.getErrorPath()).isNull();
    }

    @Test
    void onCreateAdminVerify(){
        Admin admin = new Admin();
        assertThat(admin.getId()).isEqualTo((long)1);
        assertThat(admin.getEmail()).isEqualTo("admin@service.pt");
        assertThat(admin.getRole()).isZero();
        assertThat(admin.getPassword()).isNotNull();
    }

    @Test
    void testStoreEquals()
    {
        Book b = new Book();
        Store store = new Store();
        store.getBookList().add(b);
        Store store2 = new Store();
        store2.setStoreName("as");
        store2.getBookList().add(b);
        Store store3 = store;
        boolean equal1 = store.equals(new Store());
        boolean equal2 = store.equals(store3);
        boolean Notequal1 = store.equals("store");
        boolean Notequal2 = store.equals(store2);
        assertThat(Notequal1).isFalse();
        assertThat(equal1).isTrue();
        assertThat(equal2).isTrue();
        assertThat(Notequal2).isFalse();

        Book bookOne = new Book("Branca De neve", "princess and dwarves", "bob", 12.99 ,10);
        Book bookTwo = bookOne;
        boolean eq =bookOne.equals(bookTwo);
        assertThat(eq).isTrue();
    }


    @Test
    void testBookOrder(){
        List<String> books = null;
        Client c =new Client();
        c.setId((long)9999);
        BookOrder bookorder = new BookOrder("rua do braçal",books,10.9,"forum aveiro",c.getId()); 
        assertThat(bookorder.getBooks()).isNull();
        assertThat(bookorder.getClientAddress()).isEqualTo("rua do braçal");
        assertThat(bookorder.getStoreAddress()).isEqualTo("forum aveiro");
        assertThat(bookorder.getTotal()).isEqualTo(10.9);
    }

    @Test
    void testJwtutils(){
        JwtUtils ju = new JwtUtils();
        assertThat(ju.validateJwtToken("a")).isFalse();

    }

    @Test
    void testJwtUserClient(){
        Client client = new Client("email", "name", "password",  "phone",  "city", "address",  "zipcode");
        JwtUser jwtuser = new JwtUser(client);
        assertThat(jwtuser.getEmail()).isEqualTo("email");
        assertThat(jwtuser.getPassword()).isEqualTo("password");
        assertThat(jwtuser.getRole()).isEqualTo(client.getRole());
        assertThat(jwtuser.getUsername()).isEqualTo("name");
        assertThat(jwtuser.getAuthorities()).isEqualTo(Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")));
        assertThat(jwtuser.isAccountNonExpired()).isTrue();
        assertThat(jwtuser.isAccountNonLocked()).isTrue();
        assertThat(jwtuser.isCredentialsNonExpired()).isTrue();
        assertThat(jwtuser.isEnabled()).isTrue();
    }
    @Test
    void testJwtUserStore(){
        JwtUser jwtuser = new JwtUser(store);
        assertThat(jwtuser.getEmail()).isEqualTo("TestEmail");
        assertThat(jwtuser.getPassword()).isEqualTo("Test");
        assertThat(jwtuser.getRole()).isEqualTo(store.getRole());
        assertThat(jwtuser.getUsername()).isEqualTo("TestStoreName");
    }

    @Test
    void testJwtUserAdmin(){
        JwtUser jwtuser = new JwtUser(new Admin());
        assertThat(jwtuser.getEmail()).isEqualTo("admin@service.pt");
        assertThat(jwtuser.getRole()).isZero();
        assertThat(jwtuser.getUsername()).isEqualTo("Admin");
        assertThat(jwtuser.getId()).isEqualTo((long)1);
    }
    @Test
    void applicationContextLoadedTest(){
        JwtUser jwtuser = new JwtUser(new Admin());
        assertEquals(0,jwtuser.getRole());
    }
    @Test
    void applicationStartTest() {
        JwtUser jwtuser = new JwtUser(new Admin());
        assertEquals(0,jwtuser.getRole());
        Book2DoorApplication.main(new String[] {});
    }


}