/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 *
 * Date: 25 Oct, 2015, modified 23 Nov, 2015
 *
 * Activities that contain this fragment must implement the FourthFragment.FourthFragmentListener
 * interface to handle interaction events.
 *
 * This class represents a simple multi-select list (add appointments to schedule by team).
 *
 * This class represents clickable grid (add appointments to schedule by district). Updating the
 * main list based on the selections is expensive, so no updates are performed until the user
 * begins the process of leaving the page. At that point,  a method in the main activity is called
 * to synchronized the isScheduled status of the appointments based on the selections made in this
 * fragment.
 *
 * The fragment automatically saves and restores list view selections during config changes
 * (e. g. change of orientation). It is not necessary to save it manually in onSaveInstanceState()
 * and retrieve it in onViewStateRestored()).
 */

package com.sasha_henry.lobbyday;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

// For backwards compatibility, this class should extend android.support.v4.app.Fragment
// (not android.app.Fragment).
public class FourthFragment extends Fragment {

    Boolean isTestOutput = false;
    private static final String ARG_NUMTEAMS = "numTeams";
    private int numTeams;
    private ListView fourthFragListView;
    private TeamAdapter teamAdapter;
    private ArrayList<String> teamList;
    private String outputString = "";

    private FourthFragmentListener mListener;

    public FourthFragment() {
        // Required empty public constructor
    }

    //factory method to create a new instance of second fragment using the provided parameters.
    public static FourthFragment newInstance(int numTeams) {
        FourthFragment fragment = new FourthFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMTEAMS, numTeams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numTeams = getArguments().getInt(ARG_NUMTEAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fourth, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fourthFragListView = (ListView) view.findViewById(R.id.fourth_frag_listview);

        //create a dummy team list - in the production app, this list would be provided by the
        //webserver
        teamList = new ArrayList<>();
        String teamInfo;
        for (int i = 1; i < (numTeams+1); i++) {
            teamInfo = "Team " + i + "  --  Teamlead" + i;
            teamList.add(teamInfo);
        }

        //instantiate adapter with data list
        teamAdapter = new TeamAdapter(getContext(), teamList);

        //attach adapter to listview
        fourthFragListView.setAdapter(teamAdapter);

        fourthFragListView.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //handle response in separate method
                        clickResponse(position);
                    }
                }
        );

        //bring listview's selected-items list up to date with preselected teams so teams
        //whose appointments are all selected show the proper highlighting on startup
        SparseBooleanArray startupSelectedList = mListener.getSBAForSelectedTeams();
        SparseBooleanArray checkedItemPositions = fourthFragListView.getCheckedItemPositions();
        for (int i = 0; i < startupSelectedList.size(); i++) {
            checkedItemPositions.append(
                    (startupSelectedList.keyAt(i)-1), //team i is in listview item i-1
                    startupSelectedList.valueAt(i)
            );
        }

    } //end onViewCreated

    //onAttach(Activity activity) is called by the onAttach(Context context) if there is a host
    // activity. Calling onAttach(Activity activity) directly is deprecated.
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mListener = (FourthFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FourthFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //onStop() --> onDestroyView() --> onDestroy() --> onDetach() --> fragment is destroyed
    // The system calls onPause() as the first indication that the user is leaving the fragment
    // (though it does not always mean the fragment is being destroyed). This is usually where you
    // should commit any changes that should be persisted beyond the current user session (because
    // the user might not come back).
    @Override
    public void onPause() {
        if (isTestOutput) { System.out.println("onPause activated. sync called"); }
        if (mListener != null) {
            //use checkedItemPositions array to update selected items in Main Activity
            mListener.syncSelectedTeams(fourthFragListView.getCheckedItemPositions());
        }
        super.onPause();
    }

    // Activities that communicate with this fragment must implement the following interface. This
    // ensures the activity will provide the methods called by the fragment.
    public interface FourthFragmentListener { //formerly: public interface OnFragmentInteractionListener {
        //use the information in this fragment to update the information in the Main Activity
        public void syncSelectedTeams(SparseBooleanArray sba);
        //get list of selected teams from Main Activity
        public SparseBooleanArray getSBAForSelectedTeams();
    }

    //action to perform when listview item is selected
    void clickResponse(int position) {
//        long[] idArray = listview.getCheckedItemIds(); //can't use: API is too recent for min version in specs
//        int idCount = listview.getCheckedItemCount(); //can't use: API is too recent for min version in specs
        SparseBooleanArray idBArray = fourthFragListView.getCheckedItemPositions();
        if (isTestOutput) { System.out.println(idBArray.toString()); }
        String outputString = "";
        for (int i = 0; i < idBArray.size(); i++) {
            if (isTestOutput) { System.out.println("i = " + i + ", key = " + idBArray.keyAt(i)
                    + ", value = " + idBArray.valueAt(i)); }
            if (idBArray.valueAt(i)) { //value is true = item is selected
                if (i > 0) outputString += ", ";
                if (isTestOutput) { System.out.println("adapter item at key: "
                        + teamAdapter.getItem(idBArray.keyAt(i))); }
                outputString += teamAdapter.getItem(idBArray.keyAt(i));
            }
        }
    } //end clickResponse

}//end class FourthFragment