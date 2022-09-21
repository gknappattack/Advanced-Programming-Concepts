package model;

import com.google.gson.annotations.SerializedName;

public class Location {
    /**
     * A string containing the name of a country for an Event object. Given SerializedName "country" for identification
     * by the GSON parser.
     */
    @SerializedName("country")
    private String country;
    /**
     * A string containing the name of a city for an Event object. Given SerializedName "city" for identification
     * by the GSON parser.
     */
    @SerializedName("city")
    private String city;
    /**
     * A Float variable containing the latitude coordinates for an Event object. Given SerializedName
     * "latitude" for identification by the GSON parser.
     */
    @SerializedName("latitude")
    private Float latitude;
    /**
     * A Float variable containing the longitude coordinates for an Event object. Given SerializedName
     * "longitude" for identification by the GSON parser.
     */
    @SerializedName("longitude")
    private Float longitude;

    /**
     * Constructor for the Location model class. Location objects are created by the GSON parser, to be stored in the
     * data array of the JSONLocationData class for usage in the Fill API for creating fake user data.
     *
     * @param country The String containing the name of the country the event took place in.
     * @param city The String containing the name of the city the event took place in.
     * @param latitude The Float containing the latitude coordinates of the event.
     * @param longitude The Float containing the longitude coordinates of the event.
     */
    public Location(String country, String city, Float latitude, Float longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}
