package com.example.qrstaff;

import android.os.Build;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, String date) {
        this.color = color;
        this.dates = new HashSet<>();
        // Parse the date string to LocalDate and then create CalendarDay instances
        DateTimeFormatter formatter = null; // Adjust format as needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.parse(date, formatter);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dates.add(CalendarDay.from(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()));
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(10, color)); // Adds a dot under the date
    }
}