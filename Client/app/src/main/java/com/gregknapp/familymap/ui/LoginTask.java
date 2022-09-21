package com.gregknapp.familymap.ui;

import android.os.AsyncTask;

import com.gregknapp.familymap.net.LoginRequest;
import com.gregknapp.familymap.net.LoginResult;
import com.gregknapp.familymap.net.ServerProxy;

public class LoginTask extends AsyncTask<LoginRequest, Integer, LoginResult> {

    //Set up listener interface to callback to LoginFragment
    public interface Listener {
        void onLoginFinished(LoginResult loginResult);
    }

    private Listener listener;

    public LoginTask(Listener l) {
        listener = l;
    }

    //Login Async Task - Get data from LoginFragment and run with ServerProxy
    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests) {

        //Get login information from Fragment
        LoginRequest currentRequest = loginRequests[0];

        //Create new server proxy with host and port information - run login request
        ServerProxy registerProxy = new ServerProxy("192.168.86.24", "8080");
        LoginResult loginRslt = registerProxy.login(currentRequest);

        return loginRslt;
    }

    //Callback information to fragment after Async task finishes
    protected void onPostExecute(LoginResult loginResult) {
        listener.onLoginFinished(loginResult);
    }
}