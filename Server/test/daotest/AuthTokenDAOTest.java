package daotest;

import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenDAOTest {
    private Database db;
    private Timestamp testTimeStamp;
    private Timestamp similarTimeStamp;
    private AuthToken testToken;
    private AuthToken similarToken;
    private AuthTokenDAO aDao;


    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        testTimeStamp = new Timestamp(System.currentTimeMillis());
        similarTimeStamp = new Timestamp(System.currentTimeMillis());

        testToken = new AuthToken("AJei32DLel", "gnappattack", testTimeStamp);
        similarToken = new AuthToken("AJEi32DLeL", "gnappattack", similarTimeStamp);

        Connection conn = db.getConnection();
        aDao = new AuthTokenDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void addAuthTokenPass() throws DataAccessException {
        //Add an Auth Token, then attempt to search for it.
        aDao.addAuthToken(testToken);
        AuthToken compareToken = aDao.findAuthToken(testToken.getTokenValue());

        //Check that result is non-null
        assertNotNull(compareToken);
        //Check that found token is the original
        assertEquals(testToken, compareToken);
    }

    @Test
    public void addAuthTokenFail() throws DataAccessException {
        //Force a throw by adding the same token twice
        aDao.addAuthToken(testToken);
        assertThrows(DataAccessException.class, () -> aDao.addAuthToken(testToken));
    }

    @Test
    public void findAuthTokenPass() throws DataAccessException {
        //Add two Auth Tokens with similar values
        aDao.addAuthToken(testToken);
        aDao.addAuthToken(similarToken);

        //Run a find on testToken
        AuthToken foundToken = aDao.findAuthToken(testToken.getTokenValue());

        //Check that the result is non-null
        assertNotNull(foundToken);
        //Check that the result is testToken, not similarToken
        assertEquals(testToken, foundToken);
    }

    @Test
    public void findAuthTokenFail() throws DataAccessException, SQLException {
        //Add testToken, make a good search with the correct value and a bad search with the wrong value
        aDao.addAuthToken(testToken);
        AuthToken foundToken = aDao.findAuthToken(testToken.getTokenValue());
        AuthToken notFoundToken = aDao.findAuthToken(similarToken.getTokenValue());

        //Check if the good search returned testToken and that the bad search did not
        assertEquals(testToken, foundToken);
        assertNotEquals(testToken, notFoundToken);

        //Force a throw by running findAuthToken in a non-existent database.
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        AuthTokenDAO badDao = new AuthTokenDAO(badConnection);
        assertThrows(DataAccessException.class, () -> badDao.findAuthToken(testToken.getTokenValue()));
    }

    @Test
    public void clearAuthTokenTablePass() throws DataAccessException {
        //Add one Token, clear tables, then check if size is 0
        aDao.addAuthToken(testToken);
        assertEquals(1, aDao.getTableCount());

        aDao.clearAuthTokenTable();
        assertEquals(0, aDao.getTableCount());

        //Add 2 Tokens, clear tables, then check if size is 0 again.
        aDao.addAuthToken(testToken);
        aDao.addAuthToken(similarToken);

        assertEquals(2, aDao.getTableCount());
        aDao.clearAuthTokenTable();
        assertEquals(0, aDao.getTableCount());
    }

    @Test
    public void removeAuthTokenPass() throws DataAccessException {
        //Add a token, then remove it by value and check if size is 0
        aDao.addAuthToken(testToken);
        assertEquals(1, aDao.getTableCount());
        aDao.removeAuthToken(testToken.getTokenValue());
        assertEquals(0, aDao.getTableCount());

        //Add two tokens, remove testToken by value and check if size is 1
        aDao.addAuthToken(testToken);
        aDao.addAuthToken(similarToken);

        aDao.removeAuthToken(similarToken.getTokenValue());
        assertEquals(1, aDao.getTableCount());
        assertNotEquals(similarToken, aDao.findAuthToken(similarToken.getTokenValue()));
    }

    @Test
    public void removeAuthTokenFail() throws DataAccessException, SQLException {
        //Add testToken, attempt remove on non-existent token value. Check if nothing was removed.
        aDao.addAuthToken(testToken);
        aDao.removeAuthToken(similarToken.getTokenValue());

        assertEquals(1, aDao.getTableCount());

        //Force a throw by removing from a non-existent database.
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        AuthTokenDAO badDao = new AuthTokenDAO(badConnection);
        assertThrows(DataAccessException.class, () -> badDao.findAuthToken(testToken.getTokenValue()));
    }

    @Test
    public void getTableCount() throws DataAccessException {
        //Add a token, then remove it by value and check if size is 0
        aDao.addAuthToken(testToken);
        assertEquals(1, aDao.getTableCount());
        aDao.removeAuthToken(testToken.getTokenValue());
        assertEquals(0, aDao.getTableCount());

        //Add two tokens, remove testToken by value and check if size is 1
        aDao.addAuthToken(testToken);
        aDao.addAuthToken(similarToken);

        assertEquals(2, aDao.getTableCount());
    }

    @Test
    public void getTableCountFail() throws DataAccessException, SQLException {
        //Force a throw by attempting to getsize of an non-existent database
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        AuthTokenDAO badDao = new AuthTokenDAO(badConnection);
        assertThrows(DataAccessException.class, badDao::getTableCount);
    }
}