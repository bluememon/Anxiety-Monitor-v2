package com.bluecoreservices.anxietymonitor2;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Guillermo Uribe on 13/01/2016.
 */
public class anxietyUtils {
    public final static String PAGINA_DEBUG = "anxietyUtils";

    public static class friendlyDate {
        private Calendar cal;
        ArrayList<String> nameOfMonths;

        public friendlyDate(Context context, String origDate) {
            cal = Calendar.getInstance();
            SimpleDateFormat temp = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.US);

            try {
                Date temp2 = temp.parse(origDate);
                cal.setTime(temp2);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            nameOfMonths = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.month_names)));

            //Log.i(PAGINA_DEBUG, "[Mes]" + getLocaleMonth());
        }

        private String getLocaleMonth() {
            return nameOfMonths.get(cal.get(Calendar.MONTH));
        }

        public String getFriendlyDate() {
            return cal.get(Calendar.DAY_OF_MONTH) + " " + getLocaleMonth() + " " + cal.get(Calendar.YEAR);
        }
    }
}
