package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecoreservices.anxietymonitor2.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class dasa_calendar extends Fragment {
    public final static String PAGINA_DEBUG = "add_dasa";

    View view;
    OnDateSelectedListener listener;

    public MaterialCalendarView calendario;

    public dasa_calendar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        calendario.setMinimumDate(calendar.getTime());

        calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        calendario.setMaximumDate(calendar.getTime());

        /*int bgColor = sharedVisualElements.getPrimaryColor();
        calendario.addDecorators(new EventDecorator(bgColor, ????));*/
    }

}
