package service;

import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import dao.UsersDAO;
import model.AuthToken;
import model.Users;
import request.LoginRequest;
import result.LoginResult;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.UUID;

public class LoginService {

    /**
     * The login method takes a LoginRequest object and checks if the fields exist in the database
     * to confirm a valid login.
     *
     * @param r The LoginRequest object that contains the username and password Strings to search for in the database.
     *
     * @return Returns a LoginResult object containing an AuthToken if successful, and an error message if a failure.
     */
    public static LoginResult login(LoginRequest r) throws DataAccessException {
        Database db = new Database();
        try {
            //Create DAO objects needed for LoginService
            Connection conn = db.openConnection();
            UsersDAO uDao = new UsersDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            //Check if User is found in database already.
            Users foundUser = uDao.findUser(r.getUsername());
            LoginResult rslt = null;

            if (foundUser != null) { //User was found in database.
                if (foundUser.getUsername().equals(r.getUsername()) && foundUser.getPassword().equals(r.getPassword())) {
                    //Username and password both match records, create successful LoginResult and create Auth Token.
                    String tokenValue = UUID.randomUUID().toString().substring(0,8);
                    rslt = new LoginResult(tokenValue, foundUser.getUsername(), foundUser.getPersonID(), true);

                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    AuthToken userAuthToken = new AuthToken(tokenValue,r.getUsername(),currentTime);
                    aDao.addAuthToken(userAuthToken);

                } else { //Username was found in database, but password did not match the user. Return error message.
                    rslt = new LoginResult("Error: Password does not match our records", false);
                }
            }
            else { //Username was not found in the database, return error message.
                rslt = new LoginResult("Error: User does not exist", false);
            }

            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException e) {
            //Return an error message if there is a problem accessing the database to search for the user.
            LoginResult rslt = new LoginResult("Error: Issue when logging in the user.", false);
            db.closeConnection(false);
            return rslt;
        }
    }
}
