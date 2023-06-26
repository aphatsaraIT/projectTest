package com.example.demo.service;



import com.example.demo.controller.UserController;
import com.example.demo.exception.UserException;
import com.example.demo.pojo.Account;
import com.example.demo.pojo.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResilienceBox resilientBox;
    private RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(UserController.class);

    public UserService() {
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService(ResilienceBox resilientBox) {
        this.resilientBox = resilientBox;
    }

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserService(UserRepository userRepository, ResilienceBox resilientBox, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.resilientBox = resilientBox;
        this.restTemplate = restTemplate;
    }

    @Cacheable("userList")
    public List<User> getUser() throws Exception {
        try {
            List<User> resp = userRepository.findAll();

            if(resp.isEmpty()){
                return null;
            }

            logger.info("get user success");
            return resp;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }

    @Caching(evict = { @CacheEvict(value = "userList", allEntries = true), },
            put = { @CachePut(value = "user", key = "#user.user_id") })
    public User addUser(User user) throws Exception {
        if (user.getFirst_name().isEmpty()|| user.getLast_name().isEmpty() || user.getBirth_date().isEmpty() || user.getGender().isEmpty() || user.getAddress().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty()){
            throw new UserException("Data must not be empty.");
        }
        try {
            userRepository.save(user);
            logger.info("add user into database success");
            return user;
        }catch (Exception e){
            logger.error("Data not correct or database can't connect : "+e.getMessage());
            return null;
        }
    }

    @Caching(evict = { @CacheEvict(value = "userList", allEntries = true), },
            put = { @CachePut(value = "user", key = "#user.user_id") })
    public User updateUser(User user) throws Exception {
        if (user.getFirst_name().isEmpty()|| user.getLast_name().isEmpty() || user.getBirth_date().isEmpty() || user.getGender().isEmpty() || user.getAddress().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty()){
            throw new UserException("Data must not be empty.");
        }
        User userRes = userRepository.findByUser_id(user.getUser_id());

        if (userRes == null) {
            throw new UserException("User_id have not in the database");
        }

        try {
            userRepository.save(user);
            logger.info("update user success");
            return user;
        } catch (Exception e) {
            logger.error("Data wrong or database can't connect : "+ e.getMessage());
            return null;
        }
    }

    @Caching(evict = { @CacheEvict(value = "userList", allEntries = true),

            @CacheEvict(value = "user", key = "#user.user_id"), })
    public boolean deleteUser(User user) throws Exception {
        if (user.getFirst_name().isEmpty()|| user.getLast_name().isEmpty() || user.getBirth_date().isEmpty() || user.getGender().isEmpty() || user.getAddress().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty()){
            throw new UserException("Data must not be empty.");
        }
        User userRes = userRepository.findByUser_id(user.getUser_id());
        if (userRes == null) {
            throw new UserException("User_id have not in the database");
        }

        try {
            userRepository.delete(user);
            logger.info("delete user success");
            return true;
        } catch (Exception e) {
            logger.error("Data wrong or database can't connect : "+ e.getMessage());
            return false;
        }
    }

    public List<Account> getAccountWebClient(){
        try {
            List<Account> accounts = WebClient.create()
                    .get()
                    .uri("http://localhost:8083/account")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            return accounts;
        }catch (Exception e){
            return null;
        }
    }

//    public List<Account> getAccount() throws Exception {
//        try {
//            restTemplate = new RestTemplate();
//            String urlAccount = "http://localhost:8083/account";
//            ResponseEntity<List<Account>> response = restTemplate.exchange(
//                    urlAccount,
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<Account>>() {}
//            );
//            if (response.getStatusCode().is2xxSuccessful()) {
//                List<Account> accounts = response.getBody();
//                logger.info("Get account success this account data : "+accounts);
//                return accounts;
//            } else {
//                logger.error("Account API can't connected");
//                throw new UserException("Account API can't connected");
//            }
//        } catch (Exception e) {
//            logger.error("Data wrong or database can't connect : "+ e.getMessage());
//            throw new UserException("Account API can't connected");
//        }
//    }

    @Cacheable("accountList")
    public List getAccount() throws JsonProcessingException {
        CompletableFuture<ResponseEntity<List>> accountResp = resilientBox.getCircuitBreaker().toCompletableFuture();
        System.out.println("msg test " + accountResp.join().getStatusCode().value());
        if (accountResp.join().getStatusCode().value() != HttpStatus.OK.value()) {
            logger.warn("Status code not equal 200 because Service Account Down");
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(accountResp.join().getBody());
        System.out.println(json);
        return objectMapper.readValue(json, new TypeReference<List>() {});
    }

//    public String getStringServiceB() throws Exception {
//        ResponseEntity<String> resultEntity = resilientBox.getCircuitBreaker1();
//
//        if(resultEntity.getStatusCode().value() != HttpStatus.OK.value()) {
//            System.out.println("Status code not equal 200 because Service B Down");
//            return "Service B Down";
//        }
//        return resultEntity.getBody();
//    }

    @Cacheable(value = "user", key = "#user_id", unless = "#result==null")
    public User getFindById(int user_id) throws UserException {
        try {
            User user = userRepository.findByUser_id(user_id);
            if (user == null) {
                throw new UserException("user_id have not in the database");
            } else {
                return user;
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new UserException(e.getMessage());
        }
    }
}
