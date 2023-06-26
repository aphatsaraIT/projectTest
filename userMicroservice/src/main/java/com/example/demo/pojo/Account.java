package com.example.demo.pojo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "account_flyway")
@ToString
public class Account implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int account_id;

    private String account_number;
    private String account_type;

    public Account() {
    }

    public Account(int account_id, String account_number, String account_type) {
        this.account_id = account_id;
        this.account_number = account_number;
        this.account_type = account_type;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public Account(String account_number, String account_type) {
        this.account_number = account_number;
        this.account_type = account_type;
    }
}
