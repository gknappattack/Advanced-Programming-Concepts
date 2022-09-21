package com.gregknapp.familymap.net;

import android.renderscript.ScriptGroup;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerProxy {

    //Variables containing server information passed in from Task classes using server proxxy
    public static String serverHostName;
    public static String serverPortNumber;

    public ServerProxy(String serverHost, String portNumber) {
        serverHostName = serverHost;
        serverPortNumber = portNumber;
    }


    //Function to make http request for logging in a user
    public LoginResult login(LoginRequest r) {

        try {

            //Create URL passed in from login fragment
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/login");
            String requestData;
            Gson gson = new Gson();

            //Set request method and request body - close
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            requestData = gson.toJson(r);

            OutputStream reqBody = http.getOutputStream();
            writeString(requestData, reqBody);

            reqBody.close();

            //Return a successful login result
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Login Successful");

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, LoginResult.class);
            }
            //Return a failed login result
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream response = http.getErrorStream();
                String respData = readString(response);

                return gson.fromJson(respData, LoginResult.class);

            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Server code to make registration call on web API
    public RegisterResult register(RegisterRequest r) {

        try {

            //Create URL based on host and port number from login fragment
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/register");
            String requestData;
            Gson gson = new Gson();

            //Set Request method, request body - close
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            requestData = gson.toJson(r);

            OutputStream reqBody = http.getOutputStream();
            writeString(requestData, reqBody);

            reqBody.close();

            //Return a successful RegistrationResult
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Register Successful");

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, RegisterResult.class);
            }
            //Return a failed RegistrationResult
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream response = http.getErrorStream();
                String respData = readString(response);

                return gson.fromJson(respData, RegisterResult.class);
            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Server proxy code to get back EventResult containing all events for logged in user
    public EventResult getAllEvents(EventRequest r) {

        try {

            //Set up request URL from LoginFragment data
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/event");
            String requestData;
            Gson gson = new Gson();

            //Set Request Method, add Property for auth token, make request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", r.getAuthToken());

            http.connect();

            //Return a successful EventResult
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Events retrieved successfully");

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, EventResult.class);
            }
            //Return a failed EventResult
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, EventResult.class);
            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Server Proxy code to get PersonResult containing all people under logged in user
    public PersonResult getAllPersons(PersonRequest r) {

        try {

            //Set up request URL from Login Fragment
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/person");
            String requestData;
            Gson gson = new Gson();

            //Set request method, include auth token property, make request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", r.getAuthToken());

            http.connect();

            //Return successful PersonResult
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Persons retrieved successfully");

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, PersonResult.class);
            }
            //Return failed PersonResult
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);

                return gson.fromJson(respData, PersonResult.class);
            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Clear function for calling Clear Web API as part of JUnit testing only
    public void clear() {

        try {

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/clear");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);

            }
            else {
                System.out.println("Error: " + http.getResponseMessage());
            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }


    //Function to call Load Web API as part of ServerProxy JUnit testing only
    public void loadTestData(String loadJSONData) {

        try {

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/load");

            Gson gson = new Gson();

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            OutputStream reqBody = http.getOutputStream();
            writeString(loadJSONData, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);

            }
            else {
                System.out.println("Error: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
            }

        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }


    //Function to create Input stream for request body from json data
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;

        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }

        return sb.toString();
    }

    //Function to write output stream for result body to return results from API
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
