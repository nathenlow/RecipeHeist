package sg.edu.np.mad.recipeheist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.preference.AndroidResources;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmBootReceiver extends BroadcastReceiver {
    private Context myContext;
    private Intent myIntent;
    DateFormat dateFormat;
    private static final String SHARED_PREFS = "settings";
    private static final String LAST_AUTO_UPDATE = "lastSaveDate";
    public static final long HOUR = 3600*1000;


    @Override
    public void onReceive(Context context, Intent intent) {
        myContext = context;
        myIntent = intent;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
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
        //get date and find difference
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        long difference_In_Time = date.getTime() - loaddata().getTime();
        long difference_In_Hours = TimeUnit.MILLISECONDS.toHours(difference_In_Time);
        if (difference_In_Hours >= numhours){
            //save auto update date
            savedata(date);
            //if it is the first auto update since system power on --> set repeating alarm
            if (myIntent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent intent0 = new Intent(myContext, AlarmBootReceiver.class);
                PendingIntent pintent = PendingIntent.getService(myContext, 0, intent0, 0);
                AlarmManager alarm = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), numhours * AlarmManager.INTERVAL_HOUR, pintent);
            }
            //Start Service
            Intent updateintent = new Intent(myContext, UpdateService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myContext.startForegroundService(updateintent);
            }
            else {
                myContext.startService(updateintent);
            }
        }
        else {
            Intent intent0 = new Intent(myContext, AlarmBootReceiver.class).setAction("android.intent.action.BOOT_COMPLETED");
            PendingIntent pintent = PendingIntent.getService(myContext, 0, intent0, 0);
            AlarmManager alarm = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
            Date newDate = new Date(loaddata().getTime() + 2 * HOUR);
            long timetotrigger = TimeUnit.MILLISECONDS.toMillis(newDate.getTime());
            alarm.setExact(AlarmManager.RTC, timetotrigger, pintent);
        }
    }

    //-----SharedPreferences methods
    public void savedata(Date date) {
        String strDate = dateFormat.format(date);
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_AUTO_UPDATE, strDate);
        editor.apply();
    }

    public Date loaddata() {
        Date date = new Date();
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String strDate = sharedPreferences.getString(LAST_AUTO_UPDATE, "2000-01-01 01:01:01");
        try {
            date = dateFormat.parse(strDate);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return date;
    }

}

