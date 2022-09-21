package com.gregknapp.familymap.model;

public class Person {

    //Person class reflecting Person data stored in the family map Server
    private String personID;
    private String associateUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    public String getPersonID() {
        return personID;
    }

    public String getAssociateUsername() {
        return associateUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public Person(String firstname, String lastname,
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

}
