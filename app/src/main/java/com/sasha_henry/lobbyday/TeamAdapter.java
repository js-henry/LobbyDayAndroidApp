/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 *
 * Date: 25 Oct, 2015, modified 17 Nov, 2015
 *
 * This class creates a custom adapter by extending BaseAdapter. It uses a simple array of Strings
 * for data and "list_item_team" layout for row item. The layout uses the zebra selectors for background color.
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
import android.widget.TextView;
import java.util.ArrayList;

public class TeamAdapter extends BaseAdapter {
    private Boolean isTestOutput = false;

    private LayoutInflater mInflater;
    private ArrayList<String> teamList;
    private Context mContext;

    public TeamAdapter(Context context, ArrayList<String> teamList) {
        mInflater = LayoutInflater.from(context);
        this.teamList = teamList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public String getItem(int position) {
        return teamList.get(position);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_team, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.list_item_team_text1);
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

        String s = teamList.get(position);
        holder.name.setText(s);

        return view;
    } //end getView

    private class ViewHolder {
        public TextView name;
    }

} //end TeamListAdapter
