package com.example.demo.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Summary implements Serializable {
    private List<User> userList;
    private List accounts;

    public Summary() {
    }

    public Summary(List<User> userList, List accounts) {
        this.userList = userList;
        this.accounts = accounts;
    }
}
