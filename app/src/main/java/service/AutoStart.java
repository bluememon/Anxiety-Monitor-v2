package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Guillermo Uribe on 18/12/2015.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("autoStart", "entrando autostart");
        // Daily Notifications
        context.startService(new Intent(context, dasa_notifications.class));

        // Final Notification
        context.startService(new Intent(context, final_notifications.class));
    }
}
