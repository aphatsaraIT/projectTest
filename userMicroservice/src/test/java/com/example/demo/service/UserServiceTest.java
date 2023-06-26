package com.example.demo.service;

import com.example.demo.exception.UserException;
import com.example.demo.pojo.Account;
import com.example.demo.pojo.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResilienceBox resilienceBox;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(resilienceBox); // Pass the mocked resilientBox to the constructor
    }

    @Test
    public void addUserTest_success() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doReturn(user).when(userRepository).save(user);

        User result = userService.addUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(user, result);
    }

    @Test
    public void addUserTest_cannot_data_empty_failed() {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("");

        try {
            userService.addUser(user);
            Assert.fail();
        } catch (UserException e) {
            assertEquals("Data must not be empty.", e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
        
        verify(userRepository, never()).save(user);
    }

    @Test
    public void addUserTest_cannot_connect_db_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doThrow(new RuntimeException("Database connection failed")).when(userRepository).save(user);

        User result = userService.addUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(null, result);
    }

    @Test
    public void getUserTest_success() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        doReturn(userList).when(userRepository).findAll();

        List<User> data = userService.getUser();

        assertEquals(userList.size(), data.size());
        assertEquals(userList.get(0).getUser_id(), data.get(0).getUser_id());
        assertEquals(userList.get(0).getFirst_name(), data.get(0).getFirst_name());
        assertEquals(userList.get(0).getLast_name(), data.get(0).getLast_name());
        assertEquals(userList.get(0).getBirth_date(), data.get(0).getBirth_date());
        assertEquals(userList.get(0).getGender(), data.get(0).getGender());
        assertEquals(userList.get(0).getAddress(), data.get(0).getAddress());
        assertEquals(userList.get(0).getEmail(), data.get(0).getEmail());

        assertEquals(userList.get(1).getUser_id(), data.get(1).getUser_id());
        assertEquals(userList.get(1).getFirst_name(), data.get(1).getFirst_name());
        assertEquals(userList.get(1).getLast_name(), data.get(1).getLast_name());
        assertEquals(userList.get(1).getBirth_date(), data.get(1).getBirth_date());
        assertEquals(userList.get(1).getGender(), data.get(1).getGender());
        assertEquals(userList.get(1).getAddress(), data.get(1).getAddress());
        assertEquals(userList.get(1).getEmail(), data.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void updateUserTest_success() throws Exception{
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        doReturn(user).when(userRepository).findByUser_id(1);
        doReturn(user).when(userRepository).save(user);

        User result = userService.updateUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(user, result);
    }

    @Test
    public void updateUserTest_cannot_data_empty_failed() {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("");

        try {
            userService.updateUser(user);
            Assert.fail();
        } catch (UserException e) {
            assertEquals("Data must not be empty.", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }

        verify(userRepository, never()).save(user);
    }

    @Test
    public void updateUserTest_db_connection_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        doReturn(user).when(userRepository).findByUser_id(1);
        doThrow(new RuntimeException("Database connection failed")).when(userRepository).save(user);

        User result = userService.updateUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(null, result);
    }

    @Test
    public void updateUserTest_data_have_not_db_failed() {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        User user3 = new User();
        user3.setUser_id(3);
        user3.setFirst_name("Aphatsara");
        user3.setLast_name("Moratsathian");
        user3.setBirth_date("2001-06-17");
        user3.setGender("Female");
        user3.setAddress("KMITL");
        user3.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user3.setPhone("0622108493");

        try {
            userService.updateUser(user3);
            verify(userRepository, times(1)).save(user);
            Assert.fail();
        } catch (UserException e){
            assertEquals("User_id have not in the database", e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void deleteUserTest_success() throws Exception{
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        doReturn(user).when(userRepository).findByUser_id(1);
        doNothing().when(userRepository).delete(any(User.class));

        boolean result = userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
        assertTrue(result);
    }

    @Test
    public void deleteUserTest_data_have_not_db_failed() {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        User user2 = new User();
        user2.setUser_id(2);
        user2.setFirst_name("Aphatsara");
        user2.setLast_name("Moratsathian");
        user2.setBirth_date("2001-06-17");
        user2.setGender("Female");
        user2.setAddress("KMITL");
        user2.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user2.setPhone("0926245419");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        User user3 = new User();
        user3.setUser_id(3);
        user3.setFirst_name("Aphatsara");
        user3.setLast_name("Moratsathian");
        user3.setBirth_date("2001-06-17");
        user3.setGender("Female");
        user3.setAddress("KMITL");
        user3.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user3.setPhone("0622108493");

        try {
            userService.deleteUser(user3);
            verify(userRepository, times(1)).delete(user);
            Assert.fail();
        } catch (UserException e){
            assertEquals("User_id have not in the database", e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAccountTest_failed() throws JsonProcessingException {
        CompletableFuture<ResponseEntity<List>> accountResp = CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        );

        doReturn(accountResp).when(resilienceBox).getCircuitBreaker();
        List result = userService.getAccount();
        assertEquals(Collections.emptyList(), result);
        verify(resilienceBox, times(1)).getCircuitBreaker();
    }

    @Test
    public void getAccountTest_success() throws JsonProcessingException {
        List accounts = new ArrayList<>();
        Account account1 = new Account();
        account1.setAccount_id(1);
        account1.setAccount_number("6796846990");
        account1.setAccount_type("Fixed Deposit Account");
        accounts.add(account1);

        Account account2 = new Account();
        account2.setAccount_id(2);
        account2.setAccount_number("0381732925");
        account2.setAccount_type("Fixed Deposit Account");
        accounts.add(account2);

        CompletableFuture<ResponseEntity<List<Account>>> accountResp = CompletableFuture.completedFuture(
                ResponseEntity.ok(accounts)
        );

        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);

//        when(circuitBreakerRegistry.circuitBreaker("accountServiceBreaker")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);

        doReturn(accountResp).when(resilienceBox).getCircuitBreaker();
        List<Account> result = userService.getAccount();

        ObjectMapper objectMapper = new ObjectMapper();
        String expected = objectMapper.writeValueAsString(accounts);
        String actual = objectMapper.writeValueAsString(result);

        assertEquals(expected, actual);
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        verify(resilienceBox, times(1)).getCircuitBreaker();
    }


}
