package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDAO;
import model.Events;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.EventRequest;
import request.RegisterRequest;
import result.EventResult;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private RegisterRequest regRequest;
    private String userName;
    private EventRequest r;
    private EventDAO eDao;
    private Database db;
    private Connection conn;


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
    void eventResultPass() throws DataAccessException {
        //Make valid registration - Create 91 events automatically
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        //Check that the correct number of events were created
        int eventDatabaseSize = eDao.getTableCount();
        r = new EventRequest(regRslt.getAuthToken());
        assertEquals(eventDatabaseSize, 91);

        //Get all events back from eventResult
        EventResult eventResult = EventService.event(r);

        Events[] eventsFound = eventResult.getData();



        //Check that Event Result returned
        assertNotNull(eventResult);

        //Check if EventResult found all events associated with registered users
        assertEquals(eventsFound.length, 91);

        //Check that EventResult returned boolean True
        assertTrue(eventResult.isSuccess());
    }

    @Test
    void eventResultFail() throws DataAccessException {
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        //Get the real Auth Token created upon registration and initialize a fake auth token
        String correctAuthToken = regRslt.getAuthToken();
        String incorrectAuthToken = "Fake_auth_token";

        //Get EventResults for both a good and bad auth token Request
        r = new EventRequest(correctAuthToken);
        EventRequest badRequest = new EventRequest(incorrectAuthToken);

        EventResult correctRslt = EventService.event(r);
        EventResult incorrectRslt = EventService.event(badRequest);

        //Check that EventResult found the user with a correct Auth Token
        assertNotNull(correctRslt);
        //Check that EventResult returned a result with a bad Auth Token
        assertNotNull(incorrectRslt);
        //Check that the incorrect result is false
        assertFalse(incorrectRslt.isSuccess());

        //Check that the correct and incorrect Requests did not return the same
        assertNotEquals(correctRslt.isSuccess(), incorrectRslt.isSuccess());
    }
}