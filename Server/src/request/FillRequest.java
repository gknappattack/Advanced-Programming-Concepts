package request;

public class FillRequest {
    /**
     * A String containing a username of a User already stored in teh database.
     */
    private String username;
    /**
     * An int storing the number of generations that the User would like the server to complete and add
     * to the database.
     */
    private int generations;

    /**
     * A constructor for the FillRequest class that sets the username and generation fields to user-specified values.
     * The constructor is used when both username and optional generation parameters are given by the user.
     *
     * @param username The username of the User who's family tree will be filled, should already exist in the database.
     * @param generations The number of generations to fill out starting from the specified User
     */
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    public String getUsername() {
        return username;
    }

    public int getGenerations() {
        return generations;
    }
}
