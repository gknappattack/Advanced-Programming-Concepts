package model;

public class JSONLocationData {

    /**
     * An Array of Location objects. Used to store sample data from the json directory in order to create
     * sample event data to fill the family tree
     */
    private Location[] data;


    public Location[] getData() {
        return data;
    }

    /**
     * Constructor for the JSONLocationData class. An array of Location objects is passed in by the GSON Json parser
     * and initialized here.
     *
     * @param data The Array of Location data passed in by the GSON parser.
     */
    public JSONLocationData(Location[] data) {
        this.data = data;
    }
}
