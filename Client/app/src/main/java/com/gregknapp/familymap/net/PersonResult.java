package com.gregknapp.familymap.net;

import com.gregknapp.familymap.model.Person;

public class PersonResult {

    //PersonResult class based on same server class containing an Array of all people for
    //The logged in user of the app
    private Person[] data;
    private boolean success;
    private String message;

    public PersonResult(Person[] data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public PersonResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Person[] getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
