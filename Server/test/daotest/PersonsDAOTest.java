package daotest;

import dao.DataAccessException;
import dao.Database;
import dao.PersonsDAO;
import model.Persons;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonsDAOTest {
    private Database db;
    private Persons testPerson;
    private Persons firstSimilarPerson;
    private Persons secondSimilarPerson;
    private Persons lastSimilarPerson;
    private PersonsDAO pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        testPerson = new Persons("George", "Franklin", "m", "greggy", "cdks111",
                "mk123", "gggg101", "gnappattack");
        firstSimilarPerson = new Persons("George", "Franklin", "m", "hello", "cdks111",
                "mk123", "gggg101", "gnappattack");
        secondSimilarPerson = new Persons("George", "Franklin", "m", "is", "cdks111",
                "mk123", "gggg101", "gnappattack11");
        lastSimilarPerson = new Persons("George", "Franklin", "m", "test", "cdks111",
                "mk123", "gggg101", "gnappattac111");

        Connection conn = db.getConnection();
        db.clearTables();
        pDao = new PersonsDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void addPersonPass() throws DataAccessException {
        pDao.addPerson(testPerson);

        Persons comparePerson = pDao.findPerson(testPerson.getPersonID());

        assertNotNull(comparePerson);
        assertEquals(testPerson,comparePerson);
    }

    @Test
    public void addPersonFail() throws DataAccessException {
        pDao.addPerson(testPerson);

        assertThrows(DataAccessException.class, ()-> pDao.addPerson(testPerson));
    }

    @Test
    public void findPersonPass() throws DataAccessException {
        Persons foundPerson = null;

        //Check find with null person object
        assertNotEquals(foundPerson, testPerson);

        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        //Check find when it finds a person that does not match test case
        foundPerson = pDao.findPerson("gnappattack");
        assertNotEquals(foundPerson,testPerson);

        //Check find if it finds person and matches test case.
        foundPerson = pDao.findPerson(testPerson.getPersonID());
        assertEquals(foundPerson, testPerson);
    }

    @Test
    public void findPersonFail() throws DataAccessException, SQLException {
        //Add original Person to Database, make a good and a bad find attempt with the real and a fake personID
        pDao.addPerson(testPerson);
        Persons foundPerson = pDao.findPerson(testPerson.getPersonID());
        Persons notFoundPerson = pDao.findPerson("gnappattack");

        //Make sure the good find matches the original person, and that the bad search does not.
        assertEquals(testPerson, foundPerson);
        assertNotEquals(testPerson, notFoundPerson);

        //Force a throw by attempting a find on an non-existent database
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        PersonsDAO badDao = new PersonsDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findPerson(testPerson.getPersonID()));
    }

    @Test
    public void findPersonByUserPass() throws DataAccessException {
        List<Persons> foundPerson = null;

        //Check find with null person object
        assertNotEquals(foundPerson, testPerson);

        //Add 4 people
        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        //Try a search with username belonging to two people, check if list size is 2
        foundPerson = pDao.findPersonByUser("gnappattack");
        assertEquals(foundPerson.size(), 2);

    }

    @Test
    public void findPersonByUserFails() throws DataAccessException, SQLException {
        List<Persons> foundPerson = new ArrayList<>();

        //Add original Person to Database, make a good and a bad find attempt with the real and a fake personID
        pDao.addPerson(testPerson);

        //Try a search with a non-existent username, check if list size is 0
        foundPerson = pDao.findPersonByUser("notarealname");
        assertEquals(foundPerson.size(), 0);

        //Force a throw by attempting a find on an non-existent database
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        PersonsDAO badDao = new PersonsDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findPersonByUser(testPerson.getPersonID()));
    }



    @Test
    public void clearPersonsTablePass() throws DataAccessException {
        //Add one person, check size, run clear, check if size is 0
        pDao.addPerson(testPerson);
        assertEquals(1, pDao.getTableCount());

        pDao.clearPersonsTable();
        assertEquals(0,pDao.getTableCount());

        //Add 4 people, check size, then clear and check if size is now 0 again.
        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        assertEquals(4, pDao.getTableCount());
        pDao.clearPersonsTable();
        assertEquals(0, pDao.getTableCount());
    }

    @Test
    public void removePersonPass() throws DataAccessException {
        //Add one person, check if size is 1, remove, then check if size if 0
        pDao.addPerson(testPerson);

        assertEquals(1, pDao.getTableCount());
        pDao.removePerson(testPerson.getPersonID());
        assertEquals(0, pDao.getTableCount());

        //Add 4 Persons with similar personIDs.
        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        //Attempt to remove one of the similar Persons, check if size is 3. Check if search for removed person fails.
        pDao.removePerson(firstSimilarPerson.getPersonID());

        assertEquals(3, pDao.getTableCount());
        assertNotEquals(firstSimilarPerson,pDao.findPerson(firstSimilarPerson.getPersonID()));
    }

    @Test
    public void removePersonFail() throws DataAccessException, SQLException {
        //Add original person, attempt remove with an incorrect personID
        pDao.addPerson(testPerson);
        pDao.removePerson(firstSimilarPerson.getPersonID());

        //Test function when remove is run on user that does not exist
        assertEquals(1, pDao.getTableCount());

        //Test find function into bad database to check catch SQLException and throwing DataAccessException
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        PersonsDAO badDao = new PersonsDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findPerson(testPerson.getPersonID()));
    }

    @Test
    public void clearUserPersonsTablePass() throws DataAccessException {
        //Add 4 people, check size, then clear for username gnappattack
        //Check if size is 2 (two of the similar people share the given username)
        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        assertEquals(4, pDao.getTableCount());
        pDao.clearUserPersonsTable(testPerson.getAssociateUsername());
        assertEquals(2, pDao.getTableCount());
    }

    @Test
    public void getTableCount() throws DataAccessException {
        //Add one person, check if size is 1, remove, then check if size if 0
        pDao.addPerson(testPerson);

        assertEquals(1, pDao.getTableCount());
        pDao.removePerson(testPerson.getPersonID());
        assertEquals(0, pDao.getTableCount());

        //Add 4 Persons
        pDao.addPerson(testPerson);
        pDao.addPerson(firstSimilarPerson);
        pDao.addPerson(secondSimilarPerson);
        pDao.addPerson(lastSimilarPerson);

        //Check if size is 4
        assertEquals(4, pDao.getTableCount());
    }

    @Test
    public void getTableCountFail() throws DataAccessException, SQLException {
        //Force a throw by attempting to getsize of an non-existent database
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        PersonsDAO badDao = new PersonsDAO(badConnection);
        assertThrows(DataAccessException.class, badDao::getTableCount);
    }
}