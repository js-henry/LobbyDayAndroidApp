/* Author : Joslyn Sasha Henry
 * Project : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 * Date: 25 Oct, 2015, modified 17 Nov, 2015
 *
 * Activities that contain this fragment must implement the ThirdFragment.ThirdFragmentListener
 * interface to handle interaction events.
 *
 * This fragment handles a multi-select list (add appointments to schedule by name). In order for
 * selected list items to display the custom background color when the fragment is created, the
 * listview's selected items list (selectedArray = fifthFragListView.getCheckedItemPositions())
 * must be manually updated with the list of currently scheduled appointments held by MainActivity.
 *
 * The fragment automatically saves and restores list view selections during config changes
 * (e. g. change of orientation). It is not necessary to save it manually in onSaveInstanceState()
 * and retrieve it in onViewStateRestored()).
 *
 * This class represents clickable grid (add appointments to schedule by time).
 *
 */

package com.sasha_henry.lobbyday;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


// For backwards compatibility, this class should extend android.support.v4.app.Fragment
// (not android.app.Fragment).
public class ThirdFragment extends Fragment {
    private Boolean isTestOutput = false;

    private ListView thirdFragListView;
    private View headerView;
    private String outputString = "";
    private SparseBooleanArray selectedArray;

    private ThirdFragmentListener mListener;

    public ThirdFragment() {
        // Required empty public constructor
    }

    //Factory method to create a new instance of third fragment using the provided parameters.
    public static ThirdFragment newInstance() {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return(inflater.inflate(R.layout.fragment_third, container, false));
    } //end onCreateView

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        thirdFragListView = (ListView) view.findViewById(R.id.third_frag_listview);

        //attach parent activity's appointment list adapter to third fragment's listview
        thirdFragListView.setAdapter(mListener.getApptListAdapter());

        //update listview's selected items list
        selectedArray = thirdFragListView.getCheckedItemPositions();
        if (isTestOutput) { System.out.println(selectedArray.toString()); }
        syncSelected();
        if (isTestOutput) { System.out.println(selectedArray.toString()); }

        thirdFragListView.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //handle response in separate method
                        clickResponse(position);
                    }
                }
        );

    } //end onViewCreated

    //onAttach(Activity activity) is called by the onAttach(Context context) if there is a host
    // activity. Calling onAttach(Activity activity) directly is deprecated.
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mListener = (ThirdFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ThirdFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Activities that communicate with this fragment must implement the following interface. This
    // ensures the activity will provide the methods called by the fragment.
    public interface ThirdFragmentListener { //formerly: public interface OnFragmentInteractionListener {
        public ApptListAdapter getApptListAdapter();
        public void toggleIsApptScheduled(int position);
        public void getSBAForSelectedAppts(SparseBooleanArray fragSBA);
    }

    //action to perform when listview item is selected
    void clickResponse(int position) {
//        long[] idArray = listview.getCheckedItemIds(); //can't use: API is too recent for min version in specs
//        int idCount = listview.getCheckedItemCount(); //can't use: API is too recent for min version in specs
        if (isTestOutput) { System.out.println(selectedArray.toString()); }
        String outputString = "";
        for (int i = 0; i < selectedArray.size(); i++) {
            if (isTestOutput) { System.out.println("i = " + i + ", key = " + selectedArray.keyAt(i)
                    + ", value = " + selectedArray.valueAt(i)); }
            if (selectedArray.valueAt(i)) { //value is true = item is selected
                if (i > 0) outputString += ", ";
                if (isTestOutput) { System.out.println("adapter item at key: " +
                        mListener.getApptListAdapter().getItem(selectedArray.keyAt(i))); }
                outputString += mListener.getApptListAdapter().getItem(selectedArray.keyAt(i));
            }
        }
        mListener.toggleIsApptScheduled(position);
    } //end clickResponse

    void syncSelected () {
        //send fragment listview's array of selected items to main activity for update
        mListener.getSBAForSelectedAppts(selectedArray);
        if (isTestOutput) { System.out.println(selectedArray.toString()); }
    }
} //end ThirdFragment