package model;

import com.google.gson.annotations.SerializedName;

public class Persons {
    /**
     * A String containing the personID for the Person.
     */
    @SerializedName("personID")
    private String personID;
    /**
     * A String contining the username for the user the Person is associated with.
     */
    @SerializedName("associatedUsername")
    private String associateUsername;
    /**
     * A String containing the first name of the Person
     */
    @SerializedName("firstName")
    private String firstName;
    /**
     * A String containing the last name of the Person
     */
    @SerializedName("lastName")
    private String lastName;
    /**
     * A String containing the initial of the gender of the Person (must be "m" or "f" only).
     */
    @SerializedName("gender")
    private String gender;
    /**
     * A String containing the personID of the Person object of this Person's father.
     * OPTIONAL - This field may be null if the father Person object does not exist.
     */
    @SerializedName("fatherID")
    private String fatherID;
    /**
     * A String containing the personID of the Person object of this Person's mother.
     * OPTIONAL - This field may be null if the mother Person object does not exist.
     */
    @SerializedName("motherID")
    private String motherID;
    /**
     * A String containing the personID of the Person object of this Person's spouse.
     * OPTIONAL - This field may be null if the spouse Person object does not exist.
     */
    @SerializedName("spouseID")
    private String spouseID;

    /**
     * Constructor for the Person class. Sets personID, associatedUsername, firstName, lastName, gender, fatherID,
     * motherID, and spouseID fields.
     *
     * @param personID The Person's associated personID from the server.
     * @param associatedUsername The username of the User associated with this Person.
     * @param firstname The first name of the Person.
     * @param lastname The last name of the Person.
     * @param gender The specified gender of the Person.
     * @param fatherID The personID of this Person's father. May be null.
     * @param motherID The personID of this Person's mother. May be null.
     * @param spouseID The personID of this Person's spouse. May be null.
     */
    public Persons(String firstname, String lastname,
                   String gender, String personID,
                   String fatherID, String motherID,
                   String spouseID, String associatedUsername) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.gender = gender;
        this.personID = personID;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.associateUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociateUsername() {
        return associateUsername;
    }

    public void setAssociateUsername(String associateUsername) {
        this.associateUsername = associateUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    /**
     * An Override version of the equals function for the Persons class.
     * Checks each individual data member with another Object o.
     *
     * @param o Object to be compared. If o is of type Persons, then it is wrapped for comparison using getter/setter functions.
     * @return Returns false if any non-matches are found, otherwise true if all data members match.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Persons) {
            Persons oPerson = (Persons) o;
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getAssociateUsername().equals(getAssociateUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender().equals(getGender()) &&
                    oPerson.getFatherID().equals(getFatherID()) &&
                    oPerson.getMotherID().equals(getMotherID()) &&
                    oPerson.getSpouseID().equals(getSpouseID());
        }
        else {
            return false;
        }
    }
}
