package com.gregknapp.familymap.model;

import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gregknapp.familymap.ui.LoginFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataCache {

    //Singleton Implementation of DataCache class
    private static DataCache _instance = new DataCache();

    public static DataCache getInstance() {
        if (_instance == null) {
            _instance = new DataCache();
        }

        return _instance;
    }

    //Event and Person Lists/Maps for storing data retrieved from server
    private Map<String, Person> people;
    private Map<String, Event> events;
    private final List<Person> peopleList;
    private final List<Event> eventList;
    private List<Event> filteredEventList;

    //Color arrays to pass on event type colors to Event/Person Activities
    private Map<String, Float> eventsToColors;
    public static Map<String, Integer> eventIconColors;

    //Booleans used in filtering event logic
    private boolean sideFiltered;
    private boolean allEventsFiltered;
    private boolean doneFiltering;

    private DataCache() {
        doneFiltering = false;
        people = new HashMap<>();
        events = new HashMap<>();
        peopleList = new ArrayList<>();
        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();
        sideFiltered = false;
        allEventsFiltered = false;
        eventsToColors = new HashMap<>();
        eventIconColors = new HashMap<>();

    }



    public void setEventsToColorsFloat(Map<String, Float> colorMap) {
        eventsToColors = colorMap;
    }
    public void setEventsToColorsInt(Map<String, Integer> colorMap) {
        eventIconColors = colorMap;
    }
    public Map<String, Float> getEventsToColors() {
        return eventsToColors;
    }

    //Setters and Getters used in UpdateDataCache Async Task to get events/people from server
    public void setPeople(Person[] people) {
        this.people.clear();
        this.peopleList.clear();

        for (int i = 0; i < people.length; i++) {
            Person currentPerson = people[i];
            String currentID = currentPerson.getPersonID();

            this.people.put(currentID, currentPerson);
            this.peopleList.add(currentPerson);
        }
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events.clear();
        this.eventList.clear();

        for (int i = 0; i < events.length; i++) {
            Event currentEvent = events[i];
            String currentID = currentEvent.getEventID();

            this.events.put(currentID, currentEvent);
            this.eventList.add(currentEvent);
        }
    }

    public Person getPersonFromID(String personID) {
        Iterator mapIterator = people.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry eventPair = (Map.Entry)mapIterator.next();
            Person currentPerson = (Person)eventPair.getValue();

            if (currentPerson.getPersonID().equals(personID)) {
                return currentPerson;
            }
        }
        return null;
    }

    //Retrieves all events for a person (including filters) which are put in
    //chronological order by helper function
    public List<Event> getEventsForPerson(String personID) {
        Iterator listIterator = getEventList().iterator();

        List<Event> eventsForPerson = new ArrayList<>();

        while (listIterator.hasNext()) {
            Event currentEvent = (Event)listIterator.next();

            if (currentEvent.getPersonID().equals(personID)) {
                eventsForPerson.add(currentEvent);

            }
        }

        System.out.println("Events added for person " + personID + " " + eventsForPerson.size());

        eventsForPerson = sortEventListByDate(eventsForPerson);

        return eventsForPerson;
    }

    //Helper function to sort events for a person
    private List<Event> sortEventListByDate(List<Event> eventsToSort) {
        List<Event> placeHolderEvents = new ArrayList<>();

        boolean sortedFinished = false;

        while (!sortedFinished)  {
            Event earliestEvent = null;
            Iterator<Event> eventIterator = eventsToSort.iterator();

            while (eventIterator.hasNext()) {
                Event currentEvent = eventIterator.next();

                //Compare years for events after first event in list
                if (earliestEvent == null) {
                    earliestEvent = currentEvent;
                }
                else {
                    if (earliestEvent.getYear() > currentEvent.getYear()) {
                        earliestEvent = currentEvent;
                    }
                }

            }
            if (earliestEvent != null) {
                placeHolderEvents.add(earliestEvent);
            }

            //Remove event added to ordered list from original list
            eventsToSort.remove(earliestEvent);

            //Check for final element in original list of events to break while loop
            if (eventsToSort.size() == 0) {
                sortedFinished = true;
            }

        }

        List<Event> sortedEvents = new ArrayList<>(placeHolderEvents);

        return sortedEvents;
    }

    //Logic to get spouse, father, mother, and children to display in a Person's
    //Person activity.
    public List<Person> getImmediateRelatives(Person currentPerson) {
        List<Person> immediateRelatives = new ArrayList<>();

        //Get spouse, father, and mother after null checking results
        Person spouse = getPersonFromID(currentPerson.getSpouseID());
        Person father = getPersonFromID(currentPerson.getFatherID());
        Person mother = getPersonFromID(currentPerson.getMotherID());

        if (spouse != null) {
            immediateRelatives.add(spouse);
        }

        if (father != null) {
            immediateRelatives.add(father);
        }

        if (mother != null) {
            immediateRelatives.add(mother);
        }

        //Call helper function to search for all children, if any.
        List<Person> children = getChildrenForID(currentPerson);
        if (children.size() > 0) {
            immediateRelatives.addAll(children);
        }

        return immediateRelatives;
    }

    //Private helper function to get children for a person
    private List<Person> getChildrenForID(Person parent) {
        Iterator mapIterator = people.entrySet().iterator();
        List<Person> children = new ArrayList<>();

        while (mapIterator.hasNext()) {
            Map.Entry eventPair = (Map.Entry)mapIterator.next();
            Person currentPerson = (Person)eventPair.getValue();


            if (currentPerson.getFatherID() != null) {
                if (currentPerson.getFatherID().equals(parent.getPersonID())){
                    children.add(currentPerson);
                }
            }
            if (currentPerson.getMotherID() != null) {
                if (currentPerson.getMotherID().equals(parent.getPersonID())) {
                    children.add(currentPerson);
                }
            }

        }
        return children;
    }

    //Getter for event list used by Map Fragment, Search Activity.
    //Logic checks for a filtered list, returning the filtered list instead of the complete
    //list if it exists.
    public List<Event> getEventList() {
        if (!doneFiltering) {
            return eventList;
        }
        else {
            if (allEventsFiltered) {
                return filteredEventList;
            }
            else if (filteredEventList.size() != 0) {
                return filteredEventList;
            }
            else {
                return eventList;
            }
        }
    }
    public List<Person> getPeopleList() {
        return peopleList;
    }
    public List<Event> getFilteredEventList() {return filteredEventList; }

    //Function to get events on father's side only when Mother side events are removed
    public void filterFatherEvents(Person rootPerson) {

        filteredEventList.addAll(getEventsForPerson(rootPerson.getPersonID()));
        Person spouse = getPersonFromID(rootPerson.getSpouseID());

        if (spouse != null) {
            filteredEventList.addAll(getEventsForPerson(spouse.getPersonID()));
        }


        Person father = getPersonFromID(rootPerson.getFatherID());

        if (father != null) {
            filteredEventList.addAll(getEventsForPerson(father.getPersonID()));
            filterSideHelper(father);
            sideFiltered = true;
        }

        System.out.println("Events in filtered father list: " + filteredEventList.size());

    }

    //Function to get events on Mother side only when father side is removed.
    //Logic included to filter specifically if both father and mothers side are filtered.
    public void filterMotherEvents(Person rootPerson) {

        filteredEventList.addAll(getEventsForPerson(rootPerson.getPersonID()));
        Person spouse = getPersonFromID(rootPerson.getSpouseID());

        if (spouse != null) {
            filteredEventList.addAll(getEventsForPerson(spouse.getPersonID()));
        }

        boolean bothSidesFiltered = false;

        //Check for boolean indicating father side has also been filtered.
        //Add user/spouse events only.
        if (sideFiltered) {
            filteredEventList.clear();

            filteredEventList.addAll(getEventsForPerson(rootPerson.getPersonID()));

            if (spouse != null) {
                filteredEventList.addAll(getEventsForPerson(spouse.getPersonID()));
            }
            bothSidesFiltered = true;
        }

        Person mother = getPersonFromID(rootPerson.getMotherID());

        if (mother != null && !bothSidesFiltered) {
            filteredEventList.addAll(getEventsForPerson(mother.getPersonID()));
            filterSideHelper(mother);
        }

        System.out.println("Events in filtered mother list: " + filteredEventList.size());
    }

    //Recursive helper function to get events for father or mother side
    //Used by filter functions above.
    private void filterSideHelper(Person person) {

        //Get events for father of person and make recursive call if not null
        Person father = getPersonFromID(person.getFatherID());
        if (father!= null) {
            filteredEventList.addAll(getEventsForPerson(father.getPersonID()));
            filterSideHelper(father);

        }

        //Get events for mother of person and make recursive call if not null
        Person mother = getPersonFromID(person.getMotherID());
        if (mother != null) {
            filteredEventList.addAll(getEventsForPerson(mother.getPersonID()));
            filterSideHelper(mother);
        }

    }

    //Function used to filter events of either gender, passed in as a parameter
    //Check s if both genders have been filtered, returning an empty list
    public void filterGenderEvents(String genderToFilter, boolean bothGendersFiltered) {
        Iterator<Event> listIterator;
        List<Event> filteredGenderEvents = new ArrayList<>();

        //Determine if filter needs to run on all events or filtered by side
        if (filteredEventList.size() != 0) {
            listIterator = filteredEventList.iterator();
        }
        else {
            listIterator = eventList.iterator();
        }

        if (!bothGendersFiltered) { //Filter events based on selected iterator

            while (listIterator.hasNext()) {
                Event currentEvent = listIterator.next();

                if (getPersonFromID(currentEvent.getPersonID()).getGender().toLowerCase().equals(genderToFilter)) {
                    filteredGenderEvents.add(currentEvent);
                }
            }

        }
        else {
            allEventsFiltered = true;
        }

        filteredEventList = filteredGenderEvents;

    }

    //Function to reset filtered event list after Settings Activity finishes
    public void clearFilteredEvents() {
        filteredEventList.clear();
        allEventsFiltered = false;
        doneFiltering = false;
        sideFiltered = false;
    }

    //Set boolean to indicate filtering is finished to properly return correct event list
    public void setDoneFiltering() {
        doneFiltering = true;
    }

    //Test code used to in JUnit testing to confirm correct people are added by
    //GetImmediateRelatives function
    public String determineRelationship(Person personToCheck, Person referencePerson) {
        if (personToCheck.getPersonID().equals(referencePerson.getFatherID())) {
            return "father";
        }
        else if (personToCheck.getPersonID().equals(referencePerson.getMotherID())) {
            return "mother";
        }
        else if (personToCheck.getPersonID().equals(referencePerson.getSpouseID())) {
            return "spouse";
        }

        else {
            if (personToCheck.getFatherID().equals(referencePerson.getPersonID())) {
                return "child of father";
            }
            else if (personToCheck.getMotherID().equals(referencePerson.getPersonID())) {
                return "child of mother";
            }
            else {
                return null;
            }
        }
    }

    //Controlling function that handles filter settings returned by the Settings Activity
    public List<Event> runEventsFilter(boolean motherSideFilter, boolean fatherSideFilter,
                                       boolean maleEventsFilter, boolean femaleEventsFilter, Person rootPerson) {

        //Reset boolean values and clear filtered list before calculating events
        clearFilteredEvents();

        //Filter sides first, checking if both side are filtered
        if (!motherSideFilter) {
            filterFatherEvents(rootPerson);
        }
        if (!fatherSideFilter) {
            filterMotherEvents(rootPerson);
        }

        //Filter genders next, removing events based on results of side filters.
        boolean femalesFiltered = false;
        if (!maleEventsFilter) {
            filterGenderEvents("f", femalesFiltered);
            femalesFiltered = true;
        }

        if (!femaleEventsFilter) {
            if (femalesFiltered) {
                filterGenderEvents("m", femalesFiltered);
            }
            else {
                filterGenderEvents("m", femalesFiltered);
            }
        }

        //Reset filtering booleans for next usage
        setDoneFiltering();

        return getEventList();
    }

    //Code to control searching events run by the Search Activity
    public List<Person> runPeopleSearch(String searchQuery) {
        List<Person> filteredList = new ArrayList<>();

        Iterator<Person> peopleIterator = peopleList.iterator();

        while (peopleIterator.hasNext()) {
            Person currentPerson = peopleIterator.next();

            //Check if query is contained in the Person's first name
            if (currentPerson.getFirstName().toLowerCase().contains(searchQuery)) {
                filteredList.add(currentPerson);
            }
            //Check if query is found in the Person's last name.
            else if (currentPerson.getLastName().toLowerCase().contains(searchQuery)) {
                filteredList.add(currentPerson);
            }
            else {
                //Do nothing - don't add person to result list
            }
        }
        return filteredList;
    }

    //Code controlling Event Searching handled by Search Activity
    public List<Event> runEventSearch(String searchQuery) {
        List<Event> filteredList = new ArrayList<>();

        Iterator<Event> eventIterator = getEventList().iterator();

        while (eventIterator.hasNext()) {
            Event currentEvent = eventIterator.next();

            //Check if query is found in Event's city name
            if (currentEvent.getCity().toLowerCase().contains(searchQuery)) {
                filteredList.add(currentEvent);
            }
            //Check if query is found in Event's Event Type String
            else if (currentEvent.getEventType().toLowerCase().contains(searchQuery)) {
                filteredList.add(currentEvent);
            }
            //Check if query is found in Event's Country name.
            else if (currentEvent.getCountry().toLowerCase().contains(searchQuery)) {
                filteredList.add(currentEvent);
            }
            //Check if query is found in the event's year string (converted to a string)
            else if (Integer.toString(currentEvent.getYear()).contains(searchQuery)) {
                filteredList.add(currentEvent);
            }
            else {
                //Do nothing - Don't add Event to result list
            }
        }
        return filteredList;
    }

    //Reset maps and lists + boolean values upon logout through Settings Event.
    public void resetDataCacheForNewUser() {
        doneFiltering = false;
        people.clear();
        events.clear();
        peopleList.clear();
        eventList.clear();
        filteredEventList.clear();
        sideFiltered = false;
        allEventsFiltered = false;
        eventsToColors.clear();
        eventIconColors.clear();
    }
}
