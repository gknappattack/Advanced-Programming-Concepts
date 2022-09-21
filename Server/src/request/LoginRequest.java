package request;

public class LoginRequest {
    /**
     * A String containing the username of the User trying to log in. Should exist in database already.
     */
    private String userName;
    /**
     * A String containing the username of the User trying to log in. Should exist in database and be associated with
     * same User object at the username.
     */
    private String password;

    /**
     * The constructor for the LoginRequest class that sets the username and password fields. The LoginRequest
     * is created to be passed onto the LoginService field for confirmation and processing.
     *
     * @param username The given username to attempt to login with.
     * @param password The given password to attempt to login with.
     */
    public LoginRequest(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
