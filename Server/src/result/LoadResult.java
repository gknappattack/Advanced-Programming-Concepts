package result;

public class LoadResult {
    /**
     * A String containing the success or failure message provided by the LoadService class.
     */
    private String message;
    /**
     * A boolean containing the results of the LoadService class method.
     */
    private boolean success;

    /**
     * The constructor for the LoadResult class that sets both the message and success fields, depending on the results
     * of the LoadService class method.
     *
     * @param message A success or error message provided by the LoadService class.
     * @param success A boolean that is true when LoadService succeeded and false when it failed.
     */
    public LoadResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
