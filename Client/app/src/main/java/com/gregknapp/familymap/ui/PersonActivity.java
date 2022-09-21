package com.gregknapp.familymap.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.model.Event;
import com.gregknapp.familymap.model.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.List;

public class PersonActivity extends AppCompatActivity {

    //Static variable to keep track of who is the person the event represents, set from bundle
    private Person currentPerson;
    private DataCache mapData;

    //Static String key variable to retrive Person information from MapFragment
    public static final String PERSON_TO_DISPLAY_ID =
            "com.gregknapp.familymap.ui.mapfragment.personid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate layout and set up Iconify
        setContentView(R.layout.activity_person);
        Iconify.with(new FontAwesomeModule());

        //Up button enabled for this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get DataCache instance
        mapData = DataCache.getInstance();

        //Retrieve Person data passed in as argument from MapFragment
        String personID = getIntent().getStringExtra(PERSON_TO_DISPLAY_ID);
        currentPerson = mapData.getPersonFromID(personID);


        //Set text views for first name, last name, and gender from Person information
        TextView personFirstName = (TextView) findViewById(R.id.current_person_first_name);
        personFirstName.setText(currentPerson.getFirstName());
        TextView personLastName = (TextView) findViewById(R.id.current_person_last_name);
        personLastName.setText(currentPerson.getLastName());
        TextView personGender = (TextView) findViewById(R.id.current_person_gender);

        if (currentPerson.getGender().toLowerCase().equals("m")) {
            personGender.setText("Male");
        } else {
            personGender.setText("Female");
        }

        //Get pointer for expandable list view
        ExpandableListView expandableListView = findViewById(R.id.expandable_view);

        //Get family and person's event lists from DataCache, pass into expandable list view adapter inner-class
        List<Person> personList = mapData.getImmediateRelatives(currentPerson);
        List<Event> eventList = mapData.getEventsForPerson(personID);

        expandableListView.setAdapter(new ExpandableListAdapter(personList, eventList, currentPerson));

    }

    //Private inner class to handle Expandable list view behavior for Family Member List and Events List
    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        //Static ints for Positions of both lists to display
        private static final int EVENT_LIST_POSITION = 0;
        private static final int PERSON_LIST_POSITION = 1;

        //Final variables containing reference information from main class
        private final List<Person> relativeList;
        private final List<Event> eventList;
        private final Person pagePerson;

        private ExpandableListAdapter(List<Person> relativeList, List<Event> eventList, Person pagePerson) {
            this.relativeList = relativeList;
            this.eventList = eventList;
            this.pagePerson = pagePerson;
        }


        //Assorted Overridden functions to maintain normal Expandable list behavior
        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_LIST_POSITION:
                    return relativeList.size();
                case EVENT_LIST_POSITION:
                    return eventList.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case PERSON_LIST_POSITION:
                    return getString(R.string.personListTitle);
                case EVENT_LIST_POSITION:
                    return getString(R.string.eventListTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case PERSON_LIST_POSITION:
                    return relativeList.get(childPosition);
                case EVENT_LIST_POSITION:
                    return eventList.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            //Set up view for List title, check group position to set proper title
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }
            ;
            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_LIST_POSITION:

                    //Set title for Family list if view for Family list is found
                    titleView.setText(R.string.personListTitle);
                    break;
                case EVENT_LIST_POSITION:

                    //Set title for Life Events list if view for Life Events is found
                    titleView.setText(R.string.eventListTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);

            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case EVENT_LIST_POSITION:
                    //If current item found by list is an Event List item, create new EventItemView
                    itemView = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;

                case PERSON_LIST_POSITION:
                    //If current item found by list is a Person List item, create a new PersonItemView
                    itemView = getLayoutInflater().inflate(R.layout.person_list_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;

                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return itemView;
        }

        //Set up text and gender icon for a PersonItemView
        private void initializePersonView(View personItemView, final int childPosition) {

            //Get Person for current position in list
            Person displayPerson = relativeList.get(childPosition);

            //Set Gender Icon based on gender of person
            ImageView genderIconView = (ImageView) personItemView.findViewById(R.id.gender_icon_image);

            if (displayPerson.getGender().toLowerCase().equals("m")) {
                Drawable genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(40);
                genderIconView.setImageDrawable(genderIcon);
            }
            else {
                Drawable genderIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(40);
                genderIconView.setImageDrawable(genderIcon);
            }


            //Set Person Name text view
            TextView personName = personItemView.findViewById(R.id.personName);
            String nameOfDisplayPerson = displayPerson.getFirstName() + " " +  displayPerson.getLastName();
            personName.setText(nameOfDisplayPerson);


            //Set relationship to person based on id relationships
            TextView personRelationship = personItemView.findViewById(R.id.personRelationship);

            boolean relationshipSet = false;

            if (pagePerson.getFatherID() != null) {
                if (pagePerson.getFatherID().equals(displayPerson.getPersonID())) {
                    personRelationship.setText(R.string.father_string);
                    relationshipSet = true;
                }
            }

            if (pagePerson.getMotherID() != null) {
                if (pagePerson.getMotherID().equals(displayPerson.getPersonID()) && !relationshipSet) {
                    personRelationship.setText(R.string.mother_string);
                    relationshipSet = true;
                }
            }

            if (pagePerson.getSpouseID() != null) {
                if (pagePerson.getSpouseID().equals(displayPerson.getPersonID()) && !relationshipSet) {
                    personRelationship.setText(R.string.spouse_string);
                    relationshipSet = true;
                }
            }

            if (!relationshipSet) {
                personRelationship.setText(R.string.child_string);
            }

            //Set on-click listener to start new Person activity if PersonItemView is clicked
            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent personActivity = new Intent(getApplicationContext(), PersonActivity.class);
                    personActivity.putExtra(PersonActivity.PERSON_TO_DISPLAY_ID, displayPerson.getPersonID());
                    startActivity(personActivity);
                }
            });
        }

        //Set up text views and icon for an EventItemView
        private void initializeEventView(View eventItemView, final int childPosition) {

            //Get event for current event in list at position
            Event currentEvent = eventList.get(childPosition);

            //Null-check for event found
            if (currentEvent != null) {

                String eventType = currentEvent.getEventType().toLowerCase();
                ImageView mapIconView = (ImageView) eventItemView.findViewById(R.id.map_icon_image);
                Drawable mapIcon;

                //Get color for event type based on DataCache color maps - set marker icon for view
                if (MapFragment.eventIconColors.containsKey(eventType)) {
                    int colorID = MapFragment.eventIconColors.get(eventType);

                    mapIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                            colorRes(colorID).sizeDp(40);
                    mapIconView.setImageDrawable(mapIcon);
                }

                //Assemble event details and person info strings and set text views with them
                String eventDetails = currentEvent.getEventType() + ": " + currentEvent.getCity() +
                        ", " + currentEvent.getCountry() + " (" + currentEvent.getYear() + ")";

                String eventPerson = currentPerson.getFirstName() + " " + currentPerson.getLastName();

                TextView eventDetailsView = eventItemView.findViewById(R.id.eventDetails);
                eventDetailsView.setText(eventDetails);
                TextView eventPersonView = eventItemView.findViewById(R.id.eventPerson);
                eventPersonView.setText(eventPerson);

                //Set on-click listener to start new EventActivity if EventItemView is clicked
                eventItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toEventActivity = new Intent(getApplicationContext(), EventActivity.class);

                        //Serialized Event data to JSON and push to EventActivity as an argument
                        Gson gson = new Gson();
                        String eventJSON = gson.toJson(currentEvent);

                        toEventActivity.putExtra(EventActivity.EVENT_TO_DISPLAY_ID, eventJSON);
                        startActivity(toEventActivity);
                    }
                });
            }
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}