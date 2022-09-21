package com.gregknapp.familymap;

import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.model.Event;
import com.gregknapp.familymap.model.Person;
import com.gregknapp.familymap.net.EventRequest;
import com.gregknapp.familymap.net.EventResult;
import com.gregknapp.familymap.net.LoginRequest;
import com.gregknapp.familymap.net.LoginResult;
import com.gregknapp.familymap.net.PersonRequest;
import com.gregknapp.familymap.net.PersonResult;
import com.gregknapp.familymap.net.RegisterRequest;
import com.gregknapp.familymap.net.RegisterResult;
import com.gregknapp.familymap.net.ServerProxy;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataModelTest extends TestCase {
    private ServerProxy testServer;
    private DataCache appData;
    String userPersonID;

    @BeforeEach
    public void setUp() {
        //Set up Server Proxy and load test data JSON
        testServer = new ServerProxy("localhost", "8080");
        testServer.clear();
        testServer.loadTestData(loadJSONData);
        appData = DataCache.getInstance();

        //Login user "sheila" and store her Person, Events, and PersonID information for testing
        LoginRequest r = new LoginRequest("sheila", "parker");
        LoginResult result = testServer.login(r);

        userPersonID = result.getPersonID();

        PersonRequest getPeopleForUser = new PersonRequest(result.getAuthToken());
        EventRequest getEventsForUser = new EventRequest(result.getAuthToken());

        PersonResult foundPeople = testServer.getAllPersons(getPeopleForUser);
        EventResult foundEvents = testServer.getAllEvents(getEventsForUser);

        Person[] allPeopleForUser = foundPeople.getData();
        Event[] allEventsForUser = foundEvents.getData();

        appData.setPeople(allPeopleForUser);
        appData.setEvents(allEventsForUser);
    }

    @AfterEach
    public void tearDown() {
        //Clear server for next test
        testServer.clear();
    }

    @Test
    public void testCalculateFamilyRelationshipsPass() {
        Person userPerson = appData.getPersonFromID(userPersonID);

        //Get list of related people for user
        List<Person> usersDirectRelatives = appData.getImmediateRelatives(userPerson);
        assertTrue(usersDirectRelatives.size() > 0);

        Iterator<Person> personIterator = usersDirectRelatives.iterator();

        while (personIterator.hasNext()) {
            Person personToCheck = personIterator.next();

            //Call Data Cache function to determine relationship using IDs. Returns null if invalid person
            String personRelationship = appData.determineRelationship(personToCheck, userPerson);
            assertNotNull(personRelationship);
            System.out.println(personRelationship);

            //Check if returned relationship is accurate to the ID's used to call the functions
            if (personRelationship.equals("father")) {
                assertEquals(userPerson.getFatherID(), personToCheck.getPersonID());
            }
            else if (personRelationship.equals("mother")) {
                assertEquals(userPerson.getMotherID(), personToCheck.getPersonID());
            }
            else if (personRelationship.equals("spouse")) {
                assertEquals(userPerson.getSpouseID(), personToCheck.getPersonID());
            }
            else if (personRelationship.equals("child of father")) {
                assertEquals(userPerson.getPersonID(), personToCheck.getFatherID());
            }
            else if (personRelationship.equals("child of mother")) {
                assertEquals(userPerson.getPersonID(), personToCheck.getMotherID());
            }

        }
    }

    @Test
    public void testCalculateFamilyRelationshipsFail() {
        Person notInDataPerson = new Person("Greg", "Knapp", "m",
                "fakeID", "nodadfound", "nomotherfound", "nospouse",
                "gnapp");
        Person userPerson = appData.getPersonFromID(userPersonID);
        List<Person> badRelationshipResults = appData.getImmediateRelatives(notInDataPerson);

        //Check that get related people function properly returns an empty list
        assertNotNull(badRelationshipResults);
        assertEquals(0, badRelationshipResults.size());

        //Directly check that relationship calculating function properly returns null
        //For the relationship name when checking unrelated people
        assertNull(appData.determineRelationship(notInDataPerson, userPerson));
    }


    @Test
    public void testCheckFilteredEventsPass() {
        boolean motherSideFilter = true;
        boolean fatherSideFilter = true;
        boolean maleEventFilter = true;
        boolean femaleEventFilter = true;

        Person userPerson =appData.getPersonFromID(userPersonID);

        List<Event> filteredEvents;

        //Test event size with mother side events filtered
        motherSideFilter = false;
        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if the algorithm returns 11 events as expected from the test data
        assertEquals(filteredEvents.size(), 11);

        motherSideFilter = true;
        fatherSideFilter = false;

        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if algorithm returns expected 11 events
        assertEquals(filteredEvents.size(), 11);

        //Test event list size after filtering all male events
        fatherSideFilter = true;
        maleEventFilter = false;

        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if algorithm returns expected 10 events
        assertEquals(filteredEvents.size(), 10);

        //Test event list size after filtering all female events
        maleEventFilter = true;
        femaleEventFilter = false;

        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if algorithm returns expected 6 events
        assertEquals(filteredEvents.size(), 6);

        //Test event list size after a mix of gender and side filters are applied
        femaleEventFilter = true;
        maleEventFilter = false;
        fatherSideFilter = false;

        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if algorithm returns expected 8 events under these conditions
        assertEquals(filteredEvents.size(), 8);

    }

    @Test
    public void testCheckFilteredEventsFail() {
        boolean motherSideFilter = true;
        boolean fatherSideFilter = true;
        boolean maleEventFilter = true;
        boolean femaleEventFilter = true;

        Person userPerson = appData.getPersonFromID(userPersonID);

        List<Event> filteredEvents;

        //Test that algorithm returns full event list when no filters are applied
        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if the algorithm returns 16 events as expected
        assertEquals(filteredEvents.size(), 16);

        //Test that algorithm returns correctly when both genders are filtered
        maleEventFilter = false;
        femaleEventFilter = false;

        filteredEvents = appData.runEventsFilter(motherSideFilter, fatherSideFilter,
                maleEventFilter, femaleEventFilter, userPerson);

        //Check if algorithm returns size 0 list
        assertEquals(filteredEvents.size(), 0);

    }

    @Test
    public void testEventsInDateOrderPass() {

        //Get events for user which are sorted by helper function in Data Cache class
        List<Event> eventsForPerson = appData.getEventsForPerson(userPersonID);

        Iterator<Event> eventIterator = eventsForPerson.iterator();
        Event currentEvent = null;
        Event followingEvent = null;

        //Loop through events for user person to check chronological order of the years
        while (eventIterator.hasNext()) {
            followingEvent = eventIterator.next();

            if (currentEvent != null) {
                assertTrue(currentEvent.getYear() <= followingEvent.getYear());
            }
            else { //Check that first event is birth event
                assertEquals(followingEvent.getEventType().toLowerCase(), "birth");
            }

            currentEvent = followingEvent;

            if (eventIterator.hasNext()) {
                followingEvent = eventIterator.next();
            }
            else { //Check that final event is death event
                assertEquals(followingEvent.getEventType().toLowerCase(), "death");
            }
        }
    }

    @Test
    public void testEventsInDateOrderFail() {
        List<Event> notFoundEvents = appData.getEventsForPerson("badID");

        //Test that event finding and sorting handles a bad request properly by returning an empty array.
        assertEquals(notFoundEvents.size(), 0);

    }

    @Test
    public void testEventSearchPass() {

        List<Person> personSearchResult;
        List<Event> eventSearchResult;

        //Test for generic search containing many results
        String queryToSearch = "a";

        personSearchResult = appData.runPeopleSearch(queryToSearch);
        eventSearchResult = appData.runEventSearch(queryToSearch);

        Iterator<Person> personIterator = personSearchResult.iterator();


        //Check if all results for People and Events actually contain search criteria
        while (personIterator.hasNext()) {
            Person currentPerson = personIterator.next();

            assertTrue(currentPerson.getFirstName().toLowerCase().contains(queryToSearch) ||
                    currentPerson.getLastName().toLowerCase().contains(queryToSearch));
        }

        Iterator<Event> eventIterator = eventSearchResult.iterator();

        while (eventIterator.hasNext()) {
            Event currentEvent = eventIterator.next();

            assertTrue(currentEvent.getEventType().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCity().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCountry().toLowerCase().contains(queryToSearch) ||
                    Integer.toString(currentEvent.getYear()).toLowerCase().contains(queryToSearch));
        }

        //Test for specific name only returning 1 Person and 0 Events
        queryToSearch = "ken";

        personSearchResult = appData.runPeopleSearch(queryToSearch);
        eventSearchResult = appData.runEventSearch(queryToSearch);

        personIterator = personSearchResult.iterator();

        //Ensure that a person was returned as a result
        assertTrue(personSearchResult.size() > 0);

        //Check if all results for People and Events actually contain search criteria
        while (personIterator.hasNext()) {
            Person currentPerson = personIterator.next();

            assertTrue(currentPerson.getFirstName().toLowerCase().contains(queryToSearch) ||
                    currentPerson.getLastName().toLowerCase().contains(queryToSearch));
        }

        eventIterator = eventSearchResult.iterator();

        while (eventIterator.hasNext()) {
            Event currentEvent = eventIterator.next();

            assertTrue(currentEvent.getEventType().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCity().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCountry().toLowerCase().contains(queryToSearch) ||
                    Integer.toString(currentEvent.getYear()).toLowerCase().contains(queryToSearch));
        }


        //Check if number results work properly -
        queryToSearch = "19";

        personSearchResult = appData.runPeopleSearch(queryToSearch);
        eventSearchResult = appData.runEventSearch(queryToSearch);

        personIterator = personSearchResult.iterator();

        //Check if all results for People and Events actually contain search criteria
        while (personIterator.hasNext()) {
            Person currentPerson = personIterator.next();

            assertTrue(currentPerson.getFirstName().toLowerCase().contains(queryToSearch) ||
                    currentPerson.getLastName().toLowerCase().contains(queryToSearch));
        }

        eventIterator = eventSearchResult.iterator();

        //Ensure that events were actually returned as is expected
        assertTrue(eventSearchResult.size() > 0);

        while (eventIterator.hasNext()) {
            Event currentEvent = eventIterator.next();

            assertTrue(currentEvent.getEventType().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCity().toLowerCase().contains(queryToSearch) ||
                    currentEvent.getCountry().toLowerCase().contains(queryToSearch) ||
                    Integer.toString(currentEvent.getYear()).toLowerCase().contains(queryToSearch));
        }
    }

    @Test
    public void testEventSearchFail() {
        List<Person> personSearchResult;
        List<Event> eventSearchResult;

        //Test for search that will return 0 results for both Events and People
        String queryToSearch = "Greg";

        personSearchResult = appData.runPeopleSearch(queryToSearch);
        eventSearchResult = appData.runEventSearch(queryToSearch);

        //Check if an Empty list is returned for both result sets
        assertEquals(personSearchResult.size(), 0);
        assertEquals(eventSearchResult.size(), 0);

    }



    private String loadJSONData =
            "{\n" +
                    "    \"users\": [\n" +
                    "        {\n" +
                    "            \"userName\": \"sheila\",\n" +
                    "            \"password\": \"parker\",\n" +
                    "            \"email\": \"sheila@parker.com\",\n" +
                    "            \"firstName\": \"Sheila\",\n" +
                    "            \"lastName\": \"Parker\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Sheila_Parker\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userName\": \"patrick\",\n" +
                    "            \"password\": \"spencer\",\n" +
                    "            \"email\": \"patrick@spencer.com\",\n" +
                    "            \"firstName\": \"Patrick\",\n" +
                    "            \"lastName\": \"Spencer\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Patrick_Spencer\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"persons\": [\n" +
                    "        {\n" +
                    "            \"firstName\": \"Sheila\",\n" +
                    "            \"lastName\": \"Parker\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"spouseID\": \"Davis_Hyer\",\n" +
                    "            \"fatherID\": \"Blaine_McGary\",\n" +
                    "            \"motherID\": \"Betty_White\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Davis\",\n" +
                    "            \"lastName\": \"Hyer\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Davis_Hyer\",\n" +
                    "            \"spouseID\": \"Sheila_Parker\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Blaine\",\n" +
                    "            \"lastName\": \"McGary\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Blaine_McGary\",\n" +
                    "            \"fatherID\": \"Ken_Rodham\",\n" +
                    "            \"motherID\": \"Mrs_Rodham\",\n" +
                    "            \"spouseID\": \"Betty_White\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Betty\",\n" +
                    "            \"lastName\": \"White\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Betty_White\",\n" +
                    "            \"fatherID\": \"Frank_Jones\",\n" +
                    "            \"motherID\": \"Mrs_Jones\",\n" +
                    "            \"spouseID\": \"Blaine_McGary\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Ken\",\n" +
                    "            \"lastName\": \"Rodham\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Ken_Rodham\",\n" +
                    "            \"spouseID\": \"Mrs_Rodham\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Mrs\",\n" +
                    "            \"lastName\": \"Rodham\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Mrs_Rodham\",\n" +
                    "            \"spouseID\": \"Ken_Rodham\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Frank\",\n" +
                    "            \"lastName\": \"Jones\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Frank_Jones\",\n" +
                    "            \"spouseID\": \"Mrs_Jones\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Mrs\",\n" +
                    "            \"lastName\": \"Jones\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Mrs_Jones\",\n" +
                    "            \"spouseID\": \"Frank_Jones\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"No\",\n" +
                    "            \"lastName\": \"Relatives\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"No_Relatives\",\n" +
                    "            \"associatedUsername\": \"patrick\",\n" +
                    "            \"fatherID\": \"Happy_Birthday\",\n" +
                    "            \"motherID\": \"Golden_Boy\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Patrick\",\n" +
                    "            \"lastName\": \"Wilson\",\n" +
                    "            \"gender\": \"m\",\n" +
                    "            \"personID\": \"Happy_Birthday\",\n" +
                    "            \"associatedUsername\": \"patrick\",\n" +
                    "            \"spouseID\": \"Golden_Boy\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"firstName\": \"Spencer\",\n" +
                    "            \"lastName\": \"Seeger\",\n" +
                    "            \"gender\": \"f\",\n" +
                    "            \"personID\": \"Golden_Boy\",\n" +
                    "            \"associatedUsername\": \"patrick\",\n" +
                    "            \"spouseID\": \"Happy_Birthday\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"events\": [\n" +
                    "        {\n" +
                    "            \"eventType\": \"birth\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"city\": \"Melbourne\",\n" +
                    "            \"country\": \"Australia\",\n" +
                    "            \"latitude\": -36.1833,\n" +
                    "            \"longitude\": 144.9667,\n" +
                    "            \"year\": 1970,\n" +
                    "            \"eventID\": \"Sheila_Birth\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"marriage\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"city\": \"Los Angeles\",\n" +
                    "            \"country\": \"United States\",\n" +
                    "            \"latitude\": 34.0500,\n" +
                    "            \"longitude\": -117.7500,\n" +
                    "            \"year\": 2012,\n" +
                    "            \"eventID\": \"Sheila_Marriage\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"completed asteroids\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"city\": \"Qaanaaq\",\n" +
                    "            \"country\": \"Denmark\",\n" +
                    "            \"latitude\": 77.4667,\n" +
                    "            \"longitude\": -68.7667,\n" +
                    "            \"year\": 2014,\n" +
                    "            \"eventID\": \"Sheila_Asteroids\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"COMPLETED ASTEROIDS\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"city\": \"Qaanaaq\",\n" +
                    "            \"country\": \"Denmark\",\n" +
                    "            \"latitude\": 74.4667,\n" +
                    "            \"longitude\": -60.7667,\n" +
                    "            \"year\": 2014,\n" +
                    "            \"eventID\": \"Other_Asteroids\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"death\",\n" +
                    "            \"personID\": \"Sheila_Parker\",\n" +
                    "            \"city\": \"Hohhot\",\n" +
                    "            \"country\": \"China\",\n" +
                    "            \"latitude\": 40.2444,\n" +
                    "            \"longitude\": 111.6608,\n" +
                    "            \"year\": 2015,\n" +
                    "            \"eventID\": \"Sheila_Death\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"birth\",\n" +
                    "            \"personID\": \"Davis_Hyer\",\n" +
                    "            \"city\": \"Hakodate\",\n" +
                    "            \"country\": \"Japan\",\n" +
                    "            \"latitude\": 41.7667,\n" +
                    "            \"longitude\": 140.7333,\n" +
                    "            \"year\": 1970,\n" +
                    "            \"eventID\": \"Davis_Birth\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"birth\",\n" +
                    "            \"personID\": \"Blaine_McGary\",\n" +
                    "            \"city\": \"Bratsk\",\n" +
                    "            \"country\": \"Russia\",\n" +
                    "            \"latitude\": 56.1167,\n" +
                    "            \"longitude\": 101.6000,\n" +
                    "            \"year\": 1948,\n" +
                    "            \"eventID\": \"Blaine_Birth\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"death\",\n" +
                    "            \"personID\": \"Betty_White\",\n" +
                    "            \"city\": \"Birmingham\",\n" +
                    "            \"country\": \"United Kingdom\",\n" +
                    "            \"latitude\": 52.4833,\n" +
                    "            \"longitude\": -0.1000,\n" +
                    "            \"year\": 2017,\n" +
                    "            \"eventID\": \"Betty_Death\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"Graduated from BYU\",\n" +
                    "            \"personID\": \"Ken_Rodham\",\n" +
                    "            \"country\": \"United States\",\n" +
                    "            \"city\": \"Provo\",\n" +
                    "            \"latitude\": 40.2338,\n" +
                    "            \"longitude\": -111.6585,\n" +
                    "            \"year\": 1879,\n" +
                    "            \"eventID\": \"BYU_graduation\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"marriage\",\n" +
                    "            \"personID\": \"Ken_Rodham\",\n" +
                    "            \"country\": \"North Korea\",\n" +
                    "            \"city\": \"Wonsan\",\n" +
                    "            \"latitude\": 39.15,\n" +
                    "            \"longitude\": 127.45,\n" +
                    "            \"year\": 1895,\n" +
                    "            \"eventID\": \"Rodham_Marriage\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"Did a backflip\",\n" +
                    "            \"personID\": \"Mrs_Rodham\",\n" +
                    "            \"country\": \"Mexico\",\n" +
                    "            \"city\": \"Mexicali\",\n" +
                    "            \"latitude\": 32.6667,\n" +
                    "            \"longitude\": -114.5333,\n" +
                    "            \"year\": 1890,\n" +
                    "            \"eventID\": \"Mrs_Rodham_Backflip\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"learned Java\",\n" +
                    "            \"personID\": \"Mrs_Rodham\",\n" +
                    "            \"country\": \"Algeria\",\n" +
                    "            \"city\": \"Algiers\",\n" +
                    "            \"latitude\": 36.7667,\n" +
                    "            \"longitude\": 3.2167,\n" +
                    "            \"year\": 1890,\n" +
                    "            \"eventID\": \"Mrs_Rodham_Java\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"Caught a frog\",\n" +
                    "            \"personID\": \"Frank_Jones\",\n" +
                    "            \"country\": \"Bahamas\",\n" +
                    "            \"city\": \"Nassau\",\n" +
                    "            \"latitude\": 25.0667,\n" +
                    "            \"longitude\": -76.6667,\n" +
                    "            \"year\": 1993,\n" +
                    "            \"eventID\": \"Jones_Frog\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"marriage\",\n" +
                    "            \"personID\": \"Frank_Jones\",\n" +
                    "            \"country\": \"Ghana\",\n" +
                    "            \"city\": \"Tamale\",\n" +
                    "            \"latitude\": 9.4,\n" +
                    "            \"longitude\": 0.85,\n" +
                    "            \"year\": 1997,\n" +
                    "            \"eventID\": \"Jones_Marriage\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"Ate Brazilian Barbecue\",\n" +
                    "            \"personID\": \"Mrs_Jones\",\n" +
                    "            \"country\": \"Brazil\",\n" +
                    "            \"city\": \"Curitiba\",\n" +
                    "            \"latitude\": -24.5833,\n" +
                    "            \"longitude\": -48.75,\n" +
                    "            \"year\": 2012,\n" +
                    "            \"eventID\": \"Mrs_Jones_Barbecue\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"Learned to Surf\",\n" +
                    "            \"personID\": \"Mrs_Jones\",\n" +
                    "            \"country\": \"Australia\",\n" +
                    "            \"city\": \"Gold Coast\",\n" +
                    "            \"latitude\": -27.9833,\n" +
                    "            \"longitude\": 153.4,\n" +
                    "            \"year\": 2000,\n" +
                    "            \"eventID\": \"Mrs_Jones_Surf\",\n" +
                    "            \"associatedUsername\": \"sheila\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"birth\",\n" +
                    "            \"personID\": \"Patrick_Spencer\",\n" +
                    "            \"city\": \"Grise Fiord\",\n" +
                    "            \"country\": \"Canada\",\n" +
                    "            \"latitude\": 76.4167,\n" +
                    "            \"longitude\": -81.1,\n" +
                    "            \"year\": 2016,\n" +
                    "            \"eventID\": \"Thanks_Woodfield\",\n" +
                    "            \"associatedUsername\": \"patrick\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"marriage\",\n" +
                    "            \"personID\": \"Happy_Birthday\",\n" +
                    "            \"city\": \"Boise\",\n" +
                    "            \"country\": \"United States\",\n" +
                    "            \"latitude\": 43.6167,\n" +
                    "            \"longitude\": -115.8,\n" +
                    "            \"year\": 2016,\n" +
                    "            \"eventID\": \"True_Love\",\n" +
                    "            \"associatedUsername\": \"patrick\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"eventType\": \"marriage\",\n" +
                    "            \"personID\": \"Golden_Boy\",\n" +
                    "            \"city\": \"Boise\",\n" +
                    "            \"country\": \"United States\",\n" +
                    "            \"latitude\": 43.6167,\n" +
                    "            \"longitude\": -115.8,\n" +
                    "            \"year\": 2016,\n" +
                    "            \"eventID\": \"Together_Forever\",\n" +
                    "            \"associatedUsername\": \"patrick\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
}