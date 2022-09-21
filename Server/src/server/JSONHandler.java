package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.JSONLocationData;
import model.JSONNameData;
import request.LoadRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;


public class JSONHandler {

  /**
   * deserializeRegister method uses GSON to parse and create a RegisterRequest from a JSON string
   *
   * @param json String containing information for Register Request
   * @return RegisterRequest object with fields from JSON String
   */
    public static RegisterRequest deserializeRegister(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RegisterRequest.class);
    }

  /**
   * deserializeLogin method uses GSON to parse and create a LoginRequest from a JSON string
   *
   * @param json String containing information for LoginRequest
   * @return LoginRequest object with fields from JSON String
   */
    public static LoginRequest deserializeLogin(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LoginRequest.class);
    }

    /**
     * deserializeLoad method uses GSON to parse and create a LoadRequest from a JSON string.
     * Uses GsonBuilder and .serializeNulls to check for and include null values that may appear
     * in the Users, Persons, or Event objects included in the JSON String.
     *
     * @param json String containing information for LoadRequest
     * @return LoadRequest object with fields from JSON String
     */
    public static LoadRequest deserializeLoad(String json) {
        Gson gson = new GsonBuilder()
                .serializeNulls().create();
        return gson.fromJson(json, LoadRequest.class);
    }

    /**
     * deserializeLoad method uses GSON to parse and create a LoadRequest from a JSON string.
     * Uses GsonBuilder and .serializeNulls to check for and include null values that may appear
     * in the Users, Persons, or Event objects included in the JSON String.
     *
     * @param json String containing information for LoadRequest
     * @return LoadRequest object with fields from JSON String
     */
    public static JSONNameData deserializeNames(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JSONNameData.class);
    }

  /**
   * deserializeLocationData method uses GSON to parse and create a LocationData object from a JSON string.
   * Uses GsonBuilder and .serializeNulls to check for and include null values that may appear
   * in the LocationData JSON string.
   *
   * @param json String containing information for the LocationData object.
   * @return LocationData object with fields from JSON String
   */
    public static JSONLocationData deserializeLocationData(String json) {
        Gson gson = new GsonBuilder()
                .serializeNulls().create();
      return gson.fromJson(json, JSONLocationData.class);
    }

  /**
   * serializeRegResult method uses GSON to parse a RegisterResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The RegisterResult object to be serialized into a JSON String.
   * @return A String containing the RegisterResult information as a JSON String.
   */
    public static String serializeRegResult(RegisterResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeLoginResult method uses GSON to parse a LoginResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The LoginResult object to be serialized into a JSON String.
   * @return A String containing the LoginResult information as a JSON String.
   */
    public static String serializeLoginResult(LoginResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeEventIDResult method uses GSON to parse a EventIDResult object and serialize it to a String
   * to be written out to the HTTP Response Body. The value in the year variable in the EventIDResult is checked
   * in order to determine if a failing or passing EventIDResult will be serialized, in order to prevent int and
   * float values from appearing in the failed EventIDResult's response body.
   *
   * @param r The EventIDResult object to be serialized into a JSON String.
   * @return A String containing the EventIDResult information as a JSON String.
   */
    public static String serializeEventIDResult(EventIDResult r) {
        Gson gson;

        if (r.getYear() == 0) {
          gson = new GsonBuilder()
                  .excludeFieldsWithoutExposeAnnotation()
                  .create();
        }
        else {
          gson = new Gson();
        }
        return gson.toJson(r);
    }

  /**
   * serializePersonIDResult method uses GSON to parse a PersonIDResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The PersonIDResult object to be serialized into a JSON String.
   * @return A String containing the PersonIDResult information as a JSON String.
   */
    public static String serializePersonIDResult(PersonIDResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeClearResult method uses GSON to parse a ClearResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The ClearResult object to be serialized into a JSON String.
   * @return A String containing the ClearResult information as a JSON String.
   */
    public static String serializeClearResult(ClearResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializePersonResult method uses GSON to parse a PersonResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The PersonResult object to be serialized into a JSON String.
   * @return A String containing the PersonResult information as a JSON String.
   */
    public static String serializePersonResult(PersonResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeEventResult method uses GSON to parse a EventResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The EventResult object to be serialized into a JSON String.
   * @return A String containing the EventResult information as a JSON String.
   */
    public static String serializeEventResult(EventResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeLoadResult method uses GSON to parse a LoadResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The LoadResult object to be serialized into a JSON String.
   * @return A String containing the LoadResult information as a JSON String.
   */
    public static String serializeLoadResult(LoadResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

  /**
   * serializeFillResult method uses GSON to parse a FillResult object and serialize it to a String
   * to be written out to the HTTP Response Body.
   *
   * @param r The FillResult object to be serialized into a JSON String.
   * @return A String containing the FillResult information as a JSON String.
   */
    public static String serializeFillResult(FillResult r) {
        Gson gson = new Gson();
        return gson.toJson(r);
    }

}

