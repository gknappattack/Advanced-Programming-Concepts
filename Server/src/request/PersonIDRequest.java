package request;

public class PersonIDRequest {

    /**
     * A String containing the personID to be searched for in the Persons database.
     */
    private String personID;
    private String authTokenValue;

    public String getAuthTokenValue() {
        return authTokenValue;
    }

    /**
     * The constructor for the PersonIDRequest class that sets the personID field.
     * The request is then passed to the PersonIDService class for processing.
     *
     * @param personID The personID to be searched for in the class
     */
    public PersonIDRequest(String personID, String authTokenValue) {
        this.personID = personID;
        this.authTokenValue = authTokenValue;
    }

    public String getPersonID() {
        return personID;
    }
}
