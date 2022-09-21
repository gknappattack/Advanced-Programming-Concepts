package service;

import dao.*;
import model.*;
import request.RegisterRequest;
import result.RegisterResult;
import server.JSONHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;


public class RegisterService {

    /**
     * The register method takes fields from a RegisterRequest object, creates a new User, and adds the User
     * to the Users database if the username field is does not already exist in the database.
     *
     * @param r The RegisterRequest object containing all the fields needed to create a new User object.
     *
     * @return Returns a RegisterResult object containing an AuthToken and other information if a valid registration, or an error message if a failure.
     */
    public static RegisterResult register(RegisterRequest r) throws DataAccessException {
        Database db = new Database();

        try {
            //Create DAO objects for RegisterService
            Connection conn = db.openConnection();
            UsersDAO uDao = new UsersDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            PersonsDAO pDao = new PersonsDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            RegisterResult rslt;
            List<Events> eventsForCurrentPerson;
            List<Persons> personsToAdd = new ArrayList<Persons>();

            //Generate personID for new user's Person, check for duplicates in the database.
            String username = r.getUserName();
            String personID = UUID.randomUUID().toString().substring(0, 8);

            Persons foundPerson = pDao.findPerson(personID);

            while (foundPerson != null) { //This personID is in use, generate a new one until its unique
                personID = UUID.randomUUID().toString().substring(0, 8);
                foundPerson = pDao.findPerson(personID);
            }

            String tokenValue = UUID.randomUUID().toString().substring(0,8);
            String eventID = UUID.randomUUID().toString().substring(0, 8);


            //Check if user already exists in database. If found, return an error message.
            Users foundUser = uDao.findUser(username);

            if (foundUser != null) {
                rslt = new RegisterResult("Error: Username is already in use", false);
                db.closeConnection(false);
                return rslt;
            }

            //Add new User to database
            Users newUser = new Users(username,r.getPassword(),r.getEmail(),
                    r.getFirstName(),r.getLastName(),r.getGender(),personID);

            uDao.addUser(newUser);

            //Create Auth Token for Registration access
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            AuthToken userAuthToken = new AuthToken(tokenValue,newUser.getUsername(),currentTime);
            aDao.addAuthToken(userAuthToken);

            //Create RegisterResult object, begin fill of family tree
            rslt = new RegisterResult(tokenValue,username,personID, true);

            //Get data from JSON files to create location and name data
            String maleNameFile = new String(Files.readAllBytes((Paths.get("json/mnames.json"))));
            String femaleNameFile = new String(Files.readAllBytes((Paths.get("json/fnames.json"))));
            String surnameFile = new String(Files.readAllBytes((Paths.get("json/snames.json"))));
            String locationDataFile = new String(Files.readAllBytes((Paths.get("json/locations.json"))));

            String[] possibleMaleNames = JSONHandler.deserializeNames(maleNameFile).getData();
            String[] possibleFemaleNames = JSONHandler.deserializeNames(femaleNameFile).getData();
            String[] possibleSurnames = JSONHandler.deserializeNames(surnameFile).getData();
            Location[] possibleLocations = JSONHandler.deserializeLocationData(locationDataFile).getData();

            //Create Person and birth Event for user
            List<Persons> currentGeneration = new ArrayList<Persons>();
            List<Persons> previousGeneration = new ArrayList<Persons>();

            Persons usersPerson = new Persons(newUser.getFirstName(), newUser.getLastName(), newUser.getGender(),
                    personID, null, null, null, newUser.getUsername());

            previousGeneration.add(usersPerson);

            Random birthYearGenerator = new Random();
            int birthYear = birthYearGenerator.nextInt(2010 - 1980) + 1980;

            eventsForCurrentPerson = generateEvents(possibleLocations, birthYear, personID,
                    newUser.getUsername(), true, birthYear, eDao);

            for (int i = 0; i < eventsForCurrentPerson.size(); i++) {
                eDao.addEvent(eventsForCurrentPerson.get(i));
            }
            eventsForCurrentPerson.clear();

            //Create Persons for 4 generations for new user
            for (int i = 1; i <= 4; i++) {

                Persons personToUpdate = null;

                int peopleToAddThisGeneration = (int) Math.pow(2, i);

                //Create current generation of people with createGenerationOfPeople function
                currentGeneration.clear();
                currentGeneration = createGenerationOfPeople(possibleMaleNames, possibleFemaleNames, possibleSurnames,
                        peopleToAddThisGeneration, newUser.getUsername(), pDao);

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

            //Run loop to create birth, marriage, and death Events for all people created by FillService
            // excluding the user.
            Events marriageEvent = null;
            int generationCounter = 1;
            int peopleWithEventsCreated = 0;


            for (int i = 1; i < personsToAdd.size(); i++) {
                Persons currentPerson = personsToAdd.get(i);

                Random rand = new Random();
                int differenceInBirthYear = 20;

                //Create 3 events for the currentPerson by calling generateEvents
                //Save them to List eventsForCurrentPerson
                eventsForCurrentPerson = generateEvents(possibleLocations, birthYear - differenceInBirthYear,
                        currentPerson.getPersonID(), newUser.getUsername(), false, birthYear, eDao);

                //Update marriage events of female Persons to match their husband's as a default
                if (i % 2 == 0) { //Even numbers are females
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

            //Fill completed, return RegisterResult
            db.closeConnection(true);

            return rslt;
        }
        catch (DataAccessException | IOException e) {
            //Return error message if connection to database fails
            RegisterResult rslt = new RegisterResult("Error: Failed while accessing database", false);
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
                                              String associatedUsername, boolean birthEventOnly, int comparisonBirthYear,
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
