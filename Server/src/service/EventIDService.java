package service;

import dao.*;
import model.AuthToken;
import model.Events;
import request.EventIDRequest;
import result.EventIDResult;

import java.sql.Connection;

public class EventIDService {
    /**
     * The eventID method processes an EventIDRequest object and searches the databases for an Event
     * object associated with the fields of the EventIDRequest object.
     *
     * @param r The EventIDRequest object that contains the eventID to be searched for.
     *
     * @return Returns an EventIDResult object that contains the matching fields if an event is found, or an error message if it fails.
     */
    public static EventIDResult eventID(EventIDRequest r) throws DataAccessException {
        Database db = new Database();
        try {
            //Set up DAO objects needed for EventIDService
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            EventIDResult rslt;

            //Search database for provided Auth Token value, if not found, return an error message and stop.
            AuthToken providedToken = aDao.findAuthToken(r.getAuthTokenValue());

            if (providedToken == null) {
                rslt = new EventIDResult(false, "Error: Auth Token does not exist in the database");
                db.closeConnection(false);
                return rslt;
            }

            //If Auth Token is valid, search for eventID given by user, if not found, return an error message.
            Events foundEvent = eDao.findEvent(r.getEventID());

            if (foundEvent == null) {
                rslt = new EventIDResult(false, "Error: Event with request ID was not found");
                db.closeConnection(false);
                return rslt;
            }

            //If an event is found, but does not belong to the requesting user, return error message.
            if (!providedToken.getAssociatedUsername().equals(foundEvent.getAssociatedUsername())) {
                rslt = new EventIDResult(false, "Error: Event found does not belong to current user");
                db.closeConnection(false);
                return rslt;

            }

            //Create EventIDResult if Auth Token is valid and event was found, close database and return.
            rslt = new EventIDResult(foundEvent.getEventType(), foundEvent.getPersonID(), foundEvent.getCity(),
                    foundEvent.getCountry(), foundEvent.getLatitude(), foundEvent.getLongitude(), foundEvent.getYear(),
                    foundEvent.getEventID(), foundEvent.getAssociatedUsername(), true);

            db.closeConnection(true);
            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is an issue connecting to the database.
            EventIDResult rslt = new EventIDResult(false, "Error: Could not complete search for event");
            db.closeConnection(false);
            return rslt;
        }
    }
}
