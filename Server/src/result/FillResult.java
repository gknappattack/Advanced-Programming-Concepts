package result;

public class FillResult {
    /**
     * A String containing the success or failure message provided by the FillService class.
     */
    private String message;
    /**
     * A boolean containing the results of the FillService class method.
     */
    private boolean success;

    /**
     * The constructor for the FillResult class that sets both the message and success fields, depending on the results
     * of the FillService class method.
     *
     * @param message A success or error message provided by the FillService class.
     * @param success A boolean that is true when FillService succeeded and false when it failed.
     */
    public FillResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
