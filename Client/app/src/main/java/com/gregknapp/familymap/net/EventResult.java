package com.gregknapp.familymap.net;


import com.gregknapp.familymap.model.Event;

public class EventResult {

    //Event Result class based on server code to facilitate communication with server
    private Event[] data;
    private boolean success;
    private String message;


    public EventResult(Event[] data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public EventResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Event[] getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return this.message;
    }

}
