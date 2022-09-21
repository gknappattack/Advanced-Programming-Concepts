package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDAO;
import dao.PersonsDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffmodels.Person;
import request.FillRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.FillResult;
import result.LoginResult;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class FillServiceTest {

    private RegisterRequest regRequest;
    private FillRequest fillRequest;
    private String userName;
    private Database db;
    private Connection conn;
    private EventDAO eDao;
    private PersonsDAO pDao;

    @BeforeEach
    void setUp() throws DataAccessException {
        ClearService.clear();

        userName = "gnappattack";
        regRequest = new RegisterRequest(userName, "notmyrealpassword",
                "fakeemail@gmail.com", "Greg", "Knapp", "m");
    }

    @AfterEach
    void tearDown() throws DataAccessException {
       ClearService.clear();
    }

    @Test
    void fillPass() throws DataAccessException {
        //Make a valid registration to the Server
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        //Attempt 4 generation fill - same as when registering
        fillRequest = new FillRequest(userName, 4);
        FillResult fillResult = FillService.fill(fillRequest);

        //Check if fill result is true
        assertTrue(fillResult.isSuccess());


        //Try fill with value other than default 4 generations
        fillRequest = new FillRequest(userName, 2);
        fillResult = FillService.fill(fillRequest);

        //Check if smaller fill still returns true
        assertTrue(fillResult.isSuccess());
    }

    @Test
    void fillFail() throws DataAccessException {
        //Attempt Fill on user that is not registered in the database

        fillRequest = new FillRequest("notrealusername", 4);
        FillResult fillResult = FillService.fill(fillRequest);

        assertFalse(fillResult.isSuccess());
    }
}