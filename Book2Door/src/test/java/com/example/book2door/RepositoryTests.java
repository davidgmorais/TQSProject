package com.example.book2door;


import com.example.book2door.entities.*;
import com.example.book2door.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.example.book2door.repository.AdminRepository;
import com.example.book2door.repository.BookRepository;
import com.example.book2door.repository.StoreRepository;
import com.example.book2door.service.StoreServiceImpl;

@ExtendWith(MockitoExtension.class)
class RepositoryTests {

    @Mock(lenient = true)
    private BookRepository bookRepository;

    @Mock(lenient = true)
    private StoreRepository storeRepository;

    @Mock(lenient = true)
    private AdminRepository adminRepository;
    @Mock(lenient= true)
    StoreServiceImpl storeService;

    @Mock(lenient = true)
    private OrderRepository orderRepository;


    @BeforeEach
     void setUp() {
        Book book = new Book("Branca De neve", "princess and dwarves", "bob", 12.99 ,10);
        List<String> books = new ArrayList<String>();
        books.add("Branca de neve");
        BookOrder order = new BookOrder("Viseu", books, 12.99, "Viseu", (long) 1);
        Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
        book.getSellers().add(store);
        store.getBookList().add(book);
        ArrayList<Store> storeList = new ArrayList<>();
        storeList.add(store);
        Mockito.when(bookRepository.findByTitle(book.getTitle())).thenReturn(book);
        Mockito.when(storeRepository.findBystoreEmail(store.getEmail())).thenReturn(store);
        Mockito.when(storeRepository.findBystoreName(store.getStoreName())).thenReturn(store);
        Mockito.when(storeRepository.findByAccepted(store.wasAccepted())).thenReturn(storeList);
        Mockito.when(adminRepository.findByEmail("admin@service.pt")).thenReturn(new Admin());
        Mockito.when(storeService.loadUserByUsername("fnac@fnac.pt")).thenReturn(new JwtUser(store));
        Mockito.when(storeService.register(store)).thenReturn(store);
    }



    @Test
    void whenStoreServiceLoadByUsernameThenReturnStore(){
       assertThat(storeService.loadUserByUsername("fnac@fnac.pt")).isNotNull();
    }
    @Test
    void whenStoreServiceRegisterThenReturnStore(){
      Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
      assertThat(storeService.register(store)).isNotNull();
    }
    
    @Test
     void whenSearchStoreByEmailAndStoreExistsOnDB_ThenReturnStore(){
        String storeEmail="fnac@fnac.pt";
        Store found = storeRepository.findBystoreEmail(storeEmail);
        assertThat(found.getStoreName()).isEqualTo("Fnac");
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findBystoreEmail(Mockito.anyString());
    }
    @Test
     void whenSearchStoreByEmailAndStoreDoesntExistsOnDB_ThenReturnNull(){
        String storeEmail="asdsa@fnac.pt";
        Store found = storeRepository.findBystoreEmail(storeEmail);
        assertThat(found).isNull();
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findBystoreEmail(Mockito.anyString());
    }

    @Test
     void whenSearchStoreByNameAndStoreExistsOnDB_ThenReturnStore(){
        String storeName="Fnac";
        Store found = storeRepository.findBystoreName(storeName);
        assertThat(found.getEmail()).isEqualTo("fnac@fnac.pt");
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findBystoreName(Mockito.anyString());
    }
    @Test
     void whenSearchStoreByNameAndStoreDoesntExistsOnDB_ThenReturnNull(){
        String storeName="Fnaaac";
        Store found = storeRepository.findBystoreName(storeName);
        assertThat(found).isNull();
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findBystoreName(Mockito.anyString());
    }


    @Test
     void whenSearchBookByTitleAndBookExistsOnDB_ThenReturnBook(){
        String bookTitle="Branca De neve";
        Book found = bookRepository.findByTitle(bookTitle);

        assertThat(found.getAuthor()).isEqualTo("bob");
        Mockito.verify(bookRepository, VerificationModeFactory.times(1))
                .findByTitle(Mockito.anyString());
    }

    @Test
     void whenSearchBookByTitleAndBookDoesntExistsOnDB_ThenReturnNull(){
        String bookTitle="Amarela De neve";
        Book found = bookRepository.findByTitle(bookTitle);

        assertThat(found).isNull();
        Mockito.verify(bookRepository, VerificationModeFactory.times(1))
                .findByTitle(Mockito.anyString());
    }


    @Test
     void whenSearchForStoresToAcceptAndTheyExist_ThenReturnStoreList(){
        List<Store> found = storeRepository.findByAccepted(0);
        Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
        assertThat(found).contains(store);
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findByAccepted(Mockito.anyInt());
    }

    @Test
     void whenSearchForStoresToAcceptAndTheyDontExist_ThenReturnStoreList(){
        List<Store> found = storeRepository.findByAccepted(1);
        assertThat(found).isEmpty();
        Mockito.verify(storeRepository, VerificationModeFactory.times(1))
                .findByAccepted(Mockito.anyInt());
    }

    @Test
     void whenCreateStore_ThenStoreIsNotAccepted(){
        Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
        assertThat(store.wasAccepted()).isZero();
    }

    @Test
     void whenAcceptingStore_ThenStoreIsAccepted(){
        Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
        store.accept();
        assertThat(store.wasAccepted()).isEqualTo(1);
    }

    @Test
     void testStoreGetByIdAndEquals(){
        Store store = new Store("Fnac","Forum aveiro", "Fanacito","112233","123222222","fnac@fnac.pt");
        Store storeFromEmail = storeRepository.findBystoreEmail(store.getEmail());
        assertThat(store.getId()).isEqualTo(storeFromEmail.getId()); 
    }

    @Test
    void whenLoadAdminByEmailAndItsRightEmailReturnJwtUser(){
      assertThat(adminRepository.findByEmail("admin@service.pt")).isNotNull();
    }



}