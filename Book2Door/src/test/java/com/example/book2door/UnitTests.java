package com.example.book2door;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.book2door.entities.Book;
import com.example.book2door.entities.Store;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class UnitTests{
    Book book;
    Store store;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("TestTitle");
        book.setAuthor("TestAuthor");
        book.setLanguage("TestLang");
        book.setPrice(12.99);
        book.setReleaseYear(1999);
        book.setStock(2);
        book.setSynopsis("TestSynop");
        book.addGenres("TestGenre");
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
     void testBookGetters(){
        final String title = book.getTitle();
        final String author = book.getAuthor();
        final String lang = book.getLanguage();
        final double price = book.getPrice();
        final int year = book.getReleaseYear();
        final int stock = book.getStock();
        final String syno = book.getSynopsis();
        final List<String> genre = book.getGenres();
        final Set<Store> seller = book.getSellers();
        ArrayList<String> gl = new ArrayList<>();
        gl.add("TestGenre");
        assertThat(title).isEqualTo("TestTitle");
        assertThat(author).isEqualTo("TestAuthor");
        assertThat(lang).isEqualTo("TestLang");
        assertThat(price).isEqualTo(12.99);
        assertThat(year).isEqualTo(1999);
        assertThat(syno).isEqualTo("TestSynop");
        assertThat(stock).isEqualTo(2);
        assertThat(genre).isEqualTo(gl);
        assertThat(seller).contains(store);
    }

    @Test
     void testStoreGetters(){
        final String add =store.getStoreAddress();
        assertThat(add).isEqualTo("TestAdd");
        final String fulln=  store.getFullName();
        assertThat(fulln).isEqualTo("TestFullName");
        final String email =store.getStoreEmail();
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
        
    }   

    @Test
    void testStoreToString()
    {
        Store store = new Store();
        String expected = "{ id='null', storeName='null', storeAddress='null', fullName='null', password='null', storePhone='null', storeEmail='null', rating='0.0', bookList='[]', accepted='false'}"; 
        assertThat(expected).isEqualTo(store.toString());

    }

    @Test
    void testBookToString()
    {
        Book book = new Book();
        String expected = "{ id='null', title='null', releaseYear='0', author='null', price='0.0', sellers='[]', language='null', genres='[]'}"; 
        assertThat(expected).isEqualTo(book.toString());
    }

    @Test
    void testBookEquals()
    {
        Book book = new Book();
        Book book2 = new Book();
        Book book3 = book;
        book2.setTitle("asds");
        boolean equal1 = book.equals(new Book());
        boolean equal2 = book.equals(book3);
        boolean Notequal1 = book.equals("s");
        boolean Notequal2 = book.equals(book2);
        assertThat(Notequal1).isFalse();
        assertThat(equal1).isTrue();
        assertThat(equal2).isTrue();
        assertThat(Notequal2).isFalse();
    }

    @Test
    void testStoreEquals()
    {
        Store store = new Store();
        Store store2 = new Store();
        store2.setStoreName("as");
        Store store3 = store;
        boolean equal1 = store.equals(new Store());
        boolean equal2 = store.equals(store3);
        boolean Notequal1 = store.equals("s");
        boolean Notequal2 = store.equals(store2);
        assertThat(Notequal1).isFalse();
        assertThat(equal1).isTrue();
        assertThat(equal2).isTrue();
        assertThat(Notequal2).isFalse();
    }



}