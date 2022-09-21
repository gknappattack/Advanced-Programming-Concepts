package request;

public class PersonRequest {

    /**
     * The Auth Token object that is used to identify the current User who is requesting the Persons
     */
    private String authToken;

    /**
     * The constructor for the PersonRequest object sets the authToken field. The PersonRequest object is passed
     * to the PersonService class where the authToken is processed in order to find all Persons associated with the User
     * connected to the authToken.
     *
     * @param authToken The authToken associated with the user making the request.
     */
    public PersonRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
