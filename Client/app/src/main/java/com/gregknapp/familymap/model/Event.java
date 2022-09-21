package com.gregknapp.familymap.model;

public class Event {

    //Event class based on Event data stored in Family Map Server
    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public Event(String eventType, String personID,
                 String city, String country, float latitude, float longitude,
                 int year, String eventID, String associatedUsername) {
        this.eventType = eventType;
        this.personID = personID;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.year = year;
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
    }

    public String getEventID() {
        return eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEventType() {
        return eventType;
    }

    public int getYear() {
        return year;
    }
}
