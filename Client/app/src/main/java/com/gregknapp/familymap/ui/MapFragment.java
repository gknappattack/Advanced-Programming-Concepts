package com.gregknapp.familymap.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.gregknapp.familymap.R;
import com.gregknapp.familymap.model.DataCache;
import com.gregknapp.familymap.model.Event;
import com.gregknapp.familymap.model.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    //Main Map data storing variables used in MapFragment
    GoogleMap map;
    private DataCache mapData;
    private Event selectedEvent;
    private List<Polyline> currentLines;

    //Pointers for UI elements in MapFragment
    private TextView eventText;
    private ImageView androidGenderIcon;
    private RelativeLayout eventInformationLayout;

    //Variables to manage colors of various event types and markers
    private Map<String, Float> eventsToColors;
    public static Map<String, Integer> eventIconColors;
    private float[] colorsArray;
    private int[] colorArrayForActivities;
    private int colorIterator;
    int colorArraySize = 10;

    //Booleans to track lines and filter settings from SettingsActivity
    private boolean lifeStoryLinesOn;
    private boolean familyTreeLinesOn;
    private boolean spouseLinesOn;
    private boolean fatherSideFilter;
    private boolean motherSideFilter;
    private boolean maleEventsFilter;
    private boolean femaleEventsFilter;

    //Boolean checking if MapFragment was created by MainActivity or EventActivity
    private boolean createdByEventActivity;

    //Static String Key to get Event data when created by EventActivity
    public static final String EVENT_TO_DISPLAY =
            "com.gregknapp.familymap.ui.mapfragment.eventinfo";

    //Static Request Code to get Results from SettingsActivity to set filter booleans
    public static int SETTTINGS_REQUEST_CODE;

    //On Marker clicker for Event Markers on map
    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

            //Get event from tag of marker - Set camera on selected event
            selectedEvent = (Event)marker.getTag();
            LatLng eventCoordinates = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(eventCoordinates), 1000, null);

            //Set event information text view on MapFragment with data from selected event
            if (selectedEvent != null) {
                Person associatedPerson = mapData.getPersonFromID(selectedEvent.getPersonID());
                String eventInfo = associatedPerson.getFirstName() + " " + associatedPerson.getLastName() + "\n"
                        + selectedEvent.getEventType().toUpperCase() + ": " + selectedEvent.getCity() + " "
                        + ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear() + ")";

                eventText.setText(eventInfo);

                //Insert proper male or female icon based on event data
                if (associatedPerson.getGender().toLowerCase().equals("m")) {
                    Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(40);
                    androidGenderIcon.setImageDrawable(maleIcon);
                }
                else {
                    Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(40);
                    androidGenderIcon.setImageDrawable(femaleIcon);
                }
            }

            //Call map line drawing helper function after information text is updated
            drawMapLines();

            return true;
        }
    };

    //Map line drawing helper function
    private void drawMapLines() {

        //Clear lines stored from previous selected event if any
        if (currentLines.size() != 0) {
            Iterator<Polyline> polylineIterator = currentLines.iterator();

            while (polylineIterator.hasNext()) {
                Polyline currentLine = polylineIterator.next();
                currentLine.remove();
            }
        }

        //Logic to draw life story lines if option is selected
        if (lifeStoryLinesOn) {

            //Get all events in chronological order for person of the selected event
            List<Event> lifeStoryEvents = mapData.getEventsForPerson(selectedEvent.getPersonID());

            //Check if there are any events to draw lines for
            if (lifeStoryEvents.size() > 0) {

                Iterator<Event> eventIterator = lifeStoryEvents.iterator();
                Event firstPoint = eventIterator.next();
                Event secondPoint = null;

                //Cycle through events, draw lines between each event, push lines onto PolyLine list
                while (eventIterator.hasNext()) {
                    //Get new event
                    secondPoint = eventIterator.next();

                    if (secondPoint != null) {

                        //Set coordinates for two events, draw PolyLine and add to List
                        LatLng firstCoordinates = new LatLng(firstPoint.getLatitude(), firstPoint.getLongitude());
                        LatLng secondCoordinates = new LatLng(secondPoint.getLatitude(), secondPoint.getLongitude());

                        Polyline eventLine = map.addPolyline(new PolylineOptions()
                                .add(firstCoordinates, secondCoordinates)
                                .width(16)
                                .color(Color.BLUE));

                        currentLines.add(eventLine);

                        //Update previous event for next line
                        firstPoint = secondPoint;
                    }
                }
            }
        }

        //Make call to drawFamilyTreeLines recursive helper function if boolean is true
        if (familyTreeLinesOn) {
            if (selectedEvent != null) {

                //Set generation counter to control line thickness in helper function
                float generationCounter = 0;

                //Get person for selected event and call helper function
                Person associatedPerson = mapData.getPersonFromID(selectedEvent.getPersonID());
                drawFamilyTreeLines(associatedPerson, selectedEvent, generationCounter);
            }
        }

        //Logic to draw spouse lines if boolean for that option is true
        if (spouseLinesOn) {

            if (selectedEvent != null) {

                //Get Person data for Person associated with the selected event
                Person eventPerson = mapData.getPersonFromID(selectedEvent.getPersonID());

                //Check if spouseID is null, if it is, don't run line drawing logic
                if (eventPerson.getSpouseID() != null) {
                    List<Event> spouseEvents = mapData.getEventsForPerson(eventPerson.getSpouseID());

                    //Get events for spouse to find earliest one.
                    //If no events are found, don't draw lines
                    if (spouseEvents.size() > 0) {
                        Iterator<Event> spouseEventIterator = spouseEvents.iterator();

                        //Get earliest event from chronological list, create Polyline, and add to List
                        Event spouseFirstEvent = spouseEventIterator.next();
                        LatLng selectedEventCoordinates = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());

                        if (spouseFirstEvent != null) {
                            LatLng spouseEventCoordinates = new LatLng(spouseFirstEvent.getLatitude(), spouseFirstEvent.getLongitude());

                            Polyline finalEvent = map.addPolyline(new PolylineOptions()
                                    .add(selectedEventCoordinates, spouseEventCoordinates)
                                    .width(16)
                                    .color(Color.RED));

                            currentLines.add(finalEvent);
                        }
                    }
                }
            }
        }
    }

    //Set up map markers and colors once map is ready to use
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            //Get DataCache instance and get all event informaiton
            mapData = DataCache.getInstance();
            List<Event> eventsList = mapData.getEventList();

            //Get color information if it has already been set previously
            eventsToColors = mapData.getEventsToColors();
            eventIconColors = DataCache.eventIconColors;

            //Iterate through all events and set markers
            Iterator listIterator = eventsList.iterator();
             while (listIterator.hasNext()) {
                 Event currentEvent = (Event)listIterator.next();

                 LatLng eventCoordinates = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());

                 String eventType = currentEvent.getEventType().toLowerCase();
                 Float eventColor;

                 //Check if a color for the current event's EventType has been set
                 if (eventsToColors.containsKey(eventType)) {
                     //Event Type already known, use color from map
                     eventColor = eventsToColors.get(eventType);
                 }
                 else {
                     //Color has not been set, set next color from color array
                     eventColor = colorsArray[colorIterator];
                     eventsToColors.put(eventType, eventColor);

                     int colorToAdd = colorArrayForActivities[colorIterator];
                     eventIconColors.put(eventType, colorToAdd);

                     //Increase color array iterator so next event will be a different color
                     colorIterator++;

                     //Reset color iterator value if all colors have been used
                     if (colorIterator == 10) {
                         colorIterator = 0;
                     }
                 }

                 //Add marker to map using selected coolor from above
                 map.addMarker(new MarkerOptions().position(eventCoordinates)
                 .icon(BitmapDescriptorFactory.defaultMarker(eventColor))).setTag(currentEvent);

             }

             //Update color maps in DataCache for use later
             mapData.setEventsToColorsFloat(eventsToColors);
             mapData.setEventsToColorsInt(eventIconColors);


            //Center event on marker for event passed in if MapFragment was created by
            //Event activity and JSON data has been passed in
            if (selectedEvent != null) {

                //Get coordinates for event passed in from EventActivity and center map
                LatLng eventCoordinates = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLng(eventCoordinates), 1000, null);

                //Set event information for selected event on event information text view
                Person associatedPerson = mapData.getPersonFromID(selectedEvent.getPersonID());
                String eventInfo = associatedPerson.getFirstName() + " " + associatedPerson.getLastName() + "\n"
                        + selectedEvent.getEventType().toUpperCase() + ": " + selectedEvent.getCity() + " "
                        + ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear() + ")";
                eventText.setText(eventInfo);

                //Set correct gender icon based on selected event's information
                if (associatedPerson.getGender().toLowerCase().equals("m")) {
                    Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(40);
                    androidGenderIcon.setImageDrawable(maleIcon);
                } else {
                    Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(40);
                    androidGenderIcon.setImageDrawable(femaleIcon);
                }

                //Run map line drawing helped function
                drawMapLines();
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Inflate view and set up Iconify for gender/android icons
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        Iconify.with(new FontAwesomeModule());

        //Set up DataCache instance and get pointers to UI elements
        mapData = DataCache.getInstance();
        androidGenderIcon = (ImageView) v.findViewById(R.id.androidIcon);
        eventText = (TextView) v.findViewById(R.id.map_fragment_text);

        //Create Options Menu
        setHasOptionsMenu(true);

        //Initialize ArrayList for Polylines
        currentLines = new ArrayList<>();


        //Set up color arrays and maps for map icons
        eventsToColors = new HashMap<>();
        eventIconColors = new HashMap<>();
        colorsArray = new float[colorArraySize];
        colorArrayForActivities = new int[colorArraySize];
        colorIterator = 0;

        colorsArray[0] = BitmapDescriptorFactory.HUE_RED;
        colorArrayForActivities[0] = R.color.event_type_1;
        colorsArray[1] = BitmapDescriptorFactory.HUE_BLUE;
        colorArrayForActivities[1] = R.color.event_type_2;
        colorsArray[2] = BitmapDescriptorFactory.HUE_GREEN;
        colorArrayForActivities[2] = R.color.event_type_3;
        colorsArray[3] = BitmapDescriptorFactory.HUE_MAGENTA;
        colorArrayForActivities[3] = R.color.event_type_4;
        colorsArray[4] = BitmapDescriptorFactory.HUE_YELLOW;
        colorArrayForActivities[4] = R.color.event_type_5;
        colorsArray[5] = BitmapDescriptorFactory.HUE_CYAN;
        colorArrayForActivities[5] = R.color.event_type_6;
        colorsArray[6] = BitmapDescriptorFactory.HUE_ROSE;
        colorArrayForActivities[6] = R.color.event_type_7;
        colorsArray[7] = BitmapDescriptorFactory.HUE_VIOLET;
        colorArrayForActivities[7] = R.color.event_type_8;
        colorsArray[8] = BitmapDescriptorFactory.HUE_AZURE;
        colorArrayForActivities[8] = R.color.event_type_9;
        colorsArray[9] = BitmapDescriptorFactory.HUE_ORANGE;
        colorArrayForActivities[9] = R.color.event_type_10;

        //Set default values for settings boolean variables
        lifeStoryLinesOn = true;
        familyTreeLinesOn = true;
        spouseLinesOn = true;
        fatherSideFilter = true;
        motherSideFilter = true;
        maleEventsFilter = true;
        femaleEventsFilter = true;

        //Check if created by event activity, if true, set selected event from JSON data
        createdByEventActivity = false;

        Bundle args = getArguments();

        String eventJSON = args.getString(EVENT_TO_DISPLAY);
        System.out.println(eventJSON);
        Gson gson = new Gson();
        selectedEvent = gson.fromJson(eventJSON, Event.class);

        if (selectedEvent != null) {
            createdByEventActivity = true;
        }

        //Set up android icon for a new MapFragment
        Drawable androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.android).sizeDp(30);
        androidGenderIcon.setImageDrawable(androidIcon);

        //Set on click listener for Relative layout containing event
        //information to start a new PersonActivity
        eventInformationLayout = (RelativeLayout)v.findViewById(R.id.event_information_layout);
        eventInformationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEvent != null ) {

                    //Start Person Activity with data from currently selected event if event info is clicked
                    Intent personActivity = new Intent(getActivity(), PersonActivity.class);
                    personActivity.putExtra(PersonActivity.PERSON_TO_DISPLAY_ID, selectedEvent.getPersonID());
                    startActivity(personActivity);
                }
            }
        });

        //Set on-click response for Google Map fragment
        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(
                (googleMap -> {
                    map = googleMap;
                    map.setOnMarkerClickListener(markerClickListener);
                })
        );
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //Create option menu, menu appears only if MapFragment is used by MainActivity, not EventActivity
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //Check for boolean indicating which Activity is using MapFragment
        if (!createdByEventActivity) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.map_fragment_menu, menu);

            //Create Search Icon for menu bar
            MenuItem searchOption = (MenuItem) menu.findItem(R.id.search_option);
            Drawable searchIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).
                    colorRes(R.color.white).sizeDp(20);
            searchOption.setIcon(searchIcon);

            //Create Settings Icon for menu car
            MenuItem settingOption = (MenuItem) menu.findItem(R.id.setting_option);
            Drawable settingIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).
                    colorRes(R.color.white).sizeDp(20);
            settingOption.setIcon(settingIcon);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search_option:

                //Search option has been selected - Start a new SearchActivity with no arguments
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.setting_option:

                //Settings option has been selected - Start new SettingsActivity. Pass in all booleans
                //For filter/line values as arguments in intent
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                settingsIntent.putExtra(SettingsActivity.LIFE_STORY_LINES_ON, lifeStoryLinesOn);
                settingsIntent.putExtra(SettingsActivity.FAMILY_TREE_LINES_ON, familyTreeLinesOn);
                settingsIntent.putExtra(SettingsActivity.SPOUSE_LINES_ON, spouseLinesOn);
                settingsIntent.putExtra(SettingsActivity.FATHER_SIDE_FILTER, fatherSideFilter);
                settingsIntent.putExtra(SettingsActivity.MOTHER_SIDE_FILTER, motherSideFilter);
                settingsIntent.putExtra(SettingsActivity.MALE_EVENTS_FILTER, maleEventsFilter);
                settingsIntent.putExtra(SettingsActivity.FEMALE_EVENTS_FILTER, femaleEventsFilter);

                //Run Settings for result to update filter boolean values
                startActivityForResult(settingsIntent, SETTTINGS_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Do nothing if user does not do anything on Settings Activity
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        //Update all booleans for filters/lines based on Settings results
        if (requestCode == SETTTINGS_REQUEST_CODE) {
            lifeStoryLinesOn = data.getBooleanExtra(SettingsActivity.LIFE_STORY_LINES_ON, false);
            familyTreeLinesOn = data.getBooleanExtra(SettingsActivity.FAMILY_TREE_LINES_ON, false);
            spouseLinesOn = data.getBooleanExtra(SettingsActivity.SPOUSE_LINES_ON, false);
            fatherSideFilter = data.getBooleanExtra(SettingsActivity.FATHER_SIDE_FILTER, true);
            motherSideFilter = data.getBooleanExtra(SettingsActivity.MOTHER_SIDE_FILTER, true);
            maleEventsFilter = data.getBooleanExtra(SettingsActivity.MALE_EVENTS_FILTER, true);
            femaleEventsFilter = data.getBooleanExtra(SettingsActivity.FEMALE_EVENTS_FILTER, true);

            //Run filterMap to redraw map markers based on filter settings
            filterMap();

        }

    }

    //Family Tree Lines recursive helper function
    private void drawFamilyTreeLines(Person currentPerson, Event eventToConnect, float currentGeneration) {

        //Iterate generation counter on each recursive call to reduce line thickness gradually as generation incrases.
        currentGeneration++;

        //Check for the end of the family tree line
        if (currentPerson != null) {

            //Get father - Get father's events. Draw line to earliest event. Check for null event/no father
            Person father = mapData.getPersonFromID(currentPerson.getFatherID());

            if (father != null) {
                List<Event> fatherEvents = mapData.getEventsForPerson(father.getPersonID());

                if (fatherEvents.size() > 0) {

                    //Draw event between current persons event passed in from previous recursive call and
                    //father's first chronological event.
                    Iterator<Event> fatherEventIterator = fatherEvents.iterator();
                    Event fatherFirstEvent = fatherEventIterator.next();

                    LatLng firstPoint = new LatLng(eventToConnect.getLatitude(), eventToConnect.getLongitude());
                    LatLng secondPoint;
                    if (fatherFirstEvent != null) {
                        secondPoint = new LatLng(fatherFirstEvent.getLatitude(), fatherFirstEvent.getLongitude());
                        Polyline fatherSonEvent = map.addPolyline(new PolylineOptions()
                                .add(firstPoint, secondPoint)
                                .width(16 / currentGeneration)
                                .color(Color.GREEN));

                        currentLines.add(fatherSonEvent);


                        //Make recursive call for father branch
                        drawFamilyTreeLines(father, fatherFirstEvent, currentGeneration);
                    }
                }
                else {
                    //No events for father, no lines are drawn
                }
            }
            else {
                //No father found, no lines drawn.
            }

            //Get mother - Get mother's events. Draw line to earliest event
            Person mother = mapData.getPersonFromID(currentPerson.getMotherID());

            if (mother != null) {
                List<Event> motherEvents = mapData.getEventsForPerson(mother.getPersonID());

                if (motherEvents.size() > 0) {

                    //Draw event between current persons event passed in from previous recursive call and
                    //mother's first chronological event.
                    Iterator<Event> motherEventIterator = motherEvents.iterator();
                    Event motherFirstEvent = motherEventIterator.next();

                    LatLng firstPoint = new LatLng(eventToConnect.getLatitude(), eventToConnect.getLongitude());

                    LatLng secondPoint;
                    if (motherFirstEvent != null) {
                        secondPoint = new LatLng(motherFirstEvent.getLatitude(), motherFirstEvent.getLongitude());

                        Polyline motherSonEvent = map.addPolyline(new PolylineOptions()
                                .add(firstPoint, secondPoint)
                                .width(16 / currentGeneration)
                                .color(Color.GREEN));
    

                        currentLines.add(motherSonEvent);

                        //Make recursive call for mother branch
                        drawFamilyTreeLines(mother, motherFirstEvent, currentGeneration);
                    }
                }
                else {
                    //No events for mother, no line drawn
                }
            }
            else {
                //No mother found, no lines drawn.
            }

        }
        else {

            //Base case = person searched for is null, reached end of this branch of family tree
            return;
        }
    }

    //Helper function to get filtered event data in order to redraw map correctly
    private void filterMap() {

        //Get user's Person data from Static variable in LoginFragment
        Person loggedInUser = LoginFragment.loggedInUser;

        //Update the filtered event list stored in DataCache with filter booleans from SettingsActivity
        mapData.runEventsFilter(motherSideFilter, fatherSideFilter, maleEventsFilter, femaleEventsFilter, loggedInUser);

        //Check if no filters are turned on to avoid using empty filtered event list
        boolean reset = false;
        if (maleEventsFilter && femaleEventsFilter && motherSideFilter && fatherSideFilter) {
            reset = true;
        }

        //Call helper function to add map markers and lines
        drawFilteredMap(reset);

    }

    private void drawFilteredMap(boolean resetMap) {

        //Clear current map markers and lines to re-draw
        map.clear();

        //Get filtered event list, replace with full event list if all filters are off (resetMap == true)
        List<Event> filteredEvents = mapData.getFilteredEventList();

        if (resetMap) { //All filters are off
            filteredEvents = mapData.getEventList();
        }

        //Iterate through event list, create markers by referencing color maps stored in DataCache
        Iterator listIterator = filteredEvents.iterator();
        while (listIterator.hasNext()) {
            Event currentEvent = (Event)listIterator.next();
            String eventType = currentEvent.getEventType().toLowerCase();
            float eventColor;

            //Get correct color from eventMap or set new color
            if (eventsToColors.containsKey(eventType)) {
                eventColor = eventsToColors.get(eventType);

            }
            else {
                eventColor = colorsArray[colorIterator];
            }

            //Get coordinate and set marker on map
            LatLng eventCoordinates = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
            map.addMarker(new MarkerOptions().position(eventCoordinates)
            .icon(BitmapDescriptorFactory.defaultMarker(eventColor))).setTag(currentEvent);

        }

        //If previously selected even before entering Settings has been filtered out of event list,
        //Reset event info text view to default view with android icon
        if (!filteredEvents.contains(selectedEvent)) {
            selectedEvent = null;
            Drawable androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                    colorRes(R.color.android).sizeDp(30);
            androidGenderIcon.setImageDrawable(androidIcon);
            eventText.setText(R.string.map_screen_prompt);
        }
    }
}