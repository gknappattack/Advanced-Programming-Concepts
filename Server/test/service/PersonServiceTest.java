package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonsDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.PersonRequest;
import request.RegisterRequest;
import result.PersonResult;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {
    private RegisterRequest regRequest;
    private String userName;
    private PersonRequest r;
    private PersonsDAO pDao;
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
        pDao = new PersonsDAO(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    void personResultPass() throws DataAccessException {
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        int personDatabaseSize = pDao.getTableCount();
        r = new PersonRequest(regRslt.getAuthToken());
        assertEquals(personDatabaseSize, 31);

        PersonResult personRslt = PersonService.person(r);

        assertNotNull(personRslt);
        assertTrue(personRslt.isSuccess());
    }

    @Test
    void personResultFail() throws DataAccessException {
        RegisterResult regRslt = RegisterService.register(regRequest);
        String registeredUsername = regRslt.getUsername();
        assertEquals(registeredUsername,userName);

        String correctAuthToken = regRslt.getAuthToken();
        String incorrectAuthToken = "Fake_auth_token";

        r = new PersonRequest(correctAuthToken);
        PersonRequest badRequest = new PersonRequest(incorrectAuthToken);

        PersonResult correctRslt = PersonService.person(r);
        PersonResult incorrectRslt = PersonService.person(badRequest);

        assertNotNull(correctRslt);
        assertNotNull(incorrectRslt);

        assertNotEquals(correctRslt.isSuccess(), incorrectRslt.isSuccess());
    }
}