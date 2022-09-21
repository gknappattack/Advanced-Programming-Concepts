package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Events;

public class EventDAO {

    /**
     * A Connection object that establishes connection to the Events SQL Database for data access operations.
     */
    private Connection databaseConnection;

    /**
     * Constructor for the EventDAO class. Sets databaseConnection to make connection with Events SQL database.
     *
     * @param databaseConnection The access information for the Events SQL database.
     */
    public EventDAO(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * The addEvent method takes an Event object as a parameter and adds it into the
     * connected SQL Database with SQL queries.
     *
     * @param event The Event object to be added to the database.
     */
    public void addEvent(Events event) throws DataAccessException {
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, event.getEventID());
            stmt.setString(2,event.getAssociatedUsername());
            stmt.setString(3,event.getPersonID());
            stmt.setFloat(4,event.getLatitude());
            stmt.setFloat(5,event.getLongitude());
            stmt.setString(6,event.getCountry());
            stmt.setString(7,event.getCity());
            stmt.setString(8,event.getEventType());
            stmt.setInt(9,event.getYear());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(("Error encountered while adding to the database"));
        }

    }

    /**
     * The removeEvent method takes an Event object as a parameter and locates its in the database and removes the
     * associated fields from the database.
     *
     * @param eventID The EventID of the Event to be removed from the database.
     */
    public void removeEvent(String eventID) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE EventID = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, eventID);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when removing from database");
        }
    }

    /**
     * The findEvent method takes a String eventID as parameter and searches the connected database for an Event object
     * that matches the eventID.
     *
     * @param eventID The eventID String to be searched for in the database.
     *
     * @return Event object from the database that matches the eventID taken as a parameter.
     */
    public Events findEvent(String eventID) throws DataAccessException {
        Events eventFound;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1,eventID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                eventFound = new Events(rs.getString("EventType"), rs.getString("PersonID"), rs.getString("City"),
                        rs.getString("Country"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getInt("Year"), rs.getString("EventID"), rs.getString("AssociatedUserName"));
                return eventFound;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while finding event");
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
     * The method findEventByUser accesses and returns a list of Events items that contain the username of the requesting
     * user in the associatedUsername field.
     *
     * @param associatedUsername The username to base the SQL WHERE statement on.
     * @return A List of Events items that all match the criteria of having the same AssociatedUserName that is passed in
     * as a parameter
     * @throws DataAccessException When there is an issue in connecting to the SQL Database
     */
    public List<Events> findEventByUser(String associatedUsername) throws DataAccessException {
        List<Events> eventsFound = new ArrayList<Events>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE AssociatedUserName = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1,associatedUsername);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String eventID = rs.getString("EventID");
                String username = rs.getString("AssociatedUserName");
                String personID = rs.getString("PersonID");
                Float latitude = rs.getFloat("Latitude");
                Float longitude = rs.getFloat("Longitude");
                String country = rs.getString("Country");
                String city = rs.getString("City");
                String eventType = rs.getString("EventType");
                int year = rs.getInt("Year");

                Events foundEvent = new Events(eventType,personID,city,country,latitude,longitude,
                        year,eventID,username);

                eventsFound.add(foundEvent);
            }
            return eventsFound;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while finding event");
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
    }

    /**
     * clearUsersTable method uses a SQL update to clear all records from the Events table
     *
     * @throws DataAccessException Throws DataAccessException when connection to database fails.
     */
    public void clearEventsTable() throws DataAccessException {
        String sql = "DELETE FROM Events";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing Events Table");
        }
    }

    /**
     * clearUserEventsTable clears only records from the Events table in the SQL Database that have the same
     * AssociatedUsername as the requesting User. Used as a part of the FillService class in the /Fill web API.
     *
     * @param username The username of the for whom all associated Events will be deleted for.
     * @throws DataAccessException Exception Thrown when there is an error in connecting to the database.
     */
    public void clearUserEventsTable(String username) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE AssociatedUserName = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing Events Table");
        }
    }

    /**
     * getTableCount method uses a SQL Query to get the row count for the Events table.
     *
     * @return An integer variable containing the current row count for the table
     * @throws DataAccessException Throws an exception when connection to the database fails.
     */
    public int getTableCount() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM Events";
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
            throw new DataAccessException("Error occurred while counting records in Events table");
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
