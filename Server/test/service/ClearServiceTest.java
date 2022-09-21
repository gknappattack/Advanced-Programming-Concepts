package service;

import dao.DataAccessException;
import dao.Database;
import dao.UsersDAO;
import model.Persons;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.PersonRequest;
import request.RegisterRequest;
import result.ClearResult;
import result.PersonResult;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private RegisterRequest regRequest;
    private ClearResult clearResult;

    @BeforeEach
    void setUp() throws DataAccessException {
        ClearService.clear();

        regRequest = new RegisterRequest("gnapp", "notmypassword", "fakeemail@gmail.com",
                "Greg", "Knapp", "m");

    }

    @AfterEach
    void tearDown() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    void clearPass() throws DataAccessException {
        //Register and fill the Database
        RegisterResult regResult = RegisterService.register(regRequest);

        PersonRequest personRequest = new PersonRequest(regResult.getAuthToken());
        PersonResult personResult = PersonService.person(personRequest);

        Persons[] peoplePreClear = personResult.getData();

        //Check that people were added to the database
        assertTrue(peoplePreClear.length > 0);

        clearResult = ClearService.clear();

        //Attempt to find Persons in database after clear
        personResult = PersonService.person(personRequest);

        //Assert that person result failed when searching the cleared database
        assertFalse(personResult.isSuccess());
        //Assert that clear result returned a true result
        assertTrue(clearResult.isSuccess());
    }

    @Test void clearFail()  throws DataAccessException {
        //Make Database busy by opening connection inside of the test in test to force throw
        Database db = new Database();
        Connection conn = db.openConnection();
        UsersDAO uDao = new UsersDAO(conn);

        uDao.getTableCount();

        clearResult = ClearService.clear();
        assertFalse(clearResult.isSuccess());

        db.closeConnection(false);
    }
}