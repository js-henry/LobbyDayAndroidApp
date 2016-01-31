/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)
 *
 * Date: 25 Oct, 2015, modified 17 Nov, 2015
 *
 * This class creates a custom grid adapter by extending BaseAdapter. It uses a simple list of
 * integers for data and "list_item_district" layout for grid item. Alternating coloration is
 * applied to the grid squares manually.
 */
package com.sasha_henry.lobbyday;
/**
 *
 * @author J. Sasha Henry
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Boolean isTestOutput = false;

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> list;

    // Constructor
    public GridAdapter(Context context, ArrayList<String> list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_district, parent, false);
            holder = new ViewHolder();
            holder.district = (TextView) view.findViewById(R.id.list_item_district_text1);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        //apply zebra table styling with different color for activated items
        if (position % 2 == 0){
            view.setBackgroundResource(R.drawable.zebraselector1);
        } else {
            view.setBackgroundResource(R.drawable.zebraselector2);
        }

        holder.district.setText(list.get(position));

        return view;
    } //end getView

    private class ViewHolder {
        public TextView district;
    }
} //end class GridAdapter