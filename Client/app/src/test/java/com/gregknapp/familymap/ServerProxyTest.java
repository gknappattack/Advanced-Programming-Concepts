package com.gregknapp.familymap;

import com.gregknapp.familymap.net.EventRequest;
import com.gregknapp.familymap.net.EventResult;
import com.gregknapp.familymap.net.LoginRequest;
import com.gregknapp.familymap.net.LoginResult;
import com.gregknapp.familymap.net.PersonRequest;
import com.gregknapp.familymap.net.PersonResult;
import com.gregknapp.familymap.net.RegisterRequest;
import com.gregknapp.familymap.net.RegisterResult;
import com.gregknapp.familymap.net.ServerProxy;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ServerProxyTest extends TestCase {
    private ServerProxy testServer;

    @BeforeEach
    public void setUp() {
        //Set up server proxy and clear before each test
        testServer = new ServerProxy("localhost", "8080");
        testServer.clear();
    }

    @AfterEach
    public void tearDown() {
        testServer.clear();
    }

    @Test
    public void testLoginPass() {
        //Register a new user
        RegisterRequest registerRequest = new RegisterRequest("sheila", "parker",
                "sheila.p@gmail.com", "Sheila", "Parker", "f");

        RegisterResult registerResult = testServer.register(registerRequest);
        String personID = registerResult.getPersonID();
        String authToken = registerResult.getAuthToken();

        //Run login through Server Proxy
        LoginRequest r = new LoginRequest("sheila", "parker");
        LoginResult result = testServer.login(r);

        //Check all variables of successful login
        assertTrue(result.isSuccess());
        assertEquals(result.getUsername(), "sheila");
        assertEquals(result.getPersonID(), personID);
        assertNotNull(result.getAuthToken());
        assertNull(result.getMessage());
    }

    @Test
    public void testLoginFail() {
        //Log in user a non-existent user
        LoginRequest r = new LoginRequest("greg", "knapp");
        LoginResult result = testServer.login(r);

        //Check all variables returned in failed LoginResult
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().toLowerCase().contains("error"));
    }


    @Test
    public void testRegisterPass() {
        //Register a new user with all valid fields filled out
        RegisterRequest r = new RegisterRequest("greg", "knapp", "greg@gmail.com",
                "Greg", "Knapp", "m");
        RegisterResult result = testServer.register(r);

        //Check all variables returned by a successful login
        assertTrue(result.isSuccess());
        assertNotNull(result.getAuthToken());
        assertNotNull(result.getPersonID());
        assertNull(result.getMessage());
        assertEquals(result.getUsername(), "greg");
    }

    @Test
    public void testRegisterFail() {

        //Register a user with null fields
        RegisterRequest nullUsername = new RegisterRequest(null, "knapp",
                "greg@gmail.com", "Greg", "Knapp", "m");

        RegisterResult nullRegisterResult = testServer.register(nullUsername);

        //Check variables for a failed RegisterResult
        assertNotNull(nullRegisterResult);
        assertFalse(nullRegisterResult.isSuccess());
        assertTrue(nullRegisterResult.getMessage().toLowerCase().contains("error"));

        //Register a duplicate user
        RegisterRequest r = new RegisterRequest("greg", "knapp", "greg@gmail.com",
                "Greg", "Knapp", "m");
        testServer.register(r);

        RegisterResult duplicateRegister = testServer.register(r);

        //Check variables for a failed RegisterResult
        assertNotNull(duplicateRegister);
        assertFalse(duplicateRegister.isSuccess());
        assertTrue(duplicateRegister.getMessage().toLowerCase().contains("error"));

    }

    @Test
    public void testGetAllEventsPass() {
        //Register a new user
        RegisterRequest r = new RegisterRequest("greg", "knapp", "greg@gmail.com",
                "Greg", "Knapp", "m");
        RegisterResult result = testServer.register(r);

        //Get events through server proxy
        EventRequest getEvents = new EventRequest(result.getAuthToken());
        EventResult eventsFound = testServer.getAllEvents(getEvents);

        //Check size - Should equal 91 though default new user creation
        assertTrue(eventsFound.isSuccess());
        assertEquals(eventsFound.getData().length, 91);
        assertNull(eventsFound.getMessage());
    }

    @Test
    public void testGetAllEventsFail() {
        //Get events for invalid auth token value
        EventRequest getEvents = new EventRequest("notvalidauthtoken");
        EventResult eventsFound = testServer.getAllEvents(getEvents);

        //Check boolean and error message for failed event request
        assertFalse(eventsFound.isSuccess());
        assertTrue(eventsFound.getMessage().toLowerCase().contains("error"));
    }

    @Test
    public void testGetAllPersonsPass() {
        //Register a new user
        RegisterRequest r = new RegisterRequest("greg", "knapp", "greg@gmail.com",
                "Greg", "Knapp", "m");
        RegisterResult result = testServer.register(r);

        //Get people through server proxy
        PersonRequest getPeople =new PersonRequest(result.getAuthToken());
        PersonResult peopleFound = testServer.getAllPersons(getPeople);


        //Check size - Should equal 91 though default new user creation
        assertTrue(peopleFound.isSuccess());
        assertEquals(peopleFound.getData().length, 31);
        assertNull(peopleFound.getMessage());
    }

    @Test
    public void testGetAllPersonsFail() {

        //Get people for invalid auth token value
        PersonRequest getPeople = new PersonRequest("notvalidauthtoken");
        PersonResult peopleFound = testServer.getAllPersons(getPeople);

        //Check boolean and error message for failed person result
        assertFalse(peopleFound.isSuccess());
        assertTrue(peopleFound.getMessage().toLowerCase().contains("error"));

    }
}