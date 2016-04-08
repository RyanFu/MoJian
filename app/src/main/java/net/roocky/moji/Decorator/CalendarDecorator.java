package net.roocky.moji.Decorator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Moji;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 04/08.
 * Calendar的装饰器
 */
public class CalendarDecorator implements DayViewDecorator {
    private List<CalendarDay> dayList = new ArrayList<>();

    public CalendarDecorator(List<CalendarDay> dayList) {
        this.dayList = dayList;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (dayList.contains(day)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, 0xff9e9e9e));
    }
}
