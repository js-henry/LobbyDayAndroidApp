/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 * Date: 25 Oct, 2015, modified 23 Nov, 2015
 *
 * Activities that contain this fragment must implement the SecondFragment.SecondFragmentListener
 * interface to handle interaction events.
 *
 * This class represents clickable grid (add appointments to schedule by district). Updating the
 * main list based on the selections is expensive, so no updates are performed until the user
 * begins the process of leaving the page. At that point,  a method in the main activity is called
 * to synchronized the isScheduled status of the appointments based on the selections made in this
 * fragment.
 */

package com.sasha_henry.lobbyday;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;

// For backwards compatibility, this class should extend android.support.v4.app.Fragment
// (not android.app.Fragment).
public class SecondFragment extends Fragment {
    Boolean isTestOutput = false;
    private static final String ARG_NUMDISTRICTS = "numDistricts";
    private int numDistricts;

    private GridView secondFragGridView;
    private ArrayList<String> districtList;
    private GridAdapter gridAdapter;

    private SecondFragmentListener mListener;

    public SecondFragment() {
        // Required empty public constructor
    }

    //factory method to create a new instance of second fragment using the provided parameters.
    public static SecondFragment newInstance(int numDistricts) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMDISTRICTS, numDistricts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numDistricts = getArguments().getInt(ARG_NUMDISTRICTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        secondFragGridView = (GridView) view.findViewById(R.id.second_frag_gridview);

        //create a list of district numbers
        districtList = new ArrayList<>();
        for (int i = 1; i < (numDistricts+1); i++) {
            districtList.add(((Integer) i).toString());
        }

        //instantiate adapter with data list
        gridAdapter = new GridAdapter(getContext(), districtList);

        //attach adapter to listview
        secondFragGridView.setAdapter(gridAdapter);

        secondFragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (isTestOutput) {
                    System.out.println("selected: " + v.isSelected() + ", activated: " + v.isActivated()
                            + ", pressed: " + v.isPressed());
                }
                clickResponse(position);
            }
        });

        //bring gridview's selected-items list up to date with preselected teams so districts
        //whose appointments are all selected show the proper highlighting on startup
        SparseBooleanArray startupSelectedList = mListener.getSBAForSelectedDistricts();
        SparseBooleanArray checkedItemPositions = secondFragGridView.getCheckedItemPositions();
        for (int i = 0; i < startupSelectedList.size(); i++) {
            checkedItemPositions.append(
                    (startupSelectedList.keyAt(i)-1), //district i is in listview item i-1
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
            mListener = (SecondFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SecondFragmentListener");
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
        if (isTestOutput) { System.out.println("onPause activated in SecondFragment. sync called"); }
        if (mListener != null) {
            //use checkedItemPositions array to update selected items in Main Activity
            mListener.syncSelectedDistricts(secondFragGridView.getCheckedItemPositions());
        }
        if (isTestOutput) { System.out.println("onPause completed in SecondFragment"); }
        super.onPause();
    }

    // Activities that communicate with this fragment must implement the following interface. This
    // ensures the activity will provide the methods called by the fragment.
    public interface SecondFragmentListener { //formerly: public interface OnFragmentInteractionListener {
        //use the information in this fragment to update the information in the Main Activity
        public void syncSelectedDistricts(SparseBooleanArray sba);
        //get list of selected districts from Main Activity
        public SparseBooleanArray getSBAForSelectedDistricts();

    }

    //action to perform when listview item is selected
    void clickResponse(int position) {
//        long[] idArray = listview.getCheckedItemIds(); //can't use: API is too recent for min version in specs
//        int idCount = listview.getCheckedItemCount(); //can't use: API is too recent for min version in specs
        SparseBooleanArray idBArray = secondFragGridView.getCheckedItemPositions();
        if (isTestOutput) { System.out.println(idBArray.toString()); }
        String outputString = "";
        for (int i = 0; i < idBArray.size(); i++) {
            if (isTestOutput) { System.out.println("i = " + i + ", key = " + idBArray.keyAt(i)
                    + ", value = " + idBArray.valueAt(i)); }
            if (idBArray.valueAt(i)) { //value is true = item is selected
                if (i > 0) outputString += ", ";
                if (isTestOutput) { System.out.println("adapter item at key: "
                        + gridAdapter.getItem(idBArray.keyAt(i))); }
                outputString += gridAdapter.getItem(idBArray.keyAt(i));
            }
        }
    } //end clickResponse

}