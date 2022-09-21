package model;

public class JSONNameData {
    /**
     * A generic Array of Strings used to hold various deserialized JSON arrays of name data read from a file.
     * The data array is then used by the Fill API to create data to fill a user's family tree.
     */
    private String[] data;

    public String[] getData() {
        return data;
    }

    /**
     * Constructor for the JSON Name Data class. JSONNameData objects are created through the GSON parser in the
     * JSON Handler class
     *
     * @param data The Array of Strings deserialized and passed as parameters by the GSON deserializer.
     */
    public JSONNameData(String[] data) {
        this.data = data;
    }
}
