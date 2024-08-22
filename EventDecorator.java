package com.example.qrstaff;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;

public class EventDecorator implements DayViewDecorator {
    private final Drawable drawable;
    private final Collection<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.drawable = new ColorDrawable(color);
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}