package service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.bluecoreservices.anxietymonitor2.R;

import java.util.Calendar;

/**
 * Created by Guillermo Uribe on 18/12/2015.
 */
public class dasa_notifications extends Service {
    public final static String PAGINA_DEBUG = "dasa_notification";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    Calendar calendarInicio;
    Calendar calendarFin;
    Calendar calendar;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies
        Log.e(PAGINA_DEBUG, "arranca");

        calendarInicio = Calendar.getInstance();
        calendarInicio.setTimeInMillis(System.currentTimeMillis());
        calendarInicio.set(Calendar.HOUR_OF_DAY, 20);
        calendarInicio.set(Calendar.MINUTE, 00);

        calendarFin = Calendar.getInstance();
        calendarFin.setTimeInMillis(System.currentTimeMillis());
        calendarFin.set(Calendar.HOUR_OF_DAY, 24);
        calendarFin.set(Calendar.MINUTE, 00);

        showNotification();
        stopSelf();

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        return START_NOT_STICKY;
    }

    public void showNotification(){
        Log.e(PAGINA_DEBUG, "Notificacion");
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setVibrate(new long[] {1000, 1000, 500, 500 })
                .setLights(0xffffff, 3000, 3000)
                .setSmallIcon(R.drawable.ic_comment_24dp)
                .setContentTitle("Anxiety Monitor")
                .setContentText("Recordatorio para llenar el diario");

        int mNotificationId = 001;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        // I want to restart this service again in one hour
        if (calendar.compareTo(calendarInicio) > 0 && calendar.compareTo(calendarFin) < 0) {

            alarm.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis() + (1000 * 60 * 10),
                    PendingIntent.getService(this, 0, new Intent(this, dasa_notifications.class), 0)
            );
        }
        else {
            if (calendar.compareTo(calendarInicio) < 0 && calendar.compareTo(calendarFin) < 0){
                alarm.set(AlarmManager.RTC_WAKEUP,
                        calendarInicio.getTimeInMillis(),
                        PendingIntent.getService(this, 0, new Intent(this, dasa_notifications.class), 0)
                );
            }
        }
    }

}
