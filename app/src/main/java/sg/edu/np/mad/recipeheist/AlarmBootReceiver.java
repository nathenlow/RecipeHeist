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
    DateFormat dateFormat;
    private static final String SHARED_PREFS = "settings";
    private static final String LAST_AUTO_UPDATE = "lastSaveDate";

    @Override
    public void onReceive(Context context, Intent intent) {
        myContext = context;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //check if user login
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            //load settings
            SharedPreferences settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
            String frequency = settingSharedPreferences.getString("updatefrequency", "0");

            if (!frequency.equals("0")) {
                int numf = Integer.parseInt(frequency);
                setAlarm(numf);
            }
        }
    }

    public void setAlarm(int numhours) {
        //get date and find difference
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        long difference_In_Time = date.getTime() - loaddata().getTime();
        long difference_In_Hours = TimeUnit.MILLISECONDS.toHours(difference_In_Time);

        //set alarm details
        Intent intent0 = new Intent(myContext, UpdateService.class).setAction("AUTO_UPDATE");
        PendingIntent pintent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pintent = PendingIntent.getForegroundService(myContext, 0, intent0, 0);
        } else {
            pintent = PendingIntent.getService(myContext, 0, intent0, 0);
        }
        AlarmManager alarm = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);

        //if the suppose update time had already pass, set update time to now
        if (difference_In_Hours >= numhours){
            alarm.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), numhours * AlarmManager.INTERVAL_HOUR, pintent);
        }
        else {
            Date newDate = new Date(loaddata().getTime() + numhours * AlarmManager.INTERVAL_HOUR);
            long timetotrigger = TimeUnit.MILLISECONDS.toMillis(newDate.getTime());
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, timetotrigger, numhours * AlarmManager.INTERVAL_HOUR, pintent);
        }
    }

    //get last auto update date
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

