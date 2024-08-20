package com.example.qrstaff;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> dates;
    private final Map<String, String> dateStatusMap;
    private String selectedDate;
    private final String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public CalendarAdapter(Context context, ArrayList<String> dates, Map<String, String> dateStatusMap) {
        this.context = context;
        this.dates = dates;
        this.dateStatusMap = dateStatusMap;
    }

    @Override
    public int getCount() {
        return weekDays.length + dates.size(); // Adding headers and dates
    }

    @Override
    public Object getItem(int position) {
        if (position < weekDays.length) {
            return weekDays[position]; // Return header if position is less than weekDays length
        } else {
            return dates.get(position - weekDays.length); // Return calendar date
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.dateText);

        if (position < weekDays.length) {
            // Display day headers
            textView.setText(weekDays[position]);
            textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            // Display calendar dates
            String date = dates.get(position - weekDays.length);
            textView.setText(date);

            if (date.equals(selectedDate)) {
                convertView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light)); // Highlight selected date
            } else {
                convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }

            if (dateStatusMap.containsKey(date)) {
                String status = dateStatusMap.get(date);
                if ("punch_in".equals(status)) {
                    textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                } else if ("punch_out".equals(status)) {
                    textView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                }
            } else {
                textView.setTextColor(context.getResources().getColor(android.R.color.black));
            }
        }

        return convertView;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    public void setHighlightedDate(String date) {
        // Implementation for highlighting a date in the calendar
        notifyDataSetChanged();
    }
}
