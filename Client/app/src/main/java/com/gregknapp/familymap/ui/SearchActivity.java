package com.gregknapp.familymap.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.model.Event;
import com.gregknapp.familymap.model.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    //Static int variables to control RecyclerView
    private static final int PERSON_SEARCH_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_SEARCH_ITEM_VIEW_TYPE = 1;

    private DataCache dataToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Up button enabled on this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up DataCache instance
        dataToSearch = DataCache.getInstance();

        //Set up pointers for UI elements
        SearchView searchView = (SearchView)findViewById(R.id.search_view_bar);
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Set Query Text Listener for SearchView to run search logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                //Get list of People and Events based on search criteria from DataCache
                List<Person> filteredPeople = dataToSearch.runPeopleSearch(query.toLowerCase());
                List<Event> filteredEvents = dataToSearch.runEventSearch(query.toLowerCase());

                //Set Adapter for RecyclerView using Person and Event lists
                SearchAdapter adapter = new SearchAdapter(filteredPeople, filteredEvents);
                recyclerView.setAdapter(adapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private class SearchAdapter extends RecyclerView.Adapter<SearchActivityViewHolder> {

        //Final variables with list of search result Events and People
        private final List<Person> peopleToList;
        private final List<Event> eventsToList;

        SearchAdapter(List<Person> peopleToList, List<Event> eventsToList) {
            this.peopleToList = peopleToList;
            this.eventsToList = eventsToList;
        }

        @Override
        public int getItemViewType(int position) {
            //Check if current item is a Person or Event. People are displayed first, then events
            return position < peopleToList.size() ? PERSON_SEARCH_ITEM_VIEW_TYPE : EVENT_SEARCH_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            //Create view for Person List Item if current item is a Person
            if (viewType == PERSON_SEARCH_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_list_item, parent, false);
            }
            else {
                //Create View for an Event List Item if current Item is an Event
                view = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
            }

            return new SearchActivityViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchActivityViewHolder holder, int position) {
            //Get correct event or person and bind to current view holder based on position
            if (position < peopleToList.size()) {
                holder.bind(peopleToList.get(position));
            }
            else {
                holder.bind(eventsToList.get(position - peopleToList.size()));
            }
        }

        @Override
        public int getItemCount() {
            return peopleToList.size() + eventsToList.size();
        }
    }

    //Private inner class to set the UI elements for either a PersonItemView or an EventItemView called by
    //The adapter class above.
    private class SearchActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Variables for UI element pointers
        private final ImageView iconView;
        private final TextView name;
        private final TextView eventInformation;

        //Variables to determine view type and what data should be added
        private final int viewType;
        private Person foundPerson;
        private Event foundEvent;

        SearchActivityViewHolder(View view, int viewType) {
            super(view);

            this.viewType = viewType;
            itemView.setOnClickListener(this);

            //Check view type passed in by adapter and set correct pointers based on result
            if (viewType == PERSON_SEARCH_ITEM_VIEW_TYPE) {
                //Person item found, set views for Person item
                name = itemView.findViewById(R.id.personName);
                iconView = itemView.findViewById(R.id.gender_icon_image);
                eventInformation = null;
            }
            else {
                //Event item found, set views for Event item
                name = itemView.findViewById(R.id.eventDetails);
                eventInformation = itemView.findViewById(R.id.eventPerson);
                iconView = itemView.findViewById(R.id.map_icon_image);
            }
        }

        //Overloaded bind function for when a Person object is found
        private void bind(Person person) {
            this.foundPerson = person;

            //Set text for Person name
            String nameInfo = foundPerson.getFirstName() + " " + foundPerson.getLastName();
            name.setText(nameInfo);

            //Set correct gender icon for person
            if (foundPerson.getGender().toLowerCase().equals("m")) {
                Drawable genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(40);
                iconView.setImageDrawable(genderIcon);
            }
            else {
                Drawable genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(40);
                iconView.setImageDrawable(genderIcon);
            }
        }

        //Overloaded bind function for use when an Event object is found
        private void bind(Event event) {
            this.foundEvent = event;

            //Get event details and associated person details, set text views
            String eventInfo = foundEvent.getEventType().toUpperCase() + ": " + foundEvent.getCity() + ", "
                    + foundEvent.getCountry() + " (" + foundEvent.getYear() + ")";
            name.setText(eventInfo);
            Person eventPerson = dataToSearch.getPersonFromID(foundEvent.getPersonID());
            String personName = eventPerson.getFirstName() + " " + eventPerson.getLastName();

            eventInformation.setText(personName);
            String eventType = foundEvent.getEventType().toLowerCase();

            //Create marker icon with correct color from DataCache color map
            if (MapFragment.eventIconColors.containsKey(eventType)) {
                int colorID = MapFragment.eventIconColors.get(eventType);
                Drawable mapIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                        colorRes(colorID).sizeDp(40);
                iconView.setImageDrawable(mapIcon);
            }

        }

        //Set on-click listener for all views, check view type to start correct Activity
        @Override
        public void onClick(View v) {
            if (viewType == PERSON_SEARCH_ITEM_VIEW_TYPE) {
                //Open up a new PersonActivity because Person item was clicked. Pass in person details as argument
                Intent personActivity = new Intent(getApplicationContext(), PersonActivity.class);
                personActivity.putExtra(PersonActivity.PERSON_TO_DISPLAY_ID, foundPerson.getPersonID());
                startActivity(personActivity);
            }
            else {
                //Open a new EventActivity because Event item was clicked. Pass in Event JSON as argument
                Gson gson = new Gson();
                String eventJSON = gson.toJson(foundEvent);

                Intent eventActivity = new Intent(getApplicationContext(), EventActivity.class);
                eventActivity.putExtra(EventActivity.EVENT_TO_DISPLAY_ID, eventJSON);
                startActivity(eventActivity);
            }

        }
    }
}