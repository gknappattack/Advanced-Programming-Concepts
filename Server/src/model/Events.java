package model;

import com.google.gson.annotations.SerializedName;

public class Events {
    /**
     * A String containing the eventID of the Event object
     */
    @SerializedName("eventID")
    private String eventID;
    /**
     * A String containing the username of the User object that is associated with this event
     */
    @SerializedName("associatedUsername")
    private String associatedUsername;
    /**
     * A String contiaining the personID of the Person object who is associated with this event.
     */
    @SerializedName("personID")
    private String personID;
    /**
     * A float variable that contains the latitude coordinates of the event's location
     */
    @SerializedName("latitude")
    private float latitude;
    /**
     * A float variable that contains the longitude coordiantes of the event's location
     */
    @SerializedName("longitude")
    private float longitude;
    /**
     * A String containing the name of the country that the event took place in.
     */
    @SerializedName("country")
    private String country;
    /**
     * A String containing the name of the city that the event took place in.
     */
    @SerializedName("city")
    private String city;
    /**
     *  A String containing the type of the event
     *  (eventType may include but are not limited to: birth, death, marriage, Christening, and so on).
     */
    @SerializedName("eventType")
    private String eventType;
    /**
     * An int variable that contains the year that the event took place in.
     * Must be in format "XXXX" (e.g. 2020).
     */
    @SerializedName("year")
    private int year;

    /**
     * Constructor for the Event class. Sets eventID, associatedUsername, personID, latitude, longitude, country,
     * city, eventType, and year fields.
     *
     * @param eventID The eventID for this specific event from the server.
     * @param associatedUsername The username of the User associated with this event.
     * @param personID The personID of the Person associated with this event.
     * @param latitude The latitude coordinates of the event location.
     * @param longitude The longitude coordinates of the event location.
     * @param country The name of the country the event happened in.
     * @param city The name of the city the event happened in.
     * @param eventType The type of the event.
     * @param year The year the event took place in.
     */
    public Events(String eventType, String personID,
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

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Events) {
            Events oEvent = (Events) o;
            return oEvent.getEventID().equals(getEventID()) &&
                    oEvent.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oEvent.getPersonID().equals(getPersonID()) &&
                    oEvent.getLatitude() == (getLatitude()) &&
                    oEvent.getLongitude() == (getLongitude()) &&
                    oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getEventType().equals(getEventType()) &&
                    oEvent.getYear() == (getYear());
        }
        else {
            return false;
        }
    }
}