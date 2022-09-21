package com.gregknapp.familymap.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.DataCache;

public class SettingsActivity extends AppCompatActivity {

    //Booleans to get values from and return results to MapFragment
    private boolean lifeStoryLinesOn;
    private boolean familyTreeLinesOn;
    private boolean spouseLinesOn;
    private boolean fatherSideFiltered;
    private boolean motherSideFiltered;
    private boolean maleEventsFiltered;
    private boolean femaleEventsFiltered;

    //Static String keys for each boolean value being passed in
    public static final String LIFE_STORY_LINES_ON =
            "com.gregknapp.familymap.ui.settingactivity.lifestorylines";
    public static final String FAMILY_TREE_LINES_ON =
            "com.gregknapp.familymap.ui.settingactivity.familytreelines";
    public static final String SPOUSE_LINES_ON =
            "com.gregknapp.familymap.ui.settingactivity.spouselines";
    public static final String FATHER_SIDE_FILTER =
            "com.gregknapp.familymap.ui.settingactivity.fatherside";
    public static final String MOTHER_SIDE_FILTER =
            "com.gregknapp.familymap.ui.settingactivities.motherside";
    public static final String MALE_EVENTS_FILTER =
            "com.gregknapp.familymap.ui.settingsactivity.maleevents";
    public static final String FEMALE_EVENTS_FILTER =
            "com.gregknapp.familymap.ui.settingsactivity.femaleevents";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Enable up button on this event
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get intent and boolean values for each switch in Settings
        Intent switchValues = getIntent();
        lifeStoryLinesOn = switchValues.getBooleanExtra(LIFE_STORY_LINES_ON, false);
        familyTreeLinesOn = switchValues.getBooleanExtra(FAMILY_TREE_LINES_ON, false);
        spouseLinesOn = switchValues.getBooleanExtra(SPOUSE_LINES_ON, false);
        fatherSideFiltered = switchValues.getBooleanExtra(FATHER_SIDE_FILTER, true);
        motherSideFiltered = switchValues.getBooleanExtra(MOTHER_SIDE_FILTER, true);
        maleEventsFiltered = switchValues.getBooleanExtra(MALE_EVENTS_FILTER, true);
        femaleEventsFiltered = switchValues.getBooleanExtra(FEMALE_EVENTS_FILTER, true);

        //Set up each switch based on boolean values received. Whenever a switch is changed, update values
        //In results intent to be returned when the up/back button is hit

        //Set up Life Story Lines switch
        Switch lifeStorySwitch = (Switch) findViewById(R.id.showLifeStorySwitch);
        lifeStorySwitch.setChecked(lifeStoryLinesOn);
        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lifeStoryLinesOn = true;
                }
                else {
                    lifeStoryLinesOn = false;
                }
                setSettingsResults();
            }
        });

        //Set up Family Tree Lines Switch
        Switch familyTreeSwitch = (Switch) findViewById(R.id.showFamilyTreeLinesSwitch);
        familyTreeSwitch.setChecked(familyTreeLinesOn);
        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    familyTreeLinesOn = true;
                }
                else {
                    familyTreeLinesOn = false;
                }
                setSettingsResults();
            }
        });

        //Set up Spouse Lines Switch
        Switch spouseLinesSwitch = (Switch) findViewById(R.id.showSpouseLinesSwitch);
        spouseLinesSwitch.setChecked(spouseLinesOn);
        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spouseLinesOn = true;
                }
                else {
                    spouseLinesOn = false;
                }
                setSettingsResults();
            }
        });

        //Set up Father Side Filter Switch
        Switch fatherSideSwitch = (Switch) findViewById(R.id.filterFathersSideSwitch);
        fatherSideSwitch.setChecked(fatherSideFiltered);
        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fatherSideFiltered = true;
                }
                else {
                    fatherSideFiltered = false;
                }
                setSettingsResults();
            }
        });

        //Set up Mother Side Filter Switch
        Switch motherSideSwitch = (Switch) findViewById(R.id.filterMotherSideSwitch);
        motherSideSwitch.setChecked(motherSideFiltered);
        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    motherSideFiltered = true;
                }
                else {
                    motherSideFiltered = false;
                }
                setSettingsResults();
            }
        });

        //Set up Male Event Filter Switch
        Switch maleEventsSwitch = (Switch) findViewById(R.id.filterMaleEventSwitch);
        maleEventsSwitch.setChecked(maleEventsFiltered);
        maleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maleEventsFiltered = true;
                }
                else {
                    maleEventsFiltered = false;
                }
                setSettingsResults();
            }
        });

        //Set up Female Event Filter Switch
        Switch femaleEventsSwitch = (Switch) findViewById(R.id.filterFemaleEventsSwitch);
        femaleEventsSwitch.setChecked(femaleEventsFiltered);
        femaleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    femaleEventsFiltered = true;
                }
                else {
                    femaleEventsFiltered = false;
                }
                setSettingsResults();
            }
        });

        //Set up on-click for Linear Layout containing log-out information
        LinearLayout logoutLayout = (LinearLayout) findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset values in DataCache for log in of a new user
                DataCache.getInstance().resetDataCacheForNewUser();

                Intent data = new Intent(getApplicationContext(), MainActivity.class);

                //Set flags for new Main Activity to prevent using back button to log in again
                data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(data);
            }
        });
    }

    //Helper function to update Results Intent to be passed back to MapFragment when a switch changes.
    private void setSettingsResults() {
        Intent data = new Intent();
        data.putExtra(LIFE_STORY_LINES_ON, lifeStoryLinesOn);
        data.putExtra(FAMILY_TREE_LINES_ON, familyTreeLinesOn);
        data.putExtra(SPOUSE_LINES_ON, spouseLinesOn);
        data.putExtra(FATHER_SIDE_FILTER, fatherSideFiltered);
        data.putExtra(MOTHER_SIDE_FILTER, motherSideFiltered);
        data.putExtra(MALE_EVENTS_FILTER, maleEventsFiltered);
        data.putExtra(FEMALE_EVENTS_FILTER, femaleEventsFiltered);
        setResult(RESULT_OK, data);
    }
}