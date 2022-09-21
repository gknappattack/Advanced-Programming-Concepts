package com.gregknapp.familymap.net;

public class LoginResult {

    //Login Result class based on same server class to return result of a login attempt
    private String authToken;
    private String userName;
    private String personID;
    private boolean success;
    private String message;

    public LoginResult(String authToken, String username, String personID, boolean success) {
        this.authToken = authToken;
        this.userName = username;
        this.personID = personID;
        this.success = success;
    }

    public LoginResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

}
