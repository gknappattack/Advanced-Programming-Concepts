package dao;

import model.Persons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonsDAO {
    /**
     * A Connection object that establishes connection to the Persons SQL Database for data access operations.
     */
    private Connection databaseConnection;

    /**
     * Constructor for the PersonsDAO class. Sets databaseConnection to make connection with Persons SQL database.
     *
     * @param databaseConnection The access information for the Persons SQL database.
     */
    public PersonsDAO(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * The addPerson method takes a Person object as a parameter and adds it into the
     * connected SQL Database with SQL queries.
     *
     * @param person The Person object to be added to the database.
     */
    public void addPerson(Persons person) throws DataAccessException {
        String sql = "INSERT INTO Persons (PersonID, AssociatedUserName, FirstName, LastName, " +
                "Gender, FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1,person.getPersonID());
            stmt.setString(2, person.getAssociateUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4,person.getLastName());
            stmt.setString(5,person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7,person.getMotherID());
            stmt.setString(8,person.getSpouseID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered while adding to the database");
        }

    }

    /**
     * The removePerson method takes a personID String object as a parameter and locates its in the database and removes the
     * associated fields from the database.
     *
     * @param personID The personID of the Person object to be removed from the database.
     */
    public void removePerson(String personID) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE PersonID = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, personID);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when removing from database");
        }

    }

    /**
     * The findPerson method takes a String personID as parameter and searches the connected database for a Person object
     * that matches the personID.
     *
     * @param personID The personID String to be searched for in the database.
     *
     * @return Persons object from the database that matches the personID taken as a parameter.
     */
    public Persons findPerson(String personID) throws DataAccessException {
        Persons personFound;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, personID);

            rs = stmt.executeQuery();

            if (rs.next()) {
                personFound = new Persons(rs.getString("FirstName"),rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"),
                        rs.getString("FatherID"), rs.getString("MotherID"),
                        rs.getString("SpouseID"), rs.getString("AssociatedUserName"));

                return personFound;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when finding person");
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
     * The method findPersonByUser accesses and returns a list of Persons items that contain the username of the requesting
     * user in the associatedUsername field.
     *
     * @param associatedUsername The username to base the SQL WHERE statement on.
     * @return A List of Persons items that all match the criteria of having the same AssociatedUserName that is passed in
     * as a parameter
     * @throws DataAccessException When there is an issue in connecting to the SQL Database
     */
    public List<Persons> findPersonByUser(String associatedUsername) throws DataAccessException {
        List<Persons> personsFound = new ArrayList<Persons>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE AssociatedUserName = ?";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String personID = rs.getString("personID");
                String username = rs.getString("AssociatedUserName");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String gender = rs.getString("gender");
                String fatherID = rs.getString("fatherID");
                String motherID = rs.getString("motherID");
                String spouseID = rs.getString("spouseID");

                Persons personFound = new Persons(firstName, lastName, gender, personID, fatherID, motherID,
                        spouseID, username);

                personsFound.add(personFound);
            }
            return personsFound;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered when finding person");
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
     * clearUsersTable method uses a SQL update to clear all records from the Persons table
     *
     * @throws DataAccessException Throws DataAccessException when connection to database fails.
     */
    public void clearPersonsTable() throws DataAccessException {
        String sql = "DELETE FROM Persons";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing Persons Table");
        }
    }

    /**
     * clearUserPersonsTable clears only records from the Persons table in the SQL Database that have the same
     * AssociatedUsername as the requesting User. Used as a part of the FillService class in the /Fill web API.
     *
     * @param username The username of the for whom all associated Persons will be deleted for.
     * @throws DataAccessException Exception Thrown when there is an error in connecting to the database.
     */
    public void clearUserPersonsTable(String username) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE AssociatedUserName = ?;";

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error occured while clearing Persons Table");
        }
    }

    /**
     * getTableCount method uses a SQL Query to get the row count for the Persons table.
     *
     * @return An integer variable containing the current row count for the table
     * @throws DataAccessException Throws an exception when connection to the database fails.
     */
    public int getTableCount() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM Persons";
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
            throw new DataAccessException("Error occurred while counting records in Persons table");
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
