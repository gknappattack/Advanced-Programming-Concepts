package com.gregknapp.familymap.ui;

import android.os.AsyncTask;
import android.util.Log;

import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.net.EventRequest;
import com.gregknapp.familymap.net.EventResult;
import com.gregknapp.familymap.net.PersonRequest;
import com.gregknapp.familymap.net.PersonResult;
import com.gregknapp.familymap.net.ServerProxy;

public class UpdateDataCacheTask extends AsyncTask<String, Integer, Boolean> {

    //Set up listener interface to communicate update data results to Login Fragment
    public interface Listener {
        void onDataUpdated(boolean updateSuccessful);
    }

    private Listener listener;

    public UpdateDataCacheTask(Listener l) {
        listener = l;
    }


    //Async Task to get both Person and Event arrays from server for logged in/registered user
    //If either parts of task fail, false is returned and login/registration stops
    @Override
    protected Boolean doInBackground(String... userAuthTokens) {

        //Create new server proxy with host and port information from LoginFragment
        ServerProxy updateDataProxy = new ServerProxy("192.168.86.24", "8080");
        String authToken = userAuthTokens[0];

        //Create both Event and Person requests from AuthToken from Login/Registration Tasks
        EventRequest getEventsRequest = new EventRequest(authToken);
        PersonRequest getPersonsRequest = new PersonRequest(authToken);

        //Run both getAllEvents and getAllPersons APIs, check results
        EventResult userEvents = updateDataProxy.getAllEvents(getEventsRequest);
        PersonResult userPersons = updateDataProxy.getAllPersons(getPersonsRequest);
        DataCache appCache = DataCache.getInstance();

        //Set event list/map in DataCache if event retrieval was successful
        if (userEvents.isSuccess()) {
            appCache.setEvents(userEvents.getData());
        }
        //Return false to LoginFragment if event retrieval failed
        else {
            return false;
        }

        //Set Person list/map in DataCache if person retrieval was successful
        if (userPersons.isSuccess()) {
            appCache.setPeople(userPersons.getData());

        } else {
            return false;
        }

        return true;
    }

    //Return true or false to LoginFragment based on result of Async Task
    protected void onPostExecute(Boolean updateResult) {
        listener.onDataUpdated(updateResult);
    }
}
