package com.example.openeyes.model;

public class User {
    private String fullName;
    private String nickName;
    private String email;

    public User(String fullName, String nickName, String email) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
