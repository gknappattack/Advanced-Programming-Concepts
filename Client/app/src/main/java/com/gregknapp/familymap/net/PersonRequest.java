package com.gregknapp.familymap.net;

public class PersonRequest {

    //PersonRequest class to make request from server for all People for logged in user
    private String authToken;

    public PersonRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
