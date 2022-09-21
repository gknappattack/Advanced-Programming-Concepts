package result;

import com.google.gson.annotations.Expose;

public class EventIDResult {
    /**
     * A String containing the eventID of the found event
     */
    private String eventID;
    /**
     * A String containing of the username of the User associated with the found Event.
     */
    private String associatedUsername;
    /**
     * A String containing the personID of the Person associated with the found Event.
     */
    private String personID;
    /**
     * A float with the latitude coordinates of the found Event.
     */
    private float latitude;
    /**
     * A float with the longitude coordinates of the found Event.
     */
    private float longitude;
    /**
     * A String with the country name of the location of the found Event.
     */
    private String country;
    /**
     * A String with the city name of the location of the found Event.
     */
    private String city;
    /**
     * A String with the event type of the found Event.
     */
    private String eventType;
    /**
     * An int variable with the 4-digit year of the found Event.
     */
    private int year;
    /**
     * A boolean containing the results of the EventIDRequest given by the EventIDService class.
     * Expose tag added to ensure the correct variables are serialized when a failed EventIDRequest is performed.
     */
    @Expose
    private boolean success;
    /**
     * A String containing a succcess message if the EventIDRequest succeeded or an error message if it failed.
     * Expose tag added to ensure the correct variables are serialized when a failed EventIDRequest is performed.
     */
    @Expose
    private String message;

    /**
     * The constructor of successful instances of the EventIDResult class as reported by the EventIDService class that sets
     * eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year, and success fields.
     *
     * @param eventID The eventID of the successfully found Event.
     * @param associatedUsername The username of the associated User taken from the successfully found Event.
     * @param personID The personID of the associated Person taken from the successfully found Event.
     * @param latitude The latitude coordinates taken from the successfully found Event.
     * @param longitude The longitude coordiantes taken from the successfullyh found Event.
     * @param country The country name taken from the successfully found Event.
     * @param city The city name taken from the successfully found Event.
     * @param eventType The event type taken from the successfully found Event.
     * @param year The 4-digit year taken from the successfully found Event.
     * @param success The boolean given by the EventIDService class. Should be true in successful instances.
     */
    public EventIDResult(String eventType, String personID, String city, String country,
                         float latitude, float longitude, int year, String eventID, String associatedUsername, boolean success) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        this.success = success;
    }

    /**
     * The constructor for failed instances of the EventIDResult class that sets the success and message fields only.
     *
     * @param success The boolean indicating the result of the EventIDService class. Should be false in failed instances.
     * @param message The message containing the specific error or details of the failed request.
     */
    public EventIDResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public int getYear() {
        return this.year;
    }

    public boolean isSuccess() {
        return success;
    }
}
