package result;

import model.Persons;

public class PersonResult {
    /**
     * The Array of Persons found by the PersonService class.
     */
    private Persons[] data;

    /**
     * A boolean containing the results of the PersonService class method.
     */
    private boolean success;

    /**
     * A string containing the success or failure of the PersonService class.
     */
    private String message;

    /**
     * The constructor for successful instances of the PersonResult class that sets the data and success fields only.
     *
     * @param data The Array of Persons objects that were found by the PersonService class using the given AuthToken.
     * @param success The results of the PersonService class method. Should be true in successful instances.
     */
    public PersonResult(Persons[] data, boolean success) {
        this.data = data;
        this.success = success;
    }

    /**
     * The constructor for failed instances of the PersonResult class that sets the success and message fields only.
     *
     * @param success The results of the PersonService class method. Should be false in failed instances.
     * @param message The error message provided by the PersonService class.
     */

    public PersonResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Persons[] getData() {
        return data;
    }
}
