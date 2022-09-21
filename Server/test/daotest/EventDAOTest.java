package daotest;

import dao.*;
import model.Events;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class EventDAOTest {
    private Database db;
    private Events bestEvent;
    private Events similarEvent;
    private EventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        bestEvent = new Events("Biking_Around", "Gale123A", "Ushiku","Japan",
                35.9f,140.1f,2016, "Biking_123A", "Gale");
        similarEvent = new Events("Biking_Around", "Gale123A", "Ushiku","Japan",
                35.9f,140.1f,2016, "Biking123A", "Gale");

        Connection conn = db.getConnection();
        db.clearTables();
        eDao = new EventDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        //Insert Event into database, then try to find it
        eDao.addEvent(bestEvent);
        Events compareTest = eDao.findEvent(bestEvent.getEventID());

        //Make sure findEvent returns something
        assertNotNull(compareTest);
        //Check if it is the same as the original event
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        eDao.addEvent(bestEvent);

        //Force a throw when trying to add event twice
        assertThrows(DataAccessException.class, ()-> eDao.addEvent(bestEvent));
    }

    @Test
    public void findEventPass() throws DataAccessException {
        //Add two similar events with differnet IDs
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        //Search for original
        Events foundEvent = eDao.findEvent(bestEvent.getEventID());

        //Check if findEvent returned something
        assertNotNull(foundEvent);
        //Check if foundEvent is the original event, not the similar one
        assertEquals(bestEvent, foundEvent);
    }

    @Test
    public void findEventFail() throws DataAccessException, SQLException {
        //Add original event
        eDao.addEvent(bestEvent);

        //Search for original event, and make a bad search with a non-existent EventID
        Events foundEvent = eDao.findEvent(bestEvent.getEventID());
        Events notFoundEvent = eDao.findEvent(similarEvent.getEventID());

        //Check for a valid find, followed by a null result
        assertEquals(bestEvent, foundEvent);
        assertNotEquals(bestEvent, notFoundEvent);

        //Make a database call on an non-existent database to force a throw.
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        EventDAO badDao = new EventDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findEvent(bestEvent.getEventID()));
    }

    @Test
    public void findEventByUserPass() throws DataAccessException {
        List<Events> foundEvents = new ArrayList<>();

        //Add three similar events, two associated with the same user
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        Events differentUsernameEvent = new Events("Biking_Around", "Gale123A", "Ushiku","Japan",
                35.9f,140.1f,2016, "Biking_123B", "NotGale");

        eDao.addEvent(differentUsernameEvent);

        //Search for original
        foundEvents = eDao.findEventByUser(bestEvent.getAssociatedUsername());

        //Check if findEvent returned something
        assertNotNull(foundEvents);
        //Check if foundEvent contains 2 results
        assertEquals(2, foundEvents.size());
    }

    @Test
    public void findEventsByUserFail() throws DataAccessException, SQLException {
        List<Events> foundEvents = new ArrayList<>();

        //Add three similar events, two associated with the same user
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        //Try searching for non-existent username, check if list size = 0
        foundEvents = eDao.findEventByUser("notrealusername");
        assertEquals(0, foundEvents.size());

        //Make a database call on an non-existent database to force a throw.
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        EventDAO badDao = new EventDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findEventByUser(bestEvent.getAssociatedUsername()));
    }

    @Test
    public void clearTablesPass() throws DataAccessException {
        //Add 1 event, clear, check if size is 0
        eDao.addEvent(bestEvent);
        assertEquals(1, eDao.getTableCount());

        eDao.clearEventsTable();
        assertEquals(0, eDao.getTableCount());

        //Add 2 events, clear, then check if size is again 0
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        assertEquals(2, eDao.getTableCount());
        eDao.clearEventsTable();
        assertEquals(0, eDao.getTableCount());
    }

    @Test
    public void clearUserEventsTablePass() throws DataAccessException {
        //Add 3 Events, check size, then clear for username gnappattack
        //Check if size is 2 (two of the similar people share the given username)
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        Events differentUsernameEvent = new Events("Biking_Around", "Gale123A", "Ushiku","Japan",
                35.9f,140.1f,2016, "Biking_123B", "NotGale");

        eDao.addEvent(differentUsernameEvent);

        //Check if size is 3, run clear. 2 Events should be removed, so check if size is 1
        assertEquals(3, eDao.getTableCount());
        eDao.clearUserEventsTable(bestEvent.getAssociatedUsername());
        assertEquals(1, eDao.getTableCount());
    }

    @Test
    public void removeEventPass() throws DataAccessException {
        //Add original event, check if add worked, then remove by ID and check if size is 0
        eDao.addEvent(bestEvent);

        assertEquals(1, eDao.getTableCount());
        eDao.removeEvent(bestEvent.getEventID());
        assertEquals(0, eDao.getTableCount());

        //Add two events then remove one. Check if size is 1.
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        eDao.removeEvent(similarEvent.getEventID());

        assertEquals(1, eDao.getTableCount());

        //Check that removed event cannot be found again in the database and was truly removed.
        assertNotEquals(similarEvent, eDao.findEvent(similarEvent.getEventID()));
    }

    @Test
    public void removeEventFail() throws DataAccessException, SQLException {
        //Run valid remove to make sure it works.
        eDao.addEvent(bestEvent);
        eDao.removeEvent(similarEvent.getEventID());

        assertEquals(1, eDao.getTableCount());

        //Then run remove on a non-existent database to force a throw.
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        EventDAO badDao = new EventDAO(badConnection);
        assertThrows(DataAccessException.class, ()-> badDao.findEvent(bestEvent.getEventID()));
    }

    @Test
    public void getTableCount() throws DataAccessException {
        //Add original event, check if add worked, then remove by ID and check if size is 0
        eDao.addEvent(bestEvent);

        assertEquals(1, eDao.getTableCount());
        eDao.removeEvent(bestEvent.getEventID());
        assertEquals(0, eDao.getTableCount());

        //Add two events then remove one. Check if size is 2.
        eDao.addEvent(bestEvent);
        eDao.addEvent(similarEvent);

        assertEquals(2, eDao.getTableCount());
    }

    @Test
    public void getTableCountFail() throws DataAccessException, SQLException {
        //Force a throw by attempting to getsize of an non-existent database
        Connection badConnection = DriverManager.getConnection("jdbc:sqlite:fakedata.sqlite");
        EventDAO badDao = new EventDAO(badConnection);
        assertThrows(DataAccessException.class, badDao::getTableCount);
    }
}
