package service;

import dao.DataAccessException;
import model.Events;
import model.Persons;
import model.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoadRequest;
import request.RegisterRequest;
import result.LoadResult;

import static org.junit.jupiter.api.Assertions.*;

class LoadServiceTest {

    private RegisterRequest regRequest;
    private LoadRequest loadRequest;
    private Users mainUser;
    private Users duplicateUser;
    private Persons usersPerson;
    private Persons usersMother;
    private Persons usersFather;
    private Events usersBirth;
    private Events usersMarriage;
    private Users[] usersToLoad;
    private Users[] failUserArray;
    private Persons[] peopleToLoad;
    private Events[] eventsToLoad;

    @BeforeEach
    void setUp() throws DataAccessException {
        ClearService.clear();

        mainUser = new Users("gnapp", "notrealpassword", "fakeemail.gmail.com",
                "Greg", "Knapp", "m", "g_knapp");
        duplicateUser = new Users("gnapp", "notrealpassword", "fakeemail.gmail.com",
                "Greg", "Knapp", "m", "g_knapp");
        usersPerson = new Persons("Greg", "Knapp", "m", "g_knapp", "c_knapp",
                "m_knapp", null, "gnapp");
        usersMother = new Persons("Mary", "Knapp", "f", "m_knapp", null,
                null, "c_knapp", "gnapp");
        usersFather = new Persons("Chris", "Knapp", "m", "c_knapp", null,
                null, "m_knapp", "gnapp");
        usersBirth = new Events("Birth", "g_knapp", "San Diego", "United States",
                10.40f, 20.382f, 1998, "gbirth", "gnapp");
        usersMarriage = new Events("Marriage", "g_knapp", "Provo", "United States",
                 39.30f, 30.28f, 2020, "gmarriage", "gnapp");

        usersToLoad = new Users[1];
        failUserArray = new Users[2];
        peopleToLoad = new Persons[3];
        eventsToLoad = new Events[2];

        usersToLoad[0] = mainUser;
        peopleToLoad[0] = usersPerson;
        peopleToLoad[1] = usersFather;
        peopleToLoad[2] = usersMother;
        eventsToLoad[0] = usersBirth;
        eventsToLoad[1] = usersMarriage;

        failUserArray[0] = mainUser;
        failUserArray[1] = duplicateUser;
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        ClearService.clear();
    }

    @Test
    void loadPass() throws DataAccessException {
        //Make a valid registration to the Server
        loadRequest = new LoadRequest(usersToLoad, peopleToLoad, eventsToLoad);

        LoadResult loadResult = LoadService.load(loadRequest);

        assertTrue(loadResult.isSuccess());
    }

    @Test
    void loadFail() throws DataAccessException {
        loadRequest = new LoadRequest(failUserArray, peopleToLoad,eventsToLoad);

        //Attempt a load with an array that has users with the same username - should return false
        LoadResult loadResult = LoadService.load(loadRequest);

        assertFalse(loadResult.isSuccess());
    }
}