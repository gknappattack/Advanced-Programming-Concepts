package com.gregknapp.familymap.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.gregknapp.familymap.R;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{

    //Set up fragments and fragment manager to handle switching between the two
    private LoginFragment loginFragment;
    private MapFragment mapFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = this.getSupportFragmentManager();
        loginFragment = (LoginFragment)fm.findFragmentById(R.id.fragmentContainer);

        //Create new LoginFragment upon app starting
        if (loginFragment == null) {
            loginFragment = new LoginFragment(this);

            //Set default server host and port number for current computer in bundle
            Bundle args = new Bundle();
            args.putString(LoginFragment.ARG_PARAM1,"192.168.86.24");
            args.putString(LoginFragment.ARG_PARAM2,"8080");
            loginFragment.setArguments(args);

            //Start LoginFragment
            fm.beginTransaction()
                    .add(R.id.fragmentContainer,loginFragment)
                    .commit();
        }
    }

    //Listener function receiving callback from LoginFragment to start MapFragment
    @Override
    public void onSuccessfulLogin(boolean loginSuccess) {

        //Create new MapFragment after Login is successful
        if (mapFragment == null) {
            mapFragment = new MapFragment();

            //Create MapFragment with no arguments in bundle
            Bundle args = new Bundle();
            mapFragment.setArguments(args);
            fm.beginTransaction()
                    .replace(R.id.fragmentContainer,mapFragment)
                    .commit();
        }
    }

}