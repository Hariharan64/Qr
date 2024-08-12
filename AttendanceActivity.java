package com.example.qrstaff;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {


    private CalendarView calendarView;
    private Button buttonRecordDate;
    private long selectedDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        calendarView = findViewById(R.id.calendarView);
        buttonRecordDate = findViewById(R.id.buttonRecordDate);

        // Set up the CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Optionally handle date changes
            }
        });

        // Button click listener
        buttonRecordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordCurrentDate();
            }
        });
    }

    private void recordCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        selectedDateMillis = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(calendar.getTime());

        // Update CalendarView to show the current date with a color change (note: CalendarView doesn’t support custom colors directly)
        // For custom color changes, you might need a custom view or third-party library.

        // Display the selected date as a log or Toast (you can modify this part based on your requirement)
        System.out.println("Recorded Date: " + date);
    }
}



