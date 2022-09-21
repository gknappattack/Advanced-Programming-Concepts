package result;

import model.Events;

public class EventResult {

    /**
     * The Array of Events found by the EventService class.
     */
    private Events[] data;
    /**
     * A boolean containing the results of the EventService class method.
     */
    private boolean success;
    /**
     * A string containing the success or failure of the EventService class.
     */
    private String message;

    /**
     * The constructor for successful instances of the EventResult class that sets the data and success fields only.
     *
     * @param data The Array of Event objects that were found by the EventService class using the given AuthToken.
     * @param success The results of the EventService class method. Should be true in successful instances.
     */
    public EventResult(Events[] data, boolean success) {
        this.data = data;
        this.success = success;
    }

    /**
     * The constructor for failed instances of the EventResult class that sets the success and message fields only.
     *
     * @param success The results of the EventService class method. Should be false in failed instances.
     * @param message The error message provided by the EventService class.
     */
    public EventResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Events[] getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
