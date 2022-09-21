package dao;

import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDAO {
    /**
     * A Connection object that establishes connection to the Users SQL Database for data access operations.
     */
    private Connection databaseConnection;

    /**
     * Constructor for the UsersDAO class. Sets databaseConnection to make connection with Users SQL database.
     *
     * @param databaseConnection The access information for the Users SQL database.
     */
    public UsersDAO(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * The addUser method takes a User object as a parameter and adds it into the
     * connected SQL Database with SQL queries.
     *
     * @param user The User object to be added to the database.
     */
    public void addUser(Users user) throws DataAccessException {
        String sql = "INSERT INTO USERS (UserName, Password, Email, FirstName, " +
                "LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5,user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while adding to the database");
        }
    }

    /**
     * The removeUser method takes a User object as a parameter and locates its in the database and removes the
     * associated fields from the database.
     *
     * @param username The username of the User object to be removed from the database.
     */
    public void removeUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE UserName = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when removing from database");
        }

    }

    /**
     * The findUser method takes a String username as parameter and searches the connected database for a User object
     * that matches the username.
     *
     * @param username The username String to be searched for in the database.
     *
     * @return User object from the database that matches the username taken as a parameter.
     */
    public Users findUser(String username) throws DataAccessException {
        Users userFound;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE UserName = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            if (rs.next()) {
                userFound = new Users(rs.getString("UserName"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"));

                return userFound;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while finding user");
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * clearUsersTable method uses a SQL update to clear all records from the Users table
     *
     * @throws DataAccessException Throws DataAccessException when connection to database fails.
     */
    public void clearUsersTable() throws DataAccessException {
        String sql = "DELETE FROM Users";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing User Table");
        }
    }

    /**
     * getTableCount method uses a SQL Query to get the row count for the User table.
     *
     * @return An integer variable containing the current row count for the table
     * @throws DataAccessException Throws an exception when connection to the database fails.
     */
    public int getTableCount() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM Users";
        int recordCount;
        ResultSet rs = null;

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            rs = stmt.executeQuery();

            if (rs.next())  {
                recordCount = rs.getInt(1);
                return recordCount;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occurred while counting records in User table");
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }
}
