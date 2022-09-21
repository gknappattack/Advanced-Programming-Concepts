package service;

import dao.*;
import model.AuthToken;
import model.Persons;
import request.PersonIDRequest;
import result.PersonIDResult;

import java.sql.Connection;

public class PersonIDService {

    /**
     * The personID method processes an PersonIDRequest object and searches the databases for a Person
     * object associated with the fields of the PersonIDRequest object.
     *
     * @param r The PersonIDRequest object that contains the personID to be searched for.
     *
     * @return Returns an PersonIDResult object that contains the matching fields if an event is found, or an error message if it fails.
     */
    public static PersonIDResult personID(PersonIDRequest r) throws DataAccessException {
        Database db = new Database();
        try {
            //Create DAO objects needed for PersonIDService.
            Connection conn = db.openConnection();
            PersonsDAO pDao = new PersonsDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            PersonIDResult rslt;

            //Search database for provided Auth Token value, if not found, return an error message and stop.
            AuthToken providedToken = aDao.findAuthToken(r.getAuthTokenValue());

            if (providedToken == null) {
                System.out.println("Given Auth Token does not exist in the database");
                rslt = new PersonIDResult(false, "Error: Auth Token does not exist in the database");
                db.closeConnection(false);
                return rslt;
            }

            //If Auth Token is valid, search for personID given by user, if not found, return an error message.
            Persons foundPerson = pDao.findPerson(r.getPersonID());

            if (foundPerson == null) {
                rslt = new PersonIDResult(false, "Error: Person with requested ID was not found");
                db.closeConnection(false);
                return rslt;
            }

            //If an person is found, but does not belong to the requesting user, return error message.
            if (!providedToken.getAssociatedUsername().equals(foundPerson.getAssociateUsername())) {
                System.out.println("Error: Person found does not belong to current user");
                rslt = new PersonIDResult(false, "Error: Person found does not belong to current user");
                db.closeConnection(false);
                return rslt;

            }

            //Create PersonIDResult if Auth Token is valid and Person was found, close database and return.
            rslt = new PersonIDResult(foundPerson.getAssociateUsername(),foundPerson.getPersonID(),
                    foundPerson.getFirstName(), foundPerson.getLastName(), foundPerson.getGender(),
                    foundPerson.getFatherID(), foundPerson.getMotherID(), foundPerson.getSpouseID(), true);


            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is an issue connecting to the database.
            PersonIDResult rslt = new PersonIDResult(false, "Error: Could not complete search for person");
            db.closeConnection(false);
            return rslt;
        }
    }
}
