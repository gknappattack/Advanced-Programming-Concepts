package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDAO;
import model.Events;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class EventIDServiceTest {

    RegisterRequest regRequest;
    String userName;
    EventRequest r;
    EventIDRequest eventIDRequest;
    EventIDResult eventIDResult;
    EventDAO eDao;
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
        eDao = new EventDAO(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    void eventIDPass() throws DataAccessException {
        //Run a valid register
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        r = new EventRequest(regRslt.getAuthToken());

        EventResult eventRslt = EventService.event(r);
        Events[] databaseEvents = eventRslt.getData();

        //Get people added during registration in an Array. Run PersonID Service on each person in the database.
        for (int i = 0; i < databaseEvents.length; i++) {
            Events currentEvent = databaseEvents[i];

            eventIDRequest = new EventIDRequest(currentEvent.getEventID(), regRslt.getAuthToken());

            eventIDResult = EventIDService.eventID(eventIDRequest);
            assertTrue(eventIDResult.isSuccess());
        }
    }

    @Test
    void eventIDFail() throws DataAccessException {
        //Run a valid register
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        r = new EventRequest(regRslt.getAuthToken());

        EventResult eventRslt = EventService.event(r);
        Events[] databaseEvents = eventRslt.getData();

        Events usersEvent = null;

        //Find event in database that belongs to the registered user's person
        for (int i = 0; i < databaseEvents.length; i++) {
            usersEvent = databaseEvents[i];

            if (usersEvent.getPersonID().equals(regRslt.getPersonID())) {
                break;
            }
        }

        //Make sure there are no issues finding an event for the user
        assertNotNull(usersEvent);

        System.out.println(usersEvent.getEventID() + " " + usersEvent.getPersonID());
        System.out.println("Users personid = " + regRslt.getPersonID());

        //Check if user was correctly added with PersonID
        eventIDRequest = new EventIDRequest(usersEvent.getEventID(),regRslt.getAuthToken());
        eventIDResult = EventIDService.eventID(eventIDRequest);

        assertTrue(eventIDResult.isSuccess());

        //Check if test returns false when incorrect EventID is searched for with a valid auth token
        eventIDRequest = null;
        eventIDRequest = new EventIDRequest("notvalidid", regRslt.getAuthToken());

        eventIDResult = EventIDService.eventID(eventIDRequest);
        assertFalse(eventIDResult.isSuccess());

        //Check if test returns false when a valid PersonID is searched with a bad Auth Token
        eventIDRequest = null;
        eventIDRequest = new EventIDRequest(usersEvent.getEventID(),"badauthtoken");

        eventIDResult = EventIDService.eventID(eventIDRequest);
        assertFalse(eventIDResult.isSuccess());
    }
}