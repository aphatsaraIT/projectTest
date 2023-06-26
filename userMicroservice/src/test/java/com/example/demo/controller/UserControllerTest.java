package com.example.demo.controller;

import com.example.demo.exception.UserException;
import com.example.demo.pojo.Account;
import com.example.demo.pojo.Summary;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void addUserAccount_success() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doReturn(user).when(userService).addUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(post("/user/addUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("Add User successfully"))
                        .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);

        verify(userService, times(1)).addUser(any(User.class));
        assertEquals("Add User successfully", response);
    }

    @Test
    public void addUserTest_cannot_data_empty_failed() throws Exception{
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("");

        doReturn(null).when(userService).addUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(objectMapper);

        MvcResult result = mvc.perform(post("/user/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Failed to add user"))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).addUser(any(User.class));
        assertEquals("Failed to add user", response);
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

        doThrow(new UserException("Cannot connect be the database")).when(userService).addUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(post("/user/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Failed to add user"))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).addUser(any(User.class));
        assertEquals("Failed to add user", response);
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

        doReturn(userList).when(userService).getUser();

        MvcResult result = mvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(userList.size()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(user.getUser_id()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].first_name").value(user.getFirst_name()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].last_name").value(user.getLast_name()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].birth_date").value(user.getBirth_date()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(user.getGender()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].address").value(user.getAddress()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(user.getEmail()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].user_id").value(user2.getUser_id()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].first_name").value(user2.getFirst_name()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].last_name").value(user2.getLast_name()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].birth_date").value(user2.getBirth_date()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].gender").value(user2.getGender()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].address").value(user2.getAddress()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value(user2.getEmail()))
                        .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        List<User> responseUsers = objectMapper.readValue(response, new TypeReference<>() {});

        verify(userService, times(1)).getUser();
        assertEquals(userList.size(), responseUsers.size());

        for (int i = 0; i < userList.size(); i++) {
            assertEquals(userList.get(i).getUser_id(), responseUsers.get(i).getUser_id());
            assertEquals(userList.get(i).getFirst_name(), responseUsers.get(i).getFirst_name());
            assertEquals(userList.get(i).getLast_name(), responseUsers.get(i).getLast_name());
            assertEquals(userList.get(i).getBirth_date(), responseUsers.get(i).getBirth_date());
            assertEquals(userList.get(i).getGender(), responseUsers.get(i).getGender());
            assertEquals(userList.get(i).getAddress(), responseUsers.get(i).getAddress());
            assertEquals(userList.get(i).getEmail(), responseUsers.get(i).getEmail());
        }
    }

    @Test
    public void updateUseTest_success() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doReturn(user).when(userService).updateUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(put("/user/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("update User successfully"))
                        .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).updateUser(any(User.class));
        assertEquals("update User successfully", response);
    }

    @Test
    public void updateUserTest_cannot_data_empty_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doReturn(null).when(userService).updateUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(put("/user/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("update User not successfully"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);

        verify(userService, times(1)).updateUser(any(User.class));
        assertEquals("update User not successfully", response);
    }

    @Test
    public void updateUserTest_cannot_connect_db_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doThrow(new UserException("User not found in the database")).when(userService).updateUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(put("/user/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("User not found in the database"))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).updateUser(any(User.class));
        assertEquals("User not found in the database", response);
    }

    @Test
    public void updateUserTest_data_have_not_db_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doThrow(new UserException("User not found in the database")).when(userService).updateUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(put("/user/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("User not found in the database"))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).updateUser(any(User.class));
        assertEquals("User not found in the database", response);
    }

    @Test
    public void deleteUserTest_success() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doReturn(true).when(userService).deleteUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/user/deleteUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("Delete User successfully"))
                        .andReturn();
        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).deleteUser(any(User.class));
        assertEquals("Delete User successfully", response);
        System.out.println("deleteUser Success");
    }

    @Test
    public void deleteUserTest_data_have_not_db_failed() throws Exception {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("Aphatsara");
        user.setLast_name("Moratsathian");
        user.setBirth_date("2001-06-17");
        user.setGender("Female");
        user.setAddress("KMITL");
        user.setEmail("t.aphatsara.moratsathian@scbtechx.io");
        user.setPhone("0622108493");

        doThrow(new UserException("User not found in the database")).when(userService).deleteUser(any(User.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult result = mvc.perform(delete("/user/deleteUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("User not found in the database"))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        verify(userService, times(1)).deleteUser(any(User.class));
        assertEquals("User not found in the database", response);
    }

    @Test
    public void getSummaryTest_success() throws Exception {
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

        Account account = new Account();
        account.setAccount_id(1);
        account.setAccount_number("6796846990");
        account.setAccount_type("deposit");

        Account account2= new Account();
        account2.setAccount_id(2);
        account2.setAccount_number("0381732924");
        account2.setAccount_type("deposit");

        List accountList = new ArrayList<>();
        accountList.add(account);
        accountList.add(account2);

        Summary summary = new Summary(userList, accountList);


        doReturn(userList).when(userService).getUser();
        doReturn(accountList).when(userService).getAccount();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/user/deposit/summary1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.userList").isArray())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.userList.length()").value(userList.size()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.accounts").isArray())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.accounts.length()").value(accountList.size()))
                        .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        Summary responseSummary = objectMapper.readValue(response, Summary.class);

        assertEquals(summary.getUserList().size(), responseSummary.getUserList().size());
        assertEquals(summary.getAccounts().size(), responseSummary.getAccounts().size());
        assertEquals(summary.getUserList().get(0).getUser_id(), responseSummary.getUserList().get(0).getUser_id());
        assertEquals(summary.getUserList().get(0).getFirst_name(), responseSummary.getUserList().get(0).getFirst_name());
        assertEquals(summary.getUserList().get(0).getLast_name(), responseSummary.getUserList().get(0).getLast_name());
        assertEquals(summary.getUserList().get(0).getBirth_date(), responseSummary.getUserList().get(0).getBirth_date());
        assertEquals(summary.getUserList().get(0).getGender(), responseSummary.getUserList().get(0).getGender());
        assertEquals(summary.getUserList().get(0).getAddress(), responseSummary.getUserList().get(0).getAddress());
        assertEquals(summary.getUserList().get(0).getEmail(), responseSummary.getUserList().get(0).getEmail());

        assertEquals(summary.getAccounts().get(0), responseSummary.getAccounts().get(0));
//        assertEquals(summary.getAccounts().get(0).getAccount_number(), responseSummary.getAccounts().get(0).getAccount_number());
//        assertEquals(summary.getAccounts().get(0).getAccount_type(), responseSummary.getAccounts().get(0).getAccount_type());

        verify(userService, times(1)).getAccount();
    }
}
