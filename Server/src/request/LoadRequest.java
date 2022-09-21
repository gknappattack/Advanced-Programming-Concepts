package request;

import model.Users;
import model.Persons;
import model.Events;

public class LoadRequest {

    /**
     *An Array of Users objects to be loaded into the Users database.
     */
    private Users[] users;
    /**
     * An Array of Persons objects to be loaded into the Persons database.
     */
    private Persons[] persons;
    /**
     * An Array of Events objects to be loaded into the Events database.
     */
    private Events[] events;

    /**
     * The constructor for the LoadRequest class that fills the userArray, personArray, and eventArray
     * fields. The request is created to be sent to the LoadService class for processing.
     *
     * @param userArray The given Array of Users to be added.
     * @param personArray The given Array of Persons to be added.
     * @param eventArray The given Array of Events to be added.
     */
    public LoadRequest(Users[] userArray, Persons[] personArray, Events[] eventArray) {
        this.users = userArray;
        this.persons = personArray;
        this.events = eventArray;
    }

    public Users[] getUserArray() {
        return users;
    }

    public Persons[] getPersonArray() {
        return persons;
    }

    public Events[] getEventArray() {
        return events;
    }
}
