package com.example.demo.controller;



import com.example.demo.config.Configuration;
import com.example.demo.exception.UserException;
import com.example.demo.pojo.Account;
import com.example.demo.pojo.Summary;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RefreshScope
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${user.message}")
    String message;
    private static final Logger logger = Logger.getLogger(UserController.class);

    public UserController() {
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/endpoint")
    public String retrieveLimits(){
        return message;
    }

    @RequestMapping(value ="/user/addUser", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody User user){
        try {
            User response = userService.addUser(user);
            if (response != null) {
                return ResponseEntity.ok("Add User successfully");
            } else {
                throw new UserException("Add User not successfully");
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user");
        }
    }

    @RequestMapping(value ="/user/updateUser", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            User response = userService.updateUser(user);
            System.out.println(response);
            if (response != null) {
                return  ResponseEntity.ok("update User successfully");
            } else {
                throw new UserException("update User not successfully");
            }
        } catch (UserException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    @GetMapping("/user")
    public List<User> getUser() throws Exception {
        try {
            List<User> userList = userService.getUser();
            logger.info("Endpoint : "+message);
            return userList;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/user/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody User user){
        try {
            boolean response = userService.deleteUser(user);
            if(response) {
                return ResponseEntity.ok("Delete User successfully");
            } else {
                throw new UserException("Delete User not successfully");
            }
        } catch (UserException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

    @GetMapping("/user/deposit/summary1")
    public Summary getSummary1() throws UserException {
        try {
            List<User> userList = userService.getUser();
            List accounts = userService.getAccount();
            System.out.println(accounts);
            return new Summary(userList, accounts);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new UserException("Error summary : "+e.getMessage());
        }
    }

    @GetMapping("/user/getUserById/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int accountId) throws UserException {
        try{
            User user = userService.getFindById(accountId);
            logger.info("get user by user_id success");
            return ResponseEntity.ok(user);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new UserException(e.getMessage());
        }
    }

//    @GetMapping("/breaker")
//    public String serviceBDown() throws Exception {
//        return userService.getStringServiceB();
//    }
}
