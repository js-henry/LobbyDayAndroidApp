/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 * Date: 25 Oct, 2015, modified 27 Jan, 2016
 *
 * This class represents the primary activity of the app and handles fragment interaction and
 * management. It also starts the http connection and JSON parsing using an inner class.
 *
 * Set up the Google Design Support Library before the NavigationView by adding the following
 * dependency to the app/build.gradle file:
 *  dependencies {
 *      compile 'com.android.support:design:23.1.0'
 *  }
 * The NavigationView is backwards compatible down to Android 2.1.
 *
 * Be sure to add INTERNET permission in the AndroidManifest.xml file:
 *      <uses-permission android:name="android.permission.INTERNET" />
 * Reinstate support for the Apache HTTP client by declaring the following compile-time
 * dependency in the build.gradle file:
 *      android {
 *          useLibrary 'org.apache.http.legacy'
 *      }
 */

package com.sasha_henry.lobbyday;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// AppCompatActivity extends android.support.v4.app.FragmentActivity and is the base class for
// activities using support library action bar features. On API level 7 or higher, classes extending
// AppCompatActivity can add an ActionBar by setting the activity theme to Theme.AppCompat or similar.
public class MainActivity extends AppCompatActivity
        implements FirstFragment.FirstFragmentListener,
        SecondFragment.SecondFragmentListener,
        ThirdFragment.ThirdFragmentListener,
        FourthFragment.FourthFragmentListener,
        FifthFragment.FifthFragmentListener {

    private Boolean isTestOutput = false;

    private static int DAY = 12;
    private static int MONTH = 2;
    private static int YEAR = 2015;
    private static final int startHour24 = 9;
    private static final int startMinute = 0;
    private static final int endHour24 = 16;
    private static final int endMinute = 0;
    private static final int apptDurationMinutes = 15; //appointment length in minutes
    private static final int numDistricts = 49; //number of districts
    private static final int numTeams = 8; //number of teams

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private ApptListAdapter apptListAdapter;
    private TextView mainHeaderTextview;
    private ImageView toolbarInfoIconImageview;

    private Boolean isUseTestData = false;
    private String trail = "";
    private ApptList apptList;
    private ApptList schedule;
    private Calendar startDate; //combined date and time
    private Calendar endDate; //combined date and time
    private SparseBooleanArray arrayOfIsChecked;
    private int activeFragment;

    private ProgressDialog apptPDialog;

    // URL to get contacts JSON
    private static String apptUrl = "http://centripetalsoftware.com/appts_json.txt";

    // JSON Node names for appointments
    private static final String TAG_APPOINTMENTS = "appointments";
    private static final String TAG_START_TIME = "startTime";
    private static final String TAG_ID = "district";
    private static final String TAG_DISTRICT = "district";
    private static final String TAG_CHAMBER = "chamber";
    private static final String TAG_REP__FIRST_NAME = "repFirstName";
    private static final String TAG_REP_LAST_NAME = "repLastName";
    private static final String TAG_TEAM = "team";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_IS_SCHEDULED = "isScheduled";

    // appointments JSONArray
    JSONArray appointments = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize list of appointments
        restoreList();
        arrayOfIsChecked = new SparseBooleanArray();

        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //use the ActionBarDrawerToggle class, which will animate the hamburger icon to indicate
        //the drawer is being opened and closed
        drawerToggle = setupDrawerToggle();

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // wire DrawerLayout events to the ActionBarToggle by making drawerToggle a listener
        // for mDrawer
        mDrawer.setDrawerListener(drawerToggle);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        mainHeaderTextview = (TextView)findViewById(R.id.main_header_textview);
        toolbarInfoIconImageview = (ImageView) findViewById(R.id.toolbar_info_icon_imageview);

        toolbarInfoIconImageview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showInfoToast();
                    } 
                } 
        ); //end set onClickListener

    } //end onCreate

    //ActionBarDrawerToggle renders a custom DrawerArrowDrawable for you for the hamburger icon.
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    //Change the onOptionsItemSelected() method and allow the ActionBarToggle to handle the events.
    //The ActionBarToggle will perform the same function done previously but adds a bit more checks
    // and allows mouse clicks on the icon to open and close the drawer.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Make sure to implement the correct onPostCreate(Bundle savedInstanceState) method.
    // The correct method has just `Bundle` as the signature.
    // There are 2 signatures and only this one shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred (screen is restored).
        drawerToggle.syncState();
    }

    //Synchronize the state whenever there is a configuration change (i.e screen rotation).
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Setup a listener to respond to click events on the navigation elements.
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //Setup a handler to swap out the fragment when navigation elements are clicked.
    public void selectDrawerItem(MenuItem menuItem) {
        // create and display a new fragment based on nav drawer selection
        Fragment fragment = null;
        String p1;
        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment: //plain (non-selectable) list with data gaps (schedule)
                mainHeaderTextview.setText(R.string.first_fragment_title);
                //no user interaction for now (future: swipe appt to remove from schedule)
                fragmentClass = FirstFragment.class;
                try {
                    fragment = FirstFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activeFragment = 1;
                break;
            case R.id.nav_second_fragment: //clickable grid (add appointments to schedule by district)
                mainHeaderTextview.setText(R.string.second_fragment_title);
                //on second frag close, send SparseBooleanArray back to main & sync with
                // ApptList.item.isScheduled
                fragmentClass = SecondFragment.class;
                try {
                    fragment = SecondFragment.newInstance(numDistricts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activeFragment = 2;
                break;
            case R.id.nav_third_fragment: //multi-select list (add appointments to schedule by legislator)
                mainHeaderTextview.setText(R.string.third_fragment_title);
                //sort apptList by legislator last name, legislator last name
                apptList.sortApptsByName();
                //third frag will call back for adapter
                //changes made in 3rd frag will be updated in the affected appointment's isScheduled
                //attribute immediately via a method call from the fragment to main activity
                fragmentClass = ThirdFragment.class;
                try {
                    fragment = ThirdFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activeFragment = 3;
                break;
            case R.id.nav_fourth_fragment: //multi-select list (add appointments to schedule by team)
                mainHeaderTextview.setText(R.string.fourth_fragment_title);
                //on fourth frag close, send SparseBooleanArray back to main & sync with
                // ApptList.item.isScheduled
                fragmentClass = FourthFragment.class;
                try {
                    fragment = FourthFragment.newInstance(numTeams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activeFragment = 4;
                break;
            case R.id.nav_fifth_fragment: //multi-select list (add appointment by time)
                mainHeaderTextview.setText(R.string.fifth_fragment_title);
                apptList.sortApptsByTime();
                //fifth frag will call back for adapter
                //changes made in 5th frag will be updated in the affected appointment's isScheduled
                //attribute immediately via a method call from the fragment to main activity
                p1 = "test: frag 5-param1";
                fragmentClass = FifthFragment.class;
                try {
                    fragment = FifthFragment.newInstance(p1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activeFragment = 5;
                break;
            default:
                p1 = "test: frag 1-param1";
                fragmentClass = FirstFragment.class;
                activeFragment = 1;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    //choose the proper information string for each fragment that shows when the info icon
    //is pressed
    public void showInfoToast(){
        String message = "";
        switch(activeFragment) {
            case 1:
                message = getResources().getString(R.string.first_fragment_info);
                break;
            case 2:
                message = getResources().getString(R.string.second_fragment_info);
                break;
            case 3:
                message = getResources().getString(R.string.third_fragment_info);
                break;
            case 4:
                message = getResources().getString(R.string.fourth_fragment_info);
                break;
            case 5:
                message = getResources().getString(R.string.fifth_fragment_info);
                break;
            default:
                message = "no tips are available at this time";
        }
        LayoutInflater inflater = getLayoutInflater();
        // Inflate the Layout
        View layout = inflater.inflate(R.layout.info_toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout));
        TextView infoText = (TextView) layout.findViewById(R.id.info_text);
        // Set the Text to show in TextView
        infoText.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    //update appointment list by setting isScheduled on all appointments in districts selected by
    //the user in Second Fragment
    @Override
    public void syncSelectedDistricts(SparseBooleanArray sba) {
        Boolean isSelected;
        int district;
        ArrayList<Appointment> list;
        if (isTestOutput) { System.out.println("updating appointments selected by district"); }
        if (isTestOutput) { System.out.println(sba.toString()); }
        for (int i = 0; i < sba.size(); i++) {
            district = sba.keyAt(i)+1; //district number is off by one since array starts at zero
            isSelected = sba.valueAt(i); //value is true = item is selected
            //get a list of all appointments associated with that district
            list = apptList.getAppointmentsByDistrict(district);
            if (list != null ) {
                if (isTestOutput) { System.out.println("district " + district + " has " + list.size() + " scheduled"); }
                for (Appointment appt : list) {
                    if (isTestOutput) { System.out.println("scheduling " + appt.getRepLastName() + ": " + isSelected); }
                    appt.setIsScheduled(isSelected);
                }
                if (isTestOutput) { System.out.println("list: " + list.toString()); }
                if (isTestOutput) { System.out.println("appointments: "); }
                if (isTestOutput) { apptList.printAppointments(); }
            }
            else if (isTestOutput) { System.out.println("district " + district + " has no appointments scheduled"); }
        }
    }

    //create an array indicating selected teams to send to Second Fragment on callback so the
    //listview can show selected teams with the proper highlighting
    @Override
    public SparseBooleanArray getSBAForSelectedDistricts() {
        SparseBooleanArray sba = new SparseBooleanArray();
        ArrayList<Appointment> list;
        int count = 0;
        for (int i = 1; i <= numDistricts; i++) {
            //get a list of all appointments associated with that team
            list = apptList.getAppointmentsByDistrict(i);
            if (list != null ) {
                count = 0;
                for (Appointment appt : list) {
                    if (appt.getIsScheduled()) { count++; }
                }
                //if all appointments are isScheduled=true
                if (list.size() == count) { sba.put((i),true); }
                System.out.println("district " + i + " has " + list.size() + " appointments, "
                        + count + " are scheduled");
                System.out.println(list.toString());
            }
            else System.out.println("district " + i + " has no appointments scheduled");
        }
        System.out.println("district SBA = " + sba.toString());
        return sba;
    }

    //update appointment list by setting isScheduled on all appointments in districts selected by
    //the user in Fourth Fragment
    @Override
    public void syncSelectedTeams(SparseBooleanArray sba) {
        Boolean isSelected;
        int team;
        ArrayList<Appointment> list;
        if (isTestOutput) { System.out.println("updating appointments selected by team"); }
        if (isTestOutput) { System.out.println(sba.toString()); }
        for (int i = 0; i < sba.size(); i++) {
            team = sba.keyAt(i)+1; //team number is off by one since array starts at zero
            isSelected = sba.valueAt(i); //value is true = item is selected
            //get a list of all appointments associated with that team
            list = apptList.getAppointmentsByTeam(team);
            if (list != null ) {
                if (isTestOutput) { System.out.println("team " + team + " has " + list.size() + " scheduled"); }
                for (Appointment appt : list) {
                    if (isTestOutput) { System.out.println("scheduling " + appt.getRepLastName() + ": " + isSelected); }
                    appt.setIsScheduled(isSelected);
                }
                if (isTestOutput) { System.out.println("list: " + list.toString()); }
                if (isTestOutput) { System.out.println("appointments: "); }
                if (isTestOutput) { apptList.printAppointments(); }
            }
            else if (isTestOutput) { System.out.println("team " + team + " has no appointments scheduled"); }
        }
    } //end syncSelectedTeams

    //create an array indicating selected teams to send to Fourth Fragment on callback so the
    //listview can show selected teams with the proper highlighting
    @Override
    public SparseBooleanArray getSBAForSelectedTeams() {
        SparseBooleanArray sba = new SparseBooleanArray();
        ArrayList<Appointment> list;
        int count = 0;
        for (int i = 1; i <= numTeams; i++) {
            //get a list of all appointments associated with that team
            list = apptList.getAppointmentsByTeam(i);
            if (list != null ) {
                count = 0;
                for (Appointment appt : list) {
                    if (appt.getIsScheduled()) { count++; }
                }
                //if all appointments are isScheduled=true
                if (list.size() == count) { sba.put((i),true); }
                if (isTestOutput) {
                    System.out.println("team " + i + " has " + list.size() + " appointments, "
                            + count + " are scheduled");
                    System.out.println(list.toString());
                }
            }
            else if (isTestOutput) { System.out.println("team " + i + " has no appointments scheduled"); }
        }
        if (isTestOutput) { System.out.println("team SBA = " + sba.toString()); }
        return sba;
    } //end getSBAForSelectedTeams

    // create an array indicating selected appointments to send to Third and Fifth Fragments
    // on callback
    @Override
    public void getSBAForSelectedAppts(SparseBooleanArray fragSBA) {
        Appointment appt;
        for (int i = 0; i < apptList.getApptCount(); i++) {
            appt = apptList.getAppointment(i);
            if (appt.getIsScheduled()) {
                fragSBA.append(i, true); //append(key, value)
            }
        }
    }

    // restore list from saved or create new
    ApptList restoreList() {
        //TODO logic to restore from saved file
        if (isUseTestData) {
            apptList = new ApptList(YEAR, MONTH-1, DAY);
            apptList.fillTestData();
        }
        else {
            // call async task to do http request and parse json
            new HttpGetJsonAppointments(this).execute();
        }
        return apptList;
    }

    void initializeSchedule() {
        // set Lobby Day start and end times in Calendar objects
        startDate = apptList.setTime(startHour24, startMinute); //9 am
        endDate = apptList.setTime(endHour24, endMinute); //4 pm
        syncSchedule();
    } // end initializeSchedule

    //Create a schedule to display in First Fragment. A schedule has a slot for each time, whether
    //the timeslot has
    void syncSchedule() {
        Appointment appt;
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, MMM dd, yyyy");
        GregorianCalendar acal = new GregorianCalendar();
        GregorianCalendar ccal = new GregorianCalendar();
        SparseBooleanArray sbArray = new SparseBooleanArray();

        apptList.sortApptsByTime();
        long currentTime = startDate.getTimeInMillis();
        long endTime = endDate.getTimeInMillis();
        schedule.clearApppointmentList(); //remove old list to prepare for rebuilding
        Iterator<Appointment> it = apptList.getAppointments().iterator();
        if (isTestOutput) { System.out.println("synchronizing schedule"); }
        //Put each scheduled appointment onto the schedule with empty appointments in between to
        //indicate unfilled appointment slots. This method is preferred over modeling the schedule
        //as an array (actually ArrayList since multiple appointments in a single slot are allowed
        //since an individual may try to run between appointments to catch part of each) of available
        //timeslots because there is occasionally an odd appointment like 1:10 instead of 1:00 or 1:15.
        while (currentTime < endTime ) {
            while (it.hasNext()) {
                appt = it.next();
                if (appt.getIsScheduled()) {
                    acal.setTimeInMillis(appt.getStartTimeLong());
                    ccal.setTimeInMillis(currentTime);
                    if(isTestOutput) { System.out.println("appt time:" + sdf.format(acal.getTime())
                            + ", currentTime: " + sdf.format(ccal.getTime())); }
                    while (appt.getStartTimeLong() > currentTime ) {
                        acal.setTimeInMillis(appt.getStartTimeLong());
                        ccal.setTimeInMillis(currentTime);
                        if(isTestOutput) { System.out.println("appt time (" + sdf.format(acal.getTime())
                                + ") > currentTime (" + sdf.format(ccal.getTime()) + ")"); }
                        schedule.addAppointment(new Appointment(currentTime));
                        currentTime += apptDurationMinutes*60*1000; //to milliseconds
                    }
                    if (isTestOutput) { System.out.println("adding appt with " + appt.getRepLastName()
                            + "to schedule"); }
                    schedule.addAppointment(appt);
                    if(isTestOutput) { System.out.println("updating currentTime using appt time"); }
                    currentTime = appt.getStartTimeLong() + apptDurationMinutes*60*1000;;
                } //end if isScheduled
            } //end it.hasNext
            //fill in the rest of the empty appointments
            currentTime += apptDurationMinutes*60*1000; //to milliseconds
            schedule.addAppointment(new Appointment(currentTime));
            if(isTestOutput) { System.out.println("adding placeholder to schedule"); }
        } //end while currentTime < endTime
        if (isTestOutput) { System.out.println("schedule: "); schedule.printAppointments(); }
    } //end syncSchedule

    //called from fragments
    public ApptListAdapter getApptListAdapter() {
        return apptListAdapter;
    }

    public ApptList getSchedule() {
        //recreate schedule based on current state of appointment list (First Fragment will request
        // it in callback)
        syncSchedule();
        return schedule;
    }

    public void toggleIsApptScheduled(int position) {
        Boolean isScheduled = apptList.getAppointment(position).getIsScheduled();
        if (isTestOutput) System.out.println("appt with "
                + apptList.getAppointment(position).getRepLastName()
                + " is " + isScheduled);
        apptList.getAppointment(position).setIsScheduled(!isScheduled);
        if (isTestOutput) System.out.println("\tappt with "
                + apptList.getAppointment(position).getRepLastName()
                + " is now " + apptList.getAppointment(position).getIsScheduled());
    }

    //Asynchronous task class to get json by making HTTP call on a background thread
    private class HttpGetJsonAppointments extends AsyncTask<Void, Void, Void> {
        //list to temporarily hold incoming appointments incoming in json from http request
        ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
        Context context;

        HttpGetJsonAppointments(Context context) {
            this.context = context;
        }
        //show a progress dialog before making actual http call.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            apptPDialog = new ProgressDialog(MainActivity.this);
            apptPDialog.setMessage("Please wait for appointments to load ...");
            apptPDialog.setCancelable(false);
            apptPDialog.show();

        } //end onPreExecute

        //call the makeServiceCall() method to get the json from apptsUrl, parse the JSON and add it
        //to a HashMap to show the results in the List View.
        @Override
        protected Void doInBackground(Void... arg0) {

            Appointment.Chamber chamber;

            // create an instance of the service handler class
            ServiceHandler sh = new ServiceHandler();

            // make a GET request to apptsUrl and put the response into a string
            String jsonStr = sh.makeServiceCall(apptUrl, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // get the JSON array node
                    appointments = jsonObj.getJSONArray(TAG_APPOINTMENTS);

                    // loop through all Appointments in the JSON array node, extracting the fields
                    // by using their tags
                    for (int i = 0; i < appointments.length(); i++) {
                        JSONObject c = appointments.getJSONObject(i);

                        int id = c.getInt(TAG_ID);
                        Long startTime = c.getLong(TAG_START_TIME);
                        int district = c.getInt(TAG_DISTRICT);
                        String chamberString = c.getString(TAG_CHAMBER);
                        if(chamberString.equalsIgnoreCase("house")) { chamber = Appointment.Chamber.HOUSE; }
                        else { chamber = Appointment.Chamber.SENATE; }
                        String repFirstName = c.getString(TAG_REP__FIRST_NAME);
                        String repLastName = c.getString(TAG_REP_LAST_NAME);
                        int team = c.getInt(TAG_TEAM);
                        String location = c.getString(TAG_LOCATION);
                        Boolean isScheduled = c.getBoolean(TAG_IS_SCHEDULED);

                        Appointment tempAppt =
                                new Appointment(id, startTime, chamber, district, repFirstName,
                                        repLastName, team, location, isScheduled);
                        appointmentList.add(tempAppt);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the apptsUrl");
            }

            return null;
        } //end doInBackground

        // create a list adapter and assign it to the list view
        // also dismiss the progress dialog
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (apptPDialog.isShowing())
                apptPDialog.dismiss();
            if (isTestOutput) {
                for (Appointment appt: appointmentList) {
                    System.out.println(appt.toString());
                }
            }
            apptList = new ApptList(YEAR, MONTH-1, DAY, appointmentList);
            if (isTestOutput) {
                System.out.println("internal appointment list");
                apptList.printAppointments();
            }

            //initialize schedule
            schedule = new ApptList(YEAR, MONTH-1, DAY);
            initializeSchedule();

            //instantiate adapter for list of appointments
            apptListAdapter = new ApptListAdapter(context, apptList);

            //set first fragment as startup fragment
            Fragment fragment = null;
            try {
                fragment = FirstFragment.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            activeFragment = 1;
        } //end onPostExecute

    } //end inner class HttpGetJsonAppointments

} //end MainActivity