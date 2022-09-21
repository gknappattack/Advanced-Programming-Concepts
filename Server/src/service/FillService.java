package service;

import dao.*;
import model.Events;
import model.Location;
import model.Persons;
import model.Users;
import request.FillRequest;
import result.FillResult;
import server.JSONHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FillService {

    /**
     * The fill method processes a FillRequest object, and populates the tree of the given user for the number of
     * generations specified by the FillRequest object.
     *
     * @param r The FillRequest object containing the username and the number of generations to populate the tree for.
     *
     * @return Returns a FillResult object with a message and boolean depending on the success or failure of the fill.
     */
    public static FillResult fill(FillRequest r) throws DataAccessException {
        Database db = new Database();
        try {

            //Create DAO objects needed for FillService
            Connection conn = db.openConnection();
            UsersDAO uDao = new UsersDAO(conn);
            PersonsDAO pDao = new PersonsDAO(conn);
            EventDAO eDao = new EventDAO(conn);

            FillResult rslt;

            String currentPersonID = null;
            String currentEventID = null;

            List<Events> eventsForCurrentPerson;
            List<Persons> personsToAdd = new ArrayList<Persons>();
            int peopleAddedTotal = 0;
            int eventsAddedTotal = 0;

            //Search for provided user in database, if not found return an error message.
            Users userToFill = uDao.findUser(r.getUsername());

            if (userToFill == null) {
                rslt = new FillResult("Error: User does not exist in the database.", false);
                db.closeConnection(false);
                return rslt;
            }

            //Clear all data associated with user from database before filling
            uDao.removeUser(r.getUsername());
            pDao.clearUserPersonsTable(r.getUsername());
            eDao.clearUserEventsTable(r.getUsername());

            //Get data from JSON files to create location and name data
            String maleNameFile = new String(Files.readAllBytes((Paths.get("json/mnames.json"))));
            String femaleNameFile = new String(Files.readAllBytes((Paths.get("json/fnames.json"))));
            String surnameFile = new String(Files.readAllBytes((Paths.get("json/snames.json"))));
            String locationDataFile = new String(Files.readAllBytes((Paths.get("json/locations.json"))));

            String[] possibleMaleNames = JSONHandler.deserializeNames(maleNameFile).getData();
            String[] possibleFemaleNames = JSONHandler.deserializeNames(femaleNameFile).getData();
            String[] possibleSurnames = JSONHandler.deserializeNames(surnameFile).getData();
            Location[] possibleLocations = JSONHandler.deserializeLocationData(locationDataFile).getData();

            //Re-add User to Database, create Person and birth Event for user
            List<Persons> currentGeneration = new ArrayList<Persons>();
            List<Persons> previousGeneration = new ArrayList<Persons>();

            uDao.addUser(userToFill);

            currentPersonID = UUID.randomUUID().toString().substring(0, 8);
            Persons usersPerson = new Persons(userToFill.getFirstName(), userToFill.getLastName(), userToFill.getGender(),
                    currentPersonID, null, null, null, userToFill.getUsername());

            previousGeneration.add(usersPerson);

            Random birthYearGenerator = new Random();
            int birthYear = birthYearGenerator.nextInt(2010 - 1980) + 1980;

            eventsForCurrentPerson = generateEvents(possibleLocations, birthYear, currentPersonID,
                    userToFill.getUsername(), true, birthYear, eDao);

            for (int i = 0; i < eventsForCurrentPerson.size(); i++) {
                eDao.addEvent(eventsForCurrentPerson.get(i));
            }

            eventsAddedTotal += eventsForCurrentPerson.size();
            eventsForCurrentPerson.clear();


            //Create people based on number of generations requested by user
            for (int i = 1; i <= r.getGenerations(); i++) {

                Persons personToUpdate = null;

                int peopleToAddThisGeneration = (int) Math.pow(2, i);

                //Create current generation of people with createGenerationOfPeople function
                currentGeneration.clear();
                currentGeneration = createGenerationOfPeople(possibleMaleNames, possibleFemaleNames, possibleSurnames,
                        peopleToAddThisGeneration, userToFill.getUsername(), pDao);

                //Update fatherID, motherID, and spouseID, values for current generation
                for (int j = 0; j < currentGeneration.size(); j++) {
                    Persons currentDescendant = currentGeneration.get(j);

                    if (j % 2 == 0) { //Update currentDescendant's fatherID value
                        personToUpdate = previousGeneration.get(j / 2);
                        personToUpdate.setFatherID(currentDescendant.getPersonID());

                    }
                    else { //Update currentDescendant's motherID value
                        personToUpdate.setMotherID(currentDescendant.getPersonID());
                        previousGeneration.set(j / 2, personToUpdate);
                    }
                }

                //Add currentGeneration members to previousGeneration array, clear, prepare for next iteration.
                personsToAdd.addAll(previousGeneration);
                previousGeneration.clear();
                previousGeneration.addAll(currentGeneration);
            }

            //Add final generation to array of People to add to database
            personsToAdd.addAll(currentGeneration);

            //Add all people created in loop above to People table in the database, save size to write in Result Body
            for (int i = 0; i < personsToAdd.size(); i++) {
                pDao.addPerson(personsToAdd.get(i));
            }

            peopleAddedTotal = personsToAdd.size();

            //Run loop to create birth, marriage, and death Events for all people created by FillService
            // excluding the user.
            Events marriageEvent = null;
            int generationCounter = 1;
            int peopleWithEventsCreated = 0;

            for (int i = 1; i < personsToAdd.size(); i++) {
                Persons currentPerson = personsToAdd.get(i);

                int differenceInBirthYear = 30;

                //Create 3 events for the currentPerson by calling generateEvents
                //Save them to List eventsForCurrentPerson
                eventsForCurrentPerson = generateEvents(possibleLocations, birthYear - differenceInBirthYear,
                        currentPerson.getPersonID(), userToFill.getUsername(), false, birthYear, eDao);

                //Update marriage events of female Persons to match their husband's as a default
                if (i % 2 == 0) {
                    //Update wife's marriage event
                    String wifeMarriageEventID;
                    wifeMarriageEventID = eventsForCurrentPerson.get(1).getEventID();
                    marriageEvent.setPersonID(currentPerson.getPersonID());
                    marriageEvent.setEventID(wifeMarriageEventID);

                    eventsForCurrentPerson.set(1, marriageEvent);

                }
                else {
                    //Get data of marriage event to update wife's marriage event
                    marriageEvent = eventsForCurrentPerson.get(1);
                }

                //Add events for currentPerson to Events table in database.
                for (int j = 0; j < eventsForCurrentPerson.size(); j++) {
                    eDao.addEvent(eventsForCurrentPerson.get(j));
                }

                //Update size to add to FillResult, clear events List
                eventsAddedTotal += eventsForCurrentPerson.size();
                eventsForCurrentPerson.clear();

                peopleWithEventsCreated++;

                //Check if events for a generation of people have been created by counting powers of 2
                //If a generation is complete update birthYear variable for the next generation.
                if (peopleWithEventsCreated == (int) Math.pow(2, generationCounter)){
                    //Update generation counter and birth year
                    generationCounter++;
                    birthYear = birthYear - differenceInBirthYear;
                }
            }

            //Write results of Fill to FillResult, close database and return.
            rslt = new FillResult("Successfully added " + peopleAddedTotal + " persons and " +
                    eventsAddedTotal + " Events to the database", true);
            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException | IOException e) {
            //Return an error message if connection to the database fails.
            FillResult rslt = new FillResult("Error: Could not complete fill", false);

            db.closeConnection(false);
            return rslt;
        }
    }

    /**
     * generateEvent creates a list of 3 events, birth, marriage, and death for the current Person object
     * that is being processed. The birth year to base the marriage and death events is passed from fill().
     * Marriage and death years are created using random number generation. Location data is taken from a JSON
     * file with possible locations for events.
     *
     * @param locationData An Array containing Location objects with data to fill country, city, latitude and longitude
     *                     fields of the current Event.
     * @param eventYear The year the birth, marriage or death takes place.
     * @param personID The personID of the person who the generated Events belong to.
     * @param associatedUsername The username of the user who requested the fill API.
     * @param birthEventOnly A boolean indicating that only a birth event be generated. Only used when the Person object
     *                       for the requesting user is being processed.
     * @param comparisonBirthYear The birth year of the current Person's child, used to check for invalid or unrealistic
     *                            birth, marriage, or death years.
     * @param eDao The EventDAO object used to check for duplicate eventID strings before adding to the database.
     *
     * @return A List containing the 3 events created for the currentPerson
     * @throws DataAccessException Thrown when searching for a duplicate eventID fails.
     */
    private static List<Events> generateEvents(Location[] locationData, int eventYear, String personID,
                                               String associatedUsername, boolean birthEventOnly,
                                               int comparisonBirthYear,
                                               EventDAO eDao)  throws DataAccessException {

        //Create List to add events to and return.
        String eventType = null;
        List<Events> currentPersonsEvents = new ArrayList<Events>();
        int birthYear = eventYear;

        //Run loop to create 3 events for currentPerson and add to List.
        for (int i = 0; i < 3; i++) {

            //Use random number generator to select a location for the event.
            Random rand = new Random();
            int locationArrayAccess = rand.nextInt(locationData.length);
            Location eventLocation = locationData[locationArrayAccess];

            //Randomly generate eventID, check in database if it already exists. If so, regenerate ID.
            String eventID = UUID.randomUUID().toString().substring(0, 8);

            Events foundEvent = eDao.findEvent(eventID);

            while (foundEvent != null) { //This eventID is in use, generate a new one until its unique
                eventID = UUID.randomUUID().toString().substring(0, 8);
                foundEvent = eDao.findEvent(eventID);
            }


            if (i == 0) {
                //Add birth event on first loop
                eventType = "Birth";
            }
            if (i == 1) {
                //Add marriage event on second loop,
                eventType = "Marriage";
                eventYear += rand.nextInt(30 - 20) + 20;
            }
            if (i == 2) {
                //Add death on final loop. Check for deaths that are past 2020 or occur before the child's birth year.
                eventType = "Death";
                int previousEventYear = eventYear;
                do {
                    eventYear = previousEventYear + rand.nextInt(70 - 1) + 10;

                    if (eventYear > 2020) {
                        eventYear = 2019;
                    }
                } while(eventYear - comparisonBirthYear <= 0 || eventYear - birthYear > 120);
            }

            //Create Event objects for current loop and add to List
            Events eventToAdd = new Events(eventType, personID, eventLocation.getCity(), eventLocation.getCountry(),
                    eventLocation.getLatitude(), eventLocation.getLongitude(), eventYear, eventID,associatedUsername);

            currentPersonsEvents.add(eventToAdd);

            if (birthEventOnly) { //Break when creating birth event only for user's Person
                break;
            }
        }
        return currentPersonsEvents;
    }

    /**
     * createGenerationOfPeople method creates the required number of people based on a generation parameter passed in
     * from fill(). Names for men and women as well as last names are generated using sample JSON files.
     *
     * @param maleNames An Array containing possible male names parsed from a JSON file
     * @param femaleNames An Array containing possible female names parsed from a JSON file.
     * @param surnames An Array containing possible last names parsed from a JSON file.
     * @param peopleToCreate The number of people to be created by this function and stored in the list to be returned.
     * @param associatedUsername The username of the requesting user.
     * @param pDao The PersonDAO object used to check for duplicate personIDs before adding.
     *
     * @return A List of Persons objects to be added to the database.
     * @throws DataAccessException Thrown when searching for duplicate personID's fails.
     */
    private static List<Persons> createGenerationOfPeople(String[] maleNames, String[] femaleNames, String[] surnames,
                                               int peopleToCreate, String associatedUsername,
                                                         PersonsDAO pDao) throws DataAccessException {

        //Create List and needed variables for creating generation of Persons.
        Persons currentHusband = null;
        Persons currentWife = null;

        List<Persons> peopleCreatedThisGeneration = new ArrayList<Persons>();

        //Run loop to create number of people dictated by parameter
        for (int i = 0; i < peopleToCreate; i++) {

            //Use random number to select first and last name of current Person
            Random rand = new Random();
            int nameArrayAccess = rand.nextInt(maleNames.length);

            String firstName;
            String lastName;

            //Generate personID randomly. Check if already exists in database. If so, re-generate ID.
            String personID = UUID.randomUUID().toString().substring(0, 8);

            Persons foundPerson = pDao.findPerson(personID);

            while (foundPerson != null) { //This personID is in use, generate a new one until its unique
                personID = UUID.randomUUID().toString().substring(0, 8);
                foundPerson = pDao.findPerson(personID);
            }

            //Create male Persons on even number iterations of loop
            if (i % 2 == 0) {
                firstName = maleNames[nameArrayAccess];
                lastName = surnames[nameArrayAccess];
                currentHusband = new Persons(firstName, lastName,"m", personID, null, null,
                        null, associatedUsername);
            }
            //Create female Persons on odd number iterations of loop.
            else {
                firstName = femaleNames[nameArrayAccess];
                lastName = surnames[nameArrayAccess];
                currentWife = new Persons(firstName, lastName,"f", personID, null, null,
                        null, associatedUsername);

                //Update spouseID's for both previous male and current female Persons to match each other.
                String husbandPersonID = currentHusband.getPersonID();
                String wifePersonID = currentWife.getPersonID();

                currentHusband.setSpouseID(wifePersonID);
                currentWife.setSpouseID(husbandPersonID);

                peopleCreatedThisGeneration.add(currentHusband);
                peopleCreatedThisGeneration.add(currentWife);
            }

        }
        return peopleCreatedThisGeneration;
    }
}
