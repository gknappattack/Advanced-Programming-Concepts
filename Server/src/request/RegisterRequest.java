package request;

public class RegisterRequest {
    /**
     * A String containing the username for the new User
     */
    private String userName;

    /**
     * A String containing the password for the new User.
     */
    private String password;

    /**
     * A String containing the new User's email
     */
    private String email;

    /**
     * A String containing the first name of the new User.
     */
    private String firstName;

    /**
     * A String containing the last name of the new User.
     */
    private String lastName;

    /**
     * A String containing the specified gender of the new User (Must be either "m" or "f").
     */
    private String gender;

    /**
     * The constructor for the RegisterRequest class that sets the fields username, password, email, firstName,
     * lastName, and gender. The request is created to be passed to and processed by the RegisterService class.
     *
     * No parameters may be null.
     *
     * @param username The given username for a new account.
     * @param password The given password for a new account.
     * @param email The given email for a new account.
     * @param firstName The first name of the user of the new account.
     * @param lastName The last name of the user of the new account.
     * @param gender The specified gender of the user of the new account.
     */
    public RegisterRequest(String username, String password, String email,
                    String firstName, String lastName, String gender) {
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName=userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName=firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName=lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender=gender;
    }
}
