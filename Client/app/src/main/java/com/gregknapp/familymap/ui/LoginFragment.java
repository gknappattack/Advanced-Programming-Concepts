package com.gregknapp.familymap.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.model.Person;
import com.gregknapp.familymap.net.LoginRequest;
import com.gregknapp.familymap.net.LoginResult;
import com.gregknapp.familymap.net.RegisterRequest;
import com.gregknapp.familymap.net.RegisterResult;
import com.gregknapp.familymap.net.ServerProxy;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements RegisterTask.Listener, LoginTask.Listener, UpdateDataCacheTask.Listener {

    //Listener checking for a successful login - calls back to MainActivity
    public interface Listener {
        void onSuccessfulLogin(boolean loginSuccess);
    }
    private Listener listener;

    //Argument parameter keys for default host and port numbers passed from main event
    public static final String ARG_PARAM1 = "serverHostName";
    public static final String ARG_PARAM2 = "serverPortNumber";

    //Strings containing server information for ServerProxy calls
    private String serverHost;
    private String serverPort;

    //Pointers for each view in LoginFragment
    private TextView hostTextView;
    private EditText hostEditText;
    private TextView portTextView;
    private EditText portEditText;
    private TextView usernameTextView;
    private EditText usernameEditText;
    private TextView passwordTextView;
    private EditText passwordEditText;
    private TextView firstNameTextView;
    private EditText firstNameEditText;
    private TextView lastNameTextView;
    private EditText lastNameEditText;
    private TextView emailTextView;
    private EditText emailEditText;
    private TextView genderTextView;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button signInButton;
    private Button registerButton;
    private String userPersonID;

    //Static variable containing information of logged in user referenced by all activites
    public static Person loggedInUser;

    //Text watcher to oversee checking of conditions to allow usage of login/register buttons
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //If text changes, check if conditions are met to enable login/register buttons
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkTextFieldsToEnableButtons();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //On-click listener for radio buttton - also checks conditions for registration button
    private OnClickListener radioListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            checkTextFieldsToEnableButtons();
        }
    };


    public LoginFragment(Listener l) {
        // Required empty public constructor
        listener = l;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serverHostName Parameter 1.
     * @param serverPortNumber Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String serverHostName, String serverPortNumber, Listener l) {
        LoginFragment fragment = new LoginFragment(l);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, serverHostName);
        args.putString(ARG_PARAM2, serverPortNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set defualt values for host and port number from Main Activity
        if (getArguments() != null) {
            serverHost = getArguments().getString(ARG_PARAM1);
            serverPort = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        //Set default values for host and port, get pointers and set text watcher
        //For each edit text view
        hostTextView = (TextView)v.findViewById(R.id.serverHostTextView);
        hostEditText = (EditText)v.findViewById(R.id.serverHostEditText);
        hostEditText.setText(serverHost);
        hostEditText.addTextChangedListener(textWatcher);

        portTextView = (TextView)v.findViewById(R.id.serverPortTextView);
        portEditText = (EditText)v.findViewById(R.id.serverPortEditText);
        portEditText.setText(serverPort);
        portEditText.addTextChangedListener(textWatcher);

        usernameTextView = (TextView)v.findViewById(R.id.usernameTextView);
        usernameEditText = (EditText)v.findViewById(R.id.usernameEditText);
        usernameEditText.addTextChangedListener(textWatcher);

        passwordTextView = (TextView)v.findViewById(R.id.passwordTextView);
        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
        passwordEditText.addTextChangedListener(textWatcher);

        firstNameTextView = (TextView)v.findViewById(R.id.firstNameTextView);
        firstNameEditText = (EditText)v.findViewById(R.id.firstNameEditText);
        passwordEditText.addTextChangedListener(textWatcher);

        lastNameTextView = (TextView)v.findViewById(R.id.lastNameTextView);
        lastNameEditText = (EditText)v.findViewById(R.id.lastNameEditText);
        lastNameEditText.addTextChangedListener(textWatcher);

        emailTextView = (TextView)v.findViewById(R.id.emailTextView);
        emailEditText = (EditText)v.findViewById(R.id.emailEditText);
        emailEditText.addTextChangedListener(textWatcher);

        genderTextView = (TextView)v.findViewById(R.id.genderTextView);
        maleRadioButton = (RadioButton)v.findViewById(R.id.maleRadioButton);
        maleRadioButton.setOnClickListener(radioListener);
        femaleRadioButton = (RadioButton)v.findViewById(R.id.femaleRadioButton);
        femaleRadioButton.setOnClickListener(radioListener);

        signInButton = (Button)v.findViewById(R.id.sign_in_button);
        registerButton = (Button)v.findViewById(R.id.register_button);

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        //Onclick listener for log in button
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create LoginRequest with info from text views
                //Use to run LoginTask Async
                String loginUsername = usernameEditText.getText().toString();
                String loginPassword = passwordEditText.getText().toString();

                Log.d("On-click Login","Username: " + loginUsername);
                Log.d("On-click login", "Password: " + loginPassword);

                LoginRequest loginReq = new LoginRequest(loginUsername, loginPassword);

                LoginTask task = new LoginTask(LoginFragment.this);
                task.execute(loginReq);
            }
        });

        //On click listener for Register button
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create RegisterRequest with info from text views
                //Use to run RegisterTask Async
                String registerUsername = usernameEditText.getText().toString();
                String registerPassword = passwordEditText.getText().toString();
                String registerEmail = emailEditText.getText().toString();
                String registerFirstName = firstNameEditText.getText().toString();
                String registerLastName = lastNameEditText.getText().toString();
                String registerGender = null;

                //Check radio button is selected - return without registering if neither is selected
                if (maleRadioButton.isChecked()) {
                    registerGender = "m";
                }
                else if (femaleRadioButton.isChecked()) {
                    registerGender = "f";
                }
                else {
                    Toast.makeText(getActivity(), "Please check either male or female for gender",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                RegisterRequest regReq = new RegisterRequest(registerUsername,registerPassword,
                        registerEmail,registerFirstName,registerLastName,registerGender);

                RegisterTask task = new RegisterTask(LoginFragment.this);

                task.execute(regReq);

            }
        });

        return v;
    }

    //Logic to check if login/register buttons should be enabled. Called by text watcher
    private void checkTextFieldsToEnableButtons() {

        //Get Strings for each edit text view in its current state
        String hostField = hostEditText.getText().toString();
        String portField = portEditText.getText().toString();
        String usernameField = usernameEditText.getText().toString();
        String passwordField = passwordEditText.getText().toString();
        String firstNameField = firstNameEditText.getText().toString();
        String lastNameField = lastNameEditText.getText().toString();
        String emailField = emailEditText.getText().toString();
        boolean radioClicked;

        radioClicked = maleRadioButton.isChecked() || femaleRadioButton.isChecked();

        //Enable login button if host, port, username, and password fields have text
        if (hostField.length() > 0 && portField.length() > 0 &&
                usernameField.length() > 0 && passwordField.length() > 0) {
            signInButton.setEnabled(true);

            //Enable or disable register button based on first name, last name, email
            //and radio buttons
            if (firstNameField.length() > 0 && lastNameField.length() > 0 &&
            emailField.length() > 0 && radioClicked) {
                registerButton.setEnabled(true);
            }
            else {
                registerButton.setEnabled(false);
            }
        }
        else {
            signInButton.setEnabled(false);
            registerButton.setEnabled(false);
        }
    }

    //RegisterTask listener function override to indicate Registration Task has finished
    @Override
    public void onRegisterFinished(RegisterResult regResult) {
        String text;

        //Check boolean value of RegisterResult returned by RegisterTask
        if (regResult.isSuccess()) {
            userPersonID = regResult.getPersonID();

            //If true, get People and Events for new User - switch to MapFragment
            UpdateDataCacheTask task = new UpdateDataCacheTask(LoginFragment.this);
            String registeredAuthToken = regResult.getAuthToken();
            task.execute(registeredAuthToken);

        } else {
            //Display error message and end Registration task
            text = "Registration failed";
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }


    }

    //LoginTask listener function override to indicate end of LoginTask
    @Override
    public void onLoginFinished(LoginResult loginResult) {
        String text;

        //Check boolean value of LoginResult returned by LoginTask
        if (loginResult.isSuccess()) {

            //Run UpdateDataCache async task to get Events and People for logged in user
            //Transition to MapFragment
            userPersonID = loginResult.getPersonID();
            UpdateDataCacheTask task = new UpdateDataCacheTask(LoginFragment.this);
            String registeredAuthToken = loginResult.getAuthToken();
            task.execute(registeredAuthToken);


        } else {
            //Display toast with error message, end Login task
            text = "Login failed";
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }

    }

    //UpdateDataCacheTask listener function override indicated People and Events have been retreived
    @Override
    public void onDataUpdated(boolean updateSuccessful) {

        String text;

        //Check boolean updateSuccessful - True if both People and Event Lists
        //were successfully retrieved from server
        if (updateSuccessful) {
            DataCache updatedCache = DataCache.getInstance();

            //Set static variable for Person of logged in user for later reference
            Person usersPerson = updatedCache.getPeople().get(userPersonID);
            loggedInUser = usersPerson;

            //Display logged in toast for user
            String firstName = usersPerson.getFirstName();
            String lastName = usersPerson.getLastName();

            text = "Welcome " + firstName + " " + lastName;

            //Call listener to send callback information to MainActivity
            listener.onSuccessfulLogin(true);
        }
        else {
            //Display toast indicated data was not successfully retrieved
            text = "Update failed";
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}