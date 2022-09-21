package request;

public class EventIDRequest {
    /**
     * A String containing the eventID that will be used to find the associated Event object.
     */
    private String eventID;
    private String authTokenValue;

    public String getEventID() {
        return eventID;
    }
    public String getAuthTokenValue() {
        return authTokenValue;
    }

    /**
     * The constructor of the EventIDRequest class sets the eventID field to be searched for in the database
     * so the request can be sent on to the EventIDService class for processing.
     *
     * @param eventID The eventID that will be searched for in the database.
     */
    public EventIDRequest(String eventID, String authTokenValue) {
        this.eventID = eventID;
        this.authTokenValue = authTokenValue;
    }

}
