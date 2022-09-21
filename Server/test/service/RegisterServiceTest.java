package service;

import dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    private RegisterRequest regRequest;
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
    void registerPass() throws DataAccessException {
        RegisterResult rslt = RegisterService.register(regRequest);
        String registeredUsername = rslt.getUsername();
        assertEquals(registeredUsername,userName);
    }

    @Test
    void registerFail() throws DataAccessException {
        //Attempt registration of the same user two times
        RegisterResult firstRegister = RegisterService.register(regRequest);
        RegisterResult secondRegister = RegisterService.register(regRequest);

        //Make sure the program returns a result
        assertNotNull(secondRegister);
        //Check if the result is not the same as the successful registration
        assertNotEquals(firstRegister, secondRegister);
        //Check if the boolean for the result is false
        assertFalse(secondRegister.isSuccess());
    }

}