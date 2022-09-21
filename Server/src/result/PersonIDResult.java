package result;

public class PersonIDResult {
    /**
     * A String containing of the username of the User associated with the found Person.
     */
    private String associatedUsername;

    /**
     * A String containing the personID of the Person associated with the found Person.
     */
    private String personID;

    /**
     * A String containing the first name of the found Person
     */
    private String firstName;

    /**
     * A String containing the last name of the found Person
     */
    private String lastName;

    /**
     * A String containing the specified gender of the found Person
     */
    private String gender;

    /**
     * A String containing the personID of the found Person's father.
     * May be null if father Person does not exist.
     */
    private String fatherID;

    /**
     * A String containing the personID of the found Person's mother.
     * May be null if mother Person does not exist.
     */
    private String motherID;

    /**
     * A String containing the personID of the found Person's father.
     * May be null if spouse Person does not exist.
     */
    private String spouseID;

    /**
     * A boolean containing the results of the PersonIDService class. True if successful, false if not.
     */
    private boolean success;

    /**
     * A String containing an error message of the PersonIDService class failed to find the Person object.
     */
    private String message;

    /**
     * The constructor for successful instances of the PersonIDResult class that sets the associatedUsername, personID,
     * firstName, lastName, gender, fatherID, motherID, spouseID, and success fields.
     *
     * @param associatedUsername The username of the associated User taken from the found Person.
     * @param personID The personID of the found Person.
     * @param firstName The first name of the found Person.
     * @param lastName The last name of the found Person.
     * @param gender The specified gender of the found Person.
     * @param fatherID The personID of the father Person associated with the found Person.
     * @param motherID The personID of the mother Person associated with the found Person.
     * @param spouseID The personID of the spouse Person associated with the found Person.
     * @param success The result of the PersonIDService class method. Should be true with successful instances.
     */
    public PersonIDResult(String associatedUsername, String personID, String firstName,
                          String lastName, String gender, String fatherID, String motherID, String spouseID,
                          boolean success) {
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = success;
    }

    /**
     * The constructor for failed instances of the PersonIDResult class that sets the success and message fields.
     *
     * @param success The result of the failed request. Should be false.
     * @param message The specific error message detailing the cause of failure.
     */
    public PersonIDResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
