package sg.edu.np.mad.recipeheist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AlarmBootReceiver extends BroadcastReceiver {
    private Context myContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        myContext = context;

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //load settings
            SharedPreferences settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
            String frequency = settingSharedPreferences.getString("updatefrequency", "0");
            if (frequency != "0") {
                switch (frequency) {
                    case "1":
                        setAlarm(1);
                        break;

                    case "2":
                        setAlarm(2);

                        break;

                    case "3":
                        setAlarm(3);
                        break;

                    case "6":
                        setAlarm(6);
                        break;

                    case "12":
                        setAlarm(12);
                        break;

                    case "24":
                        setAlarm(24);
                        break;

                    case "48":
                        setAlarm(48);
                        break;

                    case "168":
                        setAlarm(168);
                        break;
                }
            }
        }
    }
    public void setAlarm(int numhours) {
        Calendar calendar = Calendar.getInstance();
        Intent intent0 = new Intent(myContext, UpdateService.class);
        PendingIntent pintent = PendingIntent.getService(myContext, 0, intent0, 0);
        AlarmManager alarm = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), numhours * AlarmManager.INTERVAL_HOUR, pintent);
    }
}

