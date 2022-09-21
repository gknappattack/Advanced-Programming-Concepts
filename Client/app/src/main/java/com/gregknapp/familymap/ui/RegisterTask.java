package com.gregknapp.familymap.ui;

import android.os.AsyncTask;
import android.util.Log;

import com.gregknapp.familymap.net.RegisterRequest;
import com.gregknapp.familymap.net.RegisterResult;
import com.gregknapp.familymap.net.ServerProxy;

public class RegisterTask extends AsyncTask<RegisterRequest, Integer, RegisterResult> {

    //Set up listener interface to callback RegisterResult to LoginFragment
    public interface Listener {
        void onRegisterFinished(RegisterResult regResult);
    }

    private Listener listener;

    public RegisterTask(Listener l) {
        listener = l;
    }

    //Register Async task - Get info from LoginFragment and contact server through proxy
    @Override
    protected RegisterResult doInBackground(RegisterRequest... registerRequests) {

        //Get Registration request from Fragment information
        RegisterRequest currentRequest = registerRequests[0];

        //Create new server proxy and run register API
        ServerProxy registerProxy = new ServerProxy("192.168.86.24", "8080");
        RegisterResult regRslt = registerProxy.register(currentRequest);

        return regRslt;
    }

    //Call back RegisterResult to LoginFragement after Async task finishes
    protected void onPostExecute(RegisterResult regResult) {
        listener.onRegisterFinished(regResult);
    }
}