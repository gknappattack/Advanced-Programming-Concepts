package service;

import dao.*;
import model.Events;
import model.Persons;
import model.Users;
import request.LoadRequest;
import result.LoadResult;

import java.sql.Connection;

public class LoadService {


    /**
     * The load method processes a LoadRequest, clears the existing values in the databases, and fills them with data
     * from Arrays contained in the LoadRequest object.
     *
     * @param r The LoadRequest object containing a Users, Persons, and Events array to be loaded.
     *
     * @return Returns a LoadResult object with a message and boolean based on the success or failure of the load.
     */
    public static LoadResult load(LoadRequest r) throws DataAccessException {
        Database db = new Database();
        try {
            //Create needed DAO objects for LoadService
            Connection conn = db.openConnection();
            UsersDAO uDao = new UsersDAO(conn);
            PersonsDAO pDao = new PersonsDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            //Clear all tables before loading new values.
            uDao.clearUsersTable();
            pDao.clearPersonsTable();
            eDao.clearEventsTable();
            aDao.clearAuthTokenTable();

            //Extract arrays from LoadRequest
            Users[] usersToAdd = r.getUserArray();
            Persons[] peopleToAdd = r.getPersonArray();
            Events[] eventsToAdd = r.getEventArray();

            //Add Users to database, save number of Users added to include in LoadResult
            for (int i = 0; i < usersToAdd.length; i++) {
                Users currUser = usersToAdd[i];
                uDao.addUser(currUser);
            }
            int userSize = usersToAdd.length;

            //Add Persons to database, save number of Persons added to include in LoadResult
            for (int i = 0; i < peopleToAdd.length; i++) {
                Persons currPerson = peopleToAdd[i];
                pDao.addPerson(currPerson);
            }
            int peopleSize = peopleToAdd.length;

            //Add Events to database, save number of Events added to include in LoadResult
            for (int i = 0; i < eventsToAdd.length; i++) {
                Events currEvent = eventsToAdd[i];
                eDao.addEvent(currEvent);
            }
            int eventSize = eventsToAdd.length;

            //Close database, write LoadResult and return.
            db.closeConnection(true);

            LoadResult rslt = new LoadResult("Successfully added " + userSize + " users, "
                    + peopleSize + " persons, and " + eventSize + " events to the database.", true);

            return rslt;
        }
        catch (DataAccessException e) {
            //Return error message when connection to database or adding of objects fails.
            LoadResult rslt = new LoadResult("Error: when loading to the database", false);
            db.closeConnection(false);
            return rslt;
        }
    }
}
