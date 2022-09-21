package daotest;

import model.Users;
import dao.DataAccessException;
import dao.Database;
import dao.UsersDAO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UsersDAOTest {
    private Database db;
    private Users testUser;
    private Users firstSimilarUser;
    private Users secondSimilarUser;
    private Users lastSimilarUser;
    private UsersDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        testUser = new Users("gnappattack", "This1sAs3cUr3passWORD", "test@user.com",
                "George", "Franklin", "m","gnappattack111");
        firstSimilarUser = new Users("gnapattack", "This1sAs3cUr3passWORD", "test@user.com",
                "George", "Franklin", "m","gnappattack111");
        secondSimilarUser = new Users("gnapppattack", "This1sAs3cUr3passWORD", "test@user.com",
                "George", "Franklin", "m","gnappattack111");
        lastSimilarUser = new Users("gnappattac", "This1sAs3cUr3passWORD", "test@user.com",
                "George", "Franklin", "m","gnappattack111");

        Connection conn = db.getConnection();
        uDao = new UsersDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void addUserPass() throws DataAccessException {
        //Add a user, then attempt to find in database
        uDao.addUser(testUser);
        Users compareUser = uDao.findUser(testUser.getUsername());

        //Check if result is non-null
        assertNotNull(compareUser);
        //Check if result is original user
        assertEquals(testUser, compareUser);
    }

    @Test
    public void addUserFail() throws DataAccessException {
        //Add user, then force a throw by trying to add user a second time.
        uDao.addUser(testUser);
        assertThrows(DataAccessException.class, ()-> uDao.addUser(testUser));
    }

    @Test
    public void findUserPass() throws DataAccessException {
        //Add 4 similar Users
        uDao.addUser(testUser);
        uDao.addUser(firstSimilarUser);
        uDao.addUser(secondSimilarUser);
        uDao.addUser(lastSimilarUser);

        //Try to find original user
        Users foundUser = uDao.findUser(testUser.getUsername());

        //Check if findUser returned non-null
        assertNotNull(foundUser);
        //Check if result is the original user
        assertEquals(testUser, foundUser);

    }

    @Test
    public void findUsersFail() throws DataAccessException, SQLException {
        //Add original user, make a good with correct username and a bad search with a fake one
        uDao.addUser(testUser);
        Users foundUser = uDao.findUser(testUser.getUsername());
        Users notFoundUser = uDao.findUser("gnappattac");

        //Check a valid find, followed by a find that did not make a match.
        assertEquals(testUser, foundUser);
        assertNotEquals(testUser, notFoundUser);

        //Test find function into bad database to check catch SQLException and throwing DataAccessException
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        UsersDAO badDao = new UsersDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findUser(testUser.getUsername()));

    }

    @Test
    public void clearUserTablePass() throws DataAccessException {
        //Check single add
        uDao.addUser(testUser);
        assertEquals(1,uDao.getTableCount());

        //Check if single user was deleted from table
        uDao.clearUsersTable();
        assertEquals(0,uDao.getTableCount());

        uDao.addUser(testUser);
        uDao.addUser(firstSimilarUser);
        uDao.addUser(secondSimilarUser);
        uDao.addUser(lastSimilarUser);

        //Check if large add worked then check if all are deleted
        assertEquals(4, uDao.getTableCount());
        uDao.clearUsersTable();
        assertEquals(0, uDao.getTableCount());
    }

    @Test
    public void removeUserPass() throws DataAccessException {
        //Add original user, check size, remove then check if size is now 0 again
        uDao.addUser(testUser);

        assertEquals(1, uDao.getTableCount());
        uDao.removeUser(testUser.getUsername());
        assertEquals(0, uDao.getTableCount());

        //Add 4 users, remove one then check size if = 3
        uDao.addUser(testUser);
        uDao.addUser(firstSimilarUser);
        uDao.addUser(secondSimilarUser);
        uDao.addUser(lastSimilarUser);

        uDao.removeUser(firstSimilarUser.getUsername());

        assertEquals(3, uDao.getTableCount());
        //Check that removed user cannot be found in the database again.
        assertNotEquals(firstSimilarUser,uDao.findUser(firstSimilarUser.getUsername()));
    }

    @Test
    public void removeUserFail()  throws DataAccessException, SQLException {
        //Add one user to the database, then person an invalid remove on non-existent Person
        uDao.addUser(testUser);
        uDao.removeUser(firstSimilarUser.getUsername());

        //Test function when remove is run on user that does not exist
        assertEquals(1, uDao.getTableCount());

        //Test find function into bad database to check catch SQLException and throwing DataAccessException
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        UsersDAO badDao = new UsersDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findUser(testUser.getUsername()));
    }

    @Test
    public void getTableCount() throws DataAccessException {
        //Add one User, check if size is 1, remove, then check if size if 0
        uDao.addUser(testUser);

        assertEquals(1, uDao.getTableCount());
        uDao.removeUser(testUser.getUsername());
        assertEquals(0, uDao.getTableCount());

        //Add 4 users, remove one then check size if = 4
        uDao.addUser(testUser);
        uDao.addUser(firstSimilarUser);
        uDao.addUser(secondSimilarUser);
        uDao.addUser(lastSimilarUser);

        assertEquals(4, uDao.getTableCount());
    }

    @Test
    public void getTableCountFail() throws DataAccessException, SQLException {
        //Test getTableCount into bad database to check catch SQLException and throwing DataAccessException
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        UsersDAO badDao = new UsersDAO(badConnection);
        assertThrows(DataAccessException.class, badDao::getTableCount);
    }

}