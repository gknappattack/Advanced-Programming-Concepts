package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonsDAO;
import model.Persons;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.PersonIDRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.PersonIDResult;
import result.PersonResult;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PersonIDServiceTest {
    RegisterRequest regRequest;
    String userName;
    PersonRequest r;
    PersonIDRequest personRequest;
    PersonIDResult personIDResult;
    PersonsDAO pDao;
    Database db;
    Connection conn;


    @BeforeEach
    void setUp() throws DataAccessException {
        ClearService.clear();
        db = new Database();

        userName = "gnappattack";
        regRequest = new RegisterRequest(userName, "notmyrealpassword",
                "fakeemail@gmail.com", "Greg", "Knapp", "m");

        conn = db.openConnection();
        pDao = new PersonsDAO(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    void personIDPass() throws DataAccessException {
        //Run a valid register
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        r = new PersonRequest(regRslt.getAuthToken());

        PersonResult personRslt = PersonService.person(r);
        Persons[] databasePeople = personRslt.getData();

        //Get people added during registration in an Array. Run PersonID Service on each person in the database.
        for (int i = 0; i < databasePeople.length; i++) {
            Persons currentPerson = databasePeople[i];

            personRequest = new PersonIDRequest(currentPerson.getPersonID(), regRslt.getAuthToken());

            personIDResult = PersonIDService.personID(personRequest);
            assertTrue(personIDResult.isSuccess());
        }
    }

    @Test
    void personIDFail() throws DataAccessException {
        //Make a valid registration
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);


        //Check if user was correctly added with PersonID
        personRequest = new PersonIDRequest(regRslt.getPersonID(),regRslt.getAuthToken());
        personIDResult = PersonIDService.personID(personRequest);

        assertTrue(personIDResult.isSuccess());

        //Check if test returns false when incorrect PersonID is searched for with a valid auth token
        personRequest = null;
        personRequest = new PersonIDRequest("notvalidid", regRslt.getAuthToken());

        personIDResult = PersonIDService.personID(personRequest);
        assertFalse(personIDResult.isSuccess());

        //Check if test returns false when a valid PersonID is searched with a bad Auth Token
        personRequest = null;
        personRequest = new PersonIDRequest(regRslt.getPersonID(),"badauthtoken");

        personIDResult = PersonIDService.personID(personRequest);
        assertFalse(personIDResult.isSuccess());
    }
}