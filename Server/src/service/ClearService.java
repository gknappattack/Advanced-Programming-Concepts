package service;

import dao.*;
import result.ClearResult;

import java.sql.Connection;

public class ClearService {
    /**
     * The clear method connects with the DAO classes to clear all data from the databases.
     *
     * @return A ClearResult object reporting the success or failure of the clear method.
     */
    public static ClearResult clear() throws DataAccessException {
        Database db = new Database();
        try {

            //Create DAO objects needed in ClearService
            Connection conn = db.openConnection();
            UsersDAO uDao = new UsersDAO(conn);
            PersonsDAO pDao = new PersonsDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            //Execute Clear for each table
            uDao.clearUsersTable();
            pDao.clearPersonsTable();
            eDao.clearEventsTable();
            aDao.clearAuthTokenTable();

            //Create ClearResult, close connection and return
            ClearResult rslt = new ClearResult("Clear succeeded.", true);

            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is an issue with clearing the databases.
            ClearResult rslt = new ClearResult("Error: When clearing the database", false);
            db.closeConnection(false);
            return rslt;
        }
    }
}
