/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 *
 * Date: 25 Oct, 2015, modified Nov, 2015
 *
 * Activities that contain this fragment must implement the FirstFragment.FirstFragmentListener
 * interface to handle interaction events.
 *
 * This class represents plain (non-selectable) list with data gaps (schedule).
 *
 * The fragment automatically saves and restores list view selections during config changes
 * (e. g. change of orientation). It is not necessary to save it manually in onSaveInstanceState()
 * and retrieve it in onViewStateRestored()).
 */

package com.sasha_henry.lobbyday;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.widget.TextView;
import android.widget.Toast;


// For backwards compatibility, this class should extend android.support.v4.app.Fragment
// (not android.app.Fragment).
public class FirstFragment extends Fragment implements GestureDetector.OnGestureListener {
    Boolean isTestOutput = false;

    private GestureDetectorCompat gestureDetector;

    private ListView firstFragListView;
    private ApptListAdapter scheduleAdapter;
    private ApptList schedule;
    private String outputString = "";

    private FirstFragmentListener mListener;

    public FirstFragment() {
        // Required empty public constructor
    }

    //Factory method to create a new instance of first fragment using the provided parameters.
    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firstFragListView = (ListView) view.findViewById(R.id.first_frag_listview);

        //check syntax
        this.gestureDetector = new GestureDetectorCompat(getContext(), this);

        //instantiate adapter with schedule list
        if (isTestOutput) { System.out.println("Fetching schedule in FirstFragment"); }
        scheduleAdapter = new ApptListAdapter(getContext(), mListener.getSchedule());
        //attach adapter to listview
        firstFragListView.setAdapter(scheduleAdapter);

    } //end onViewCreated

    //onAttach(Activity activity) is called by the onAttach(Context context) if there is a host
    // activity. Calling onAttach(Activity activity) directly is deprecated.
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mListener = (FirstFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FirstFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Activities that communicate with this fragment must implement the following interface. This
    // ensures the activity will provide the methods called by the fragment.
    public interface FirstFragmentListener {
        public ApptList getSchedule();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        showInfoToast("onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        showInfoToast("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        showInfoToast("onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        showInfoToast("onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        showInfoToast("onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        showInfoToast("onFling");
        return false;
    }

    //display a toast indicating which gesture has been chosen
    public void showInfoToast(String msg){
//        Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
    }

} //end class FirstFragment