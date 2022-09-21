package com.gregknapp.familymap.net;

public class LoginRequest {

    //LoginRequest class based on same server class to check if a user is in the database
    private String userName;
    private String password;

    public LoginRequest(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
