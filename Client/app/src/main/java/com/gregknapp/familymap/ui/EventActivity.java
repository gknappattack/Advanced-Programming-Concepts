package com.gregknapp.familymap.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.Event;

public class EventActivity extends AppCompatActivity {

    //Map Fragment and Fragment manager to get fragment to display
    private MapFragment mapFragment;
    private FragmentManager fm;

    //Static variable acting as key for selected event JSON data
    public static final String EVENT_TO_DISPLAY_ID =
            "com.gregknapp.familymap.ui.mapfragment.eventid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get JSON string for event selected by user passed in through Intent
        String eventJSON = getIntent().getStringExtra(EVENT_TO_DISPLAY_ID);

        fm = this.getSupportFragmentManager();

        //Create map fragment, JSON data inserted as part of bundle arguments
        mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(MapFragment.EVENT_TO_DISPLAY, eventJSON);
        mapFragment.setArguments(args);

        fm.beginTransaction()
                .replace(R.id.fragmentContainer,mapFragment)
                .commit();
    }
}