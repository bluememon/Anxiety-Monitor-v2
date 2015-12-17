package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.bluecoreservices.anxietymonitor2.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dasa_calendar extends Fragment {
    public final static String PAGINA_DEBUG = "add_dasa";

    View view;

    public CalendarView calendario;

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

        calendario = (CalendarView) view.findViewById(R.id.calendarView);
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaSeleccionada =  fecha.format(new Date(calendario.getDate()));

        Log.i(PAGINA_DEBUG, fechaSeleccionada);

        return view;
    }

}
