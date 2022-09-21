package result;

public class LoginResult {
    /**
     * A String containing the value of an AuthToken created upon a successful login.
     */
    private String authToken;
    /**
     * A String containing the username of the User attempting to log in
     */
    private String userName;
    /**
     * A String containing the personID of the Person object associated with the logged in User.
     */
    private String personID;

    /**
     * A boolean containing the result of the LoginService class, true if successful and false if failed.
     */
    private boolean success;
    /**
     * A String containing an error message upon a failed login attempt.
     */
    private String message;

    /**
     * The constructor for successful instances of the LoginResult class that sets the authToken, username, personID,
     * and success fields.
     *
     * @param authToken The value of the AuthToken that was created when this User successfully logged in.
     * @param username The username given by the User that was accepted by the server.
     * @param personID The ID for the Person object created for the new User of the family map.
     * @param success The boolean indicating a successful log in. Should be true.
     */
    public LoginResult(String authToken, String username, String personID, boolean success) {
        this.authToken = authToken;
        this.userName = username;
        this.personID = personID;
        this.success = success;
    }

    /**
     * The constructor for failed instances of the LoginResult class that sets the message and success fields.
     *
     * @param message An error message reporting the reason for the login failure.
     * @param success The boolean indicating a failed log in. Should be false.
     */
    public LoginResult(String message, boolean success) {
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
