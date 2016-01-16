package layout;


import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecoreservices.anxietymonitor.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class dasa_calendar extends Fragment {
    public final static String PAGINA_DEBUG = "add_dasa";

    public View view;
    public OnDateSelectedListener listener;

    public MaterialCalendarView calendario;
    public Calendar calendar;

    public dasa_calendar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dasa_calendar, container, false);

        calendario = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        initializeCalendar();

        return view;
    }

    private void initializeCalendar() {

        calendario.setOnDateChangedListener(listener);
        calendario.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar calendar = Calendar.getInstance();
        calendario.setSelectedDate(calendar.getTime());

        /*calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        calendario.setMinimumDate(calendar.getTime());

        calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        calendario.setMaximumDate(calendar.getTime());*/

        calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);

        OneDayDecorator diaDecor = new OneDayDecorator();

        ArrayList<CalendarDay> dates = new ArrayList<>();
        CalendarDay cal = CalendarDay.from(calendar);

        dates.add(cal);


        //calendario.addDecorator(new EventDecorator(0xffcccccc, dates));
        diaDecor.setDate(new Date(calendar.getTimeInMillis()));
        calendario.addDecorator(diaDecor);
    }

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //view.addSpan(new StyleSpan(Typeface.BOLD));
            //view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));

            Drawable myImage;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImage = getContext().getResources().getDrawable(R.drawable.ic_circle);
            } else {
                myImage = getContext().getResources().getDrawable(R.drawable.ic_circle);
            }
            myImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

            view.setBackgroundDrawable(myImage);

            //myImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            //view.addSpan(new DotSpan(Color.GRAY));
        }

        /**
         * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
         */
        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }
}
