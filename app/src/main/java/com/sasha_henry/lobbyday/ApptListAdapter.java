/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 *
 * Date: 25 Oct, 2015, modified 17 Nov, 2015
 *
 * This class creates a custom adapter by extending BaseAdapter. It uses the "Appointment" class for
 * data and "list_item_appt" layout for row item. The layout uses the zebra selectors for background color.
 */

package com.sasha_henry.lobbyday;
/**
 *
 * @author J. Sasha Henry
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApptListAdapter extends BaseAdapter {
    private Boolean isTestOutput = false;

    private LayoutInflater mInflater;
    private ApptList mApptList;
    private Context mContext;

    public ApptListAdapter(Context context, ApptList apptList) {
        mInflater = LayoutInflater.from(context);
        mApptList = apptList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mApptList.getApptCount();
    }

    @Override
    public Appointment getItem(int position) {
        return mApptList.getAppointment(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_appt, parent, false);
            holder = new ViewHolder();
            holder.time = (TextView) view.findViewById(R.id.list_item_appt_text1);
            holder.name = (TextView) view.findViewById(R.id.list_item_appt_text2);
            holder.district = (TextView) view.findViewById(R.id.list_item_appt_text3);
            holder.team = (TextView) view.findViewById(R.id.list_item_appt_text4);
            holder.location = (TextView) view.findViewById(R.id.list_item_appt_text5);

            view.setTag(holder);
        }
        else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        //apply zebra table styling with different color for activated items
        if (position % 2 == 0){
            view.setBackgroundResource(R.drawable.zebraselector1);
        } else {
            view.setBackgroundResource(R.drawable.zebraselector2);
        }

        Appointment appt = mApptList.getAppointment(position);
        holder.time.setText(appt.getStartTimeString());

        //schedule placeholders
        if (appt.getDistrict() < 0) {
            holder.name.setText("");
            holder.district.setText("");
            holder.team.setText("");
            holder.location.setText("");
        }
        else {
            String name = Appointment.getTitle(appt.getChamber()) + " " + appt.getRepLastName()
                    + ", " + appt.getRepFirstName();
            holder.name.setText(name);
            holder.district.setText(((Integer) appt.getDistrict()).toString());
            holder.team.setText(((Integer) appt.getTeam()).toString());
            holder.location.setText(appt.getLocation());
        }

        if (isTestOutput) { System.out.println(appt.getRepLastName() + " is scheduled: " + appt.getIsScheduled()); }
        if (isTestOutput) { System.out.println("view is selected: " + view.isSelected()); }

        return view;
    } //end getView

    private class ViewHolder {
        public ImageView avatar;
        public TextView time, name, district, team, location;
    }

} //end ApptListAdapter
