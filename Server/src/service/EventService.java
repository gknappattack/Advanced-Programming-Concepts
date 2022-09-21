package service;

import dao.*;
import model.AuthToken;
import model.Events;
import request.EventRequest;
import result.EventResult;

import java.sql.Connection;
import java.util.List;

public class EventService {

    /**
     * The event method takes an EventRequest object and searches for all events connected to the user associated with
     * the EventRequest object's AuthToken that is contained.
     *
     * @param r The EventRequest object containing the AuthToken object that indicates the user to search Events for.
     *
     * @return Return an EventResult object with an Array of Events if successful or an error message if a failure.
     */
    public static EventResult event(EventRequest r) throws DataAccessException {
        Database db = new Database();
        try {
            //Set up DAO objects needed for EventService.
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            EventResult rslt;

            //Search database for provided Auth Token value, if not found, return an error message and stop.
            AuthToken usersToken = aDao.findAuthToken(r.getAuthToken());

            if (usersToken == null) {
                rslt = new EventResult(false, "Error: Invalid AuthToken detected");
                db.closeConnection(false);
                return rslt;
            }

            //If Auth Token is valid, search database for all events associated with the user.
            String searchUsername = usersToken.getAssociatedUsername();

            List<Events> eventsFound = eDao.findEventByUser(searchUsername);

            //Check if the user has any events in the database.
            if (eventsFound.size() > 0) {
                Events[] foundArray = new Events[eventsFound.size()];

                //If events are found, put Event objects into an array, create EventResult and return.
                for (int i = 0; i < eventsFound.size(); i++) {
                    foundArray[i] = eventsFound.get(i);
                }
                rslt = new EventResult(foundArray,true);
            }
            else {
                //If no events were found, return an error message.
                rslt = new EventResult(false, "Error: No events were found with the current user's name");
            }

            db.closeConnection(true);
            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is an issue connecting to the database.
            EventResult rslt = new EventResult(false, "Error: Could not complete search for events");
            db.closeConnection(false);
            return rslt;
        }
    }
}
