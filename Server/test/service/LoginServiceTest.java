package service;

import dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private RegisterRequest regRequest;
    private LoginRequest loginRequest;
    private String userName;

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
    void loginPass() throws DataAccessException {
        //Make a valid registration to the Server
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        //Attempt log in using information that was just registered
        loginRequest = new LoginRequest(regRequest.getUserName(), regRequest.getPassword());
        LoginResult loginResult = LoginService.login(loginRequest);

        //Check if login was successful
        assertTrue(loginResult.isSuccess());

        //Check if login service created a valid Auth Token
        assertNotNull(loginResult.getAuthToken());
    }

    @Test
    void loginFail() throws DataAccessException {
        //Make a valid registration to the Server
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        //Attempt login with an unregistered username
        loginRequest = new LoginRequest("notregisteredUser", regRequest.getPassword());
        LoginResult loginFail = LoginService.login(loginRequest);

        assertFalse(loginFail.isSuccess());

        //Attempt login with correct username but the wrong password
        loginRequest = new LoginRequest(regRequest.getUserName(), "incorrectpassword");
        loginFail = LoginService.login(loginRequest);

        assertFalse(loginFail.isSuccess());
    }
}