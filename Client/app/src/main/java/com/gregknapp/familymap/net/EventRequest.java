package com.gregknapp.familymap.net;

public class EventRequest {

    //Event Request based on server code class to make request for all events
    private String authToken;
    public EventRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
