package result;

public class ClearResult {
    /**
     * A String containing the success or failure message from the ClearService class.
     */
    private String message;
    /**
     * A boolean containing true if the Clear succeeded or false if it failed.
     */
    private boolean success;

    /**
     * The constructor for the ClearResult class that sets the messaage and success fields with values recieved from
     * the ClearService class.
     *
     * @param message The success or error message to return to the user.
     * @param success The boolean indicating if the clear succeeded or failed.
     */
    public ClearResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
