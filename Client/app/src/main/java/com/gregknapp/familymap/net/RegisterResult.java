package com.gregknapp.familymap.net;

import com.google.gson.annotations.SerializedName;

public class RegisterResult {

    //RegisterResult class based on same server class to return results of valid and invalid
    //Registrations to the server.
    private String authToken;
    private String userName;
    private String personID;
    private boolean success;
    private String message;

    public RegisterResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public RegisterResult(String authToken, String username,
                          String personID, boolean success) {
        this.authToken = authToken;
        this.userName = username;
        this.personID = personID;
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

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
