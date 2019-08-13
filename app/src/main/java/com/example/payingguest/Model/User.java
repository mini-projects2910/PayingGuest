package com.example.payingguest.Model;

public class User {
    String username;
    String password;
    String phonenumber;




    public User(String username, String passowrd, String phonenumber) {
        this.username = username;
        this.password = passowrd;
        this.phonenumber = phonenumber;
    }

    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "phonenumber=" + phonenumber +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
