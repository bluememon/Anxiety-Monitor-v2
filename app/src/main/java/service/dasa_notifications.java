package service;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bluecoreservices.anxietymonitor2.R;
import com.bluecoreservices.anxietymonitor2.add_dasa;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by Guillermo Uribe on 18/12/2015.
 */
public class dasa_notifications extends Service {
    public final static String PAGINA_DEBUG = "dasa_notification";
    SharedPreferences sharedPref;

    Calendar calendarInicio;
    Calendar calendarFin;
    Calendar calendar;

    String idPaciente;
    String type;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies
        Log.e(PAGINA_DEBUG, "arranca");
        sharedPref = getSharedPreferences("userPref", 0);

        calendarInicio = Calendar.getInstance();
        calendarInicio.setTimeInMillis(System.currentTimeMillis());
        calendarInicio.set(Calendar.HOUR_OF_DAY, 20);
        calendarInicio.set(Calendar.MINUTE, 00);

        calendarFin = Calendar.getInstance();
        calendarFin.setTimeInMillis(System.currentTimeMillis());
        calendarFin.set(Calendar.HOUR_OF_DAY, 24);
        calendarFin.set(Calendar.MINUTE, 00);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (calendar.compareTo(calendarInicio) > 0 && calendar.compareTo(calendarFin) < 0) {
            if (sharedPref.getString("logged", null) != null) {
                Log.e(PAGINA_DEBUG, "logeado");
                idPaciente = sharedPref.getString("userId", "");
                type = sharedPref.getString("type", "");

                if (type.equals("3")) {
                    Log.e(PAGINA_DEBUG, "checando dasa");
                    checkDasa(idPaciente);
                }
            }
        }

        stopSelf();

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        return START_NOT_STICKY;
    }

    public void showNotification(){
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, add_dasa.class), PendingIntent.FLAG_UPDATE_CURRENT);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] {1000, 500, 250, 500 })
                .setLights(0xffffff, 3000, 3000)
                .setSmallIcon(R.drawable.ic_comment_24dp)
                .setContentTitle("Anxiety Monitor")
                .setContentText("Recordatorio para llenar el diario")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.am_alert_logo);

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
        //Else i will restart the service until 8 o clock in the evening
        else {
            if (calendar.compareTo(calendarInicio) < 0 && calendar.compareTo(calendarFin) < 0){
                alarm.set(AlarmManager.RTC_WAKEUP,
                        calendarInicio.getTimeInMillis(),
                        PendingIntent.getService(this, 0, new Intent(this, dasa_notifications.class), 0)
                );
            }
        }
    }

    private void checkDasa(String idPaciente) {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private final String url = "http://app.bluecoreservices.com/webservices/checkDasa.php";

            String charset = "UTF-8";
            HttpURLConnection conn;
            DataOutputStream wr;
            StringBuilder result = new StringBuilder();
            URL urlObj;
            JSONObject jObj = null;
            StringBuilder sbParams;
            String paramsString;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String idPac = params[0];

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(idPac, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    urlObj = new URL(url);

                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", charset);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);

                    conn.connect();

                    paramsString = sbParams.toString();

                    wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(paramsString);
                    wr.flush();
                    wr.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //response from the server
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.disconnect();

                String stringResult = result.toString().trim();

                try {
                    jObj = new JSONObject(stringResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return jObj;
            }


            @Override
            protected void onPostExecute(JSONObject result) {

                Boolean respuesta = false;
                try {
                    respuesta = result.getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (respuesta) {
                    Log.i(PAGINA_DEBUG, "si exsiste registro del diario DASA");

                }
                else {
                    showNotification();
                    Log.i(PAGINA_DEBUG, "no exsiste registro del diario DASA");
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute(idPaciente);
    }
}
