package service;

import dao.*;
import model.AuthToken;
import model.Persons;
import request.PersonRequest;
import result.PersonResult;

import java.sql.Connection;
import java.util.List;

public class PersonService {

    /**
     * The person method takes an PersonRequest object and searches for all Persons connected to the user associated with
     * the PersonRequest object's AuthToken that is contained.
     *
     * @param r The PersonRequest object containing the AuthToken object that indicates the user to search Persons for.
     *
     * @return Return an PersonResult object with an Array of Persons if successful or an error message if a failure.
     */
    public static PersonResult person(PersonRequest r) throws DataAccessException  {
        Database db = new Database();
        try {
            //Set up DAO objects needed for PersonService.
            Connection conn = db.openConnection();
            PersonsDAO pDao = new PersonsDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            PersonResult rslt;

            //Search database for provided Auth Token value, if not found, return an error message and stop.
            AuthToken usersToken = aDao.findAuthToken(r.getAuthToken());

            if (usersToken == null) {
                rslt = new PersonResult(false, "Error: Invalid AuthToken detected");
                db.closeConnection(false);
                return rslt;
            }

            //If Auth Token is valid, search database for all Persons associated with the user.
            String searchUsername = usersToken.getAssociatedUsername();

            List<Persons> peopleFound = pDao.findPersonByUser(searchUsername);

            //Check if the user has any Persons in the database.
            if (peopleFound.size() > 0) { //Check if user was found
                Persons[] foundArray = new Persons[peopleFound.size()];

                //If Persons are found, put Person objects into an array, create PersonResult and return.
                for (int i = 0; i < peopleFound.size(); i++) {
                    foundArray[i] = peopleFound.get(i);
                }
                rslt = new PersonResult(foundArray,true);
            }
            else {
                //If no Persons were found, return an error message.
                rslt = new PersonResult(false, "Error: No people were found with the current user's name");
            }

            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is an issue connecting to the database.
            PersonResult rslt = new PersonResult(false, "Error: Could not complete search for Persons");
            db.closeConnection(false);
            return rslt;
        }
    }
}
