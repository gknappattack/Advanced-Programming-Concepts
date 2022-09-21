package dao;

import java.sql.Connection;
import model.AuthToken;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthTokenDAO {

    /**
     * A Connection object that establishes connection to the AuthToken SQL Database for data access operations.
     */
    private Connection databaseConnection;

    /**
     * Constructor for the AuthTokenDAO class. Sets databaseConnection to make connection with AuthToken SQL database.
     *
     * @param databaseConnection The access information for the AuthToken SQL database.
     */
    public AuthTokenDAO(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * The addAuthToken method takes an AuthToken object as a parameter and adds it into the
     * connected SQL Database with SQL queries.
     *
     * @param authorizationToken The AuthToken object to be added to the database.
     */
    public void addAuthToken(AuthToken authorizationToken) throws DataAccessException {
        String sql = "INSERT INTO AuthorizationToken (TokenValue, AssociatedUser, AccessTime) " +
                "VALUES(?,?,?);";
        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, authorizationToken.getTokenValue());
            stmt.setString(2, authorizationToken.getAssociatedUsername());
            stmt.setTimestamp(3, authorizationToken.getTimeAccessed());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered while adding to the database");
        }
    }

    /**
     * The removeAuthToken method takes an AuthToken object as a parameter and locates its in the database and removes the
     * associated fields from the database.
     *
     * @param tokenValue The value of the AuthToken to be removed from the database.
     */
    public void removeAuthToken(String tokenValue) throws DataAccessException {
        String sql = "DELETE FROM AuthorizationToken WHERE TokenValue = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, tokenValue);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when removing from database");
        }
    }

    /**
     * The findAuthToken method takes a String tokenValue as parameter and searches the connected database for
     * an AuthToken object that matches the tokenValue.
     *
     * @param tokenValue The tokenValue String to be searched for in the database.
     *
     * @return AuthToken object from the database that matches the tokenValue taken as a parameter.
     */
    public AuthToken findAuthToken(String tokenValue) throws DataAccessException {
        AuthToken authorizationToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthorizationToken WHERE tokenValue = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1,tokenValue);
            rs = stmt.executeQuery();

            if (rs.next()) {
                authorizationToken = new AuthToken (rs.getString("TokenValue"),
                        rs.getString("AssociatedUser"), rs.getTimestamp("AccessTime"));
                return authorizationToken;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while finding auth token");
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
     * clearUsersTable method uses a SQL update to clear all records from the AuthorizationToken table
     *
     * @throws DataAccessException Throws DataAccessException when connection to database fails.
     */
    public void clearAuthTokenTable() throws DataAccessException {
        String sql = "DELETE FROM AuthorizationToken";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing AuthorizationToken Table");
        }
    }

    /**
     * getTableCount method uses a SQL Query to get the row count for the AuthorizationToken table.
     *
     * @return An integer variable containing the current row count for the table
     * @throws DataAccessException Throws an exception when connection to the database fails.
     */
    public int getTableCount() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM AuthorizationToken";
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
            throw new DataAccessException("Error occurred while counting records in AuthorizationToken table");
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