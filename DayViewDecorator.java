package com.example.qrstaff;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public abstract class DayViewDecorator implements com.prolificinteractive.materialcalendarview.DayViewDecorator {
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {

    }

    public abstract void decorate(com.example.qrstaff.DayViewFacade view);
}
