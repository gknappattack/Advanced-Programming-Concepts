package model;

import java.sql.Timestamp;

public class AuthToken {

    /**
     * Timestamp variable that records when this AuthToken object was created and used.
     */
    private Timestamp timeAccessed;
    /**
     * A String containing the actual AuthToken value used to verify the User.
     */
    private String tokenValue;
    /**
     * A String containing the username of the User that the AuthToken was created for and used by.
     */
    private String associatedUsername;

    /**
     * Constructor for the AuthToken class. Sets fields of timeAccessed, tokenValue, and associatedUsername.
     *
     * @param timeAccessed The time the AuthToken was created and used.
     * @param tokenValue The token value created by the server.
     * @param associatedUsername The username of the User who accessed the AuthToken.
     */
    public AuthToken(String tokenValue, String associatedUsername,
                      Timestamp timeAccessed) {
        this.timeAccessed = timeAccessed;
        this.tokenValue = tokenValue;
        this.associatedUsername = associatedUsername;
    }

    public Timestamp getTimeAccessed() {
        return timeAccessed;
    }

    public void setTimeAccessed(Timestamp timeAccessed) {
        this.timeAccessed = timeAccessed;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    /**
     * An Override version of the equals function for the Users class.
     * Checks each individual data member with another Object o.
     *
     * @param o Object to be compared. If o is of type AuthToken, then it is wrapped for comparison using getter/setter functions.
     * @return Returns false if any non-matches are found, otherwise true if all data members match.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof AuthToken) {
            AuthToken oAuthToken = (AuthToken) o;
            return oAuthToken.getTokenValue().equals(getTokenValue()) &&
                    oAuthToken.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oAuthToken.getTimeAccessed().equals(getTimeAccessed());
        }
        else {
            return false;
        }
    }
}
