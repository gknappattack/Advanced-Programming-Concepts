package request;

public class EventRequest {
    /**
     * The Auth Token object that helps identify the current User who is requesting the Events
     */
    private String authToken;

    /**
     * The constructor for the EventRequest object sets the authToken field. The EventRequest object is passed
     * to the EventService class where the authToken is processed in order to find all events associated with the user
     * connected to the authToken.
     *
     * @param authToken The authToken associated with the user making the request.
     */
    public EventRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
